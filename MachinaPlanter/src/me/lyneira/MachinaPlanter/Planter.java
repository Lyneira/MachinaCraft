package me.lyneira.MachinaPlanter;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.Fuel;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.Machina;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class Planter implements Machina {
    private static int delay = 20;
    static int maxLength = 16;
    static int maxWidth = 10;

    private final Rail rail;
    private final BlockLocation lever;
    private final BlockLocation base;
    private final BlockLocation chest;
    private final BlockLocation furnace;
    private final BlockRotation furnaceYaw;
    private final boolean harvest;
    private State state;

    Planter(Rail rail, BlockLocation lever, BlockLocation base, BlockLocation chest, BlockLocation furnace, BlockRotation furnaceYaw, boolean harvest) {
        this.rail = rail;
        this.lever = lever;
        this.base = base;
        this.chest = chest;
        this.furnace = furnace;
        this.furnaceYaw = furnaceYaw;
        this.harvest = harvest;
        Fuel.setFurnace(furnace.getBlock(), furnaceYaw, true);
        state = activate;
    }

    /**
     * Loads the given configuration.
     * 
     * @param configuration
     */
    static void loadConfiguration(ConfigurationSection configuration) {
        delay = Math.max(configuration.getInt("action-delay", delay), 1);
        maxLength = Math.min(Math.max(configuration.getInt("max-length", maxLength), 1), 64);
        maxWidth = Math.min(Math.max(configuration.getInt("max-width", maxWidth), 1), 64);
    }

    @Override
    public boolean verify(BlockLocation anchor) {
        if (!(anchor.checkType(Blueprint.anchorMaterial) //
                && lever.checkType(Material.LEVER) //
                && base.checkType(Blueprint.baseMaterial) //
                && chest.checkType(Material.CHEST) //
        && furnace.checkTypes(Material.BURNING_FURNACE)))
            return false;

        return rail.verify();
    }

    @Override
    public HeartBeatEvent heartBeat(BlockLocation anchor) {
        state = state.run();
        if (state == null)
            return null;
        return new HeartBeatEvent(delay);
    }

    @Override
    public boolean onLever(BlockLocation anchor, Player player, ItemStack itemInHand) {
        if (player.hasPermission("machinaplanter.activate")) {
            if (state == activate || state == plant)
            state = deactivate;
        }
        return true;
    }

    @Override
    public void onDeActivate(BlockLocation anchor) {
        Fuel.setFurnace(furnace.getBlock(), furnaceYaw, false);
    }

    private interface State {
        State run();
    }

    private final State activate = new State() {
        @Override
        public State run() {
            if (rail.activate())
                return plant;
            else
                return parkHead.run();
        }
    };

    private final State deactivate = new State() {
        @Override
        public State run() {
            if (rail.deactivate()) {
                return parkInit;
            } else {
                return null;
            }
        }
    };

    private final State plant = new State() {
        @Override
        public State run() {
            // TODO Take planting actions

            if (rail.nextTile()) {
                return this;
            } else {
                return deactivate.run();
            }
        }
    };

    /**
     * In this state the planter will retract the moving rail if the head's
     * current direction is backward.
     */
    private final State parkInit = new State() {
        @Override
        public State run() {
            if (rail.isHeadMovingBackward()) {
                rail.retract();
                return parkHead;
            } else {
                return parkHead.run();
            }
        }
    };

    /**
     * In this state the planter will pull the head back to its parking
     * position.
     */
    private final State parkHead = new State() {
        @Override
        public State run() {
            switch (rail.retractHead()) {
            case OK:
                return this;
            case RAIL_END:
                return parkMovingRail.run();
            default:
                return null;
            }
        }
    };

    /**
     * In this state the planter will retract the moving rail back to its
     * parking position.
     */
    private final State parkMovingRail = new State() {
        @Override
        public State run() {
            if (rail.retract()) {
                return this;
            } else {
                return null;
            }
        }
    };
}
