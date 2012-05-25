package me.lyneira.MachinaBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lyneira.MachinaCore.BlockData;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.Fuel;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.Movable;
import me.lyneira.util.InventoryManager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;

/**
 * Abstract builder that can move and build rail behind its furnace.
 * 
 * @author Lyneira
 */
public abstract class Builder extends Movable {
    /**
     * The number of server ticks to wait for a move action.
     */
    private static int moveDelay = 20;

    /**
     * The number of server ticks to wait for a build action.
     */
    protected static int buildDelay = 10;

    /**
     * Whether the builder should use energy.
     */
    private static boolean useEnergy = true;
    
    /**
     * Whether the builder should use a pickaxe when removing blocks to replace them.
     */
    protected static boolean useTool = false;

    /**
     * How many builders a player can have active at any one time. 0 means no
     * limit.
     */
    private static int activeLimit = 0;

    /**
     * Keeps track of how many active builders a player has.
     */
    private final static Map<Player, Integer> active = new HashMap<Player, Integer>();

    /**
     * The amount of energy stored. This is just the number of server ticks left
     * before needing to consume new fuel.
     */
    private int currentEnergy = 0;

    /**
     * The drill's new location if it has moved.
     */
    private BlockLocation newAnchor;

    /**
     * {@link BlueprintBlock} pointing to this builder's furnace.
     */
    protected final BlueprintBlock furnace;

    /**
     * Central base of the builder for ground checks.
     */
    private final BlueprintBlock centralBase;

    /**
     * Supply chest used for building rails.
     */
    private final BlueprintBlock supplyChest;

    /**
     * Primary head of the builder for move permission checks and sign rotation.
     */
    private final BlueprintBlock primaryHead;

    protected final Blueprint blueprint;

    protected final State startingState;
    protected final State moveState = new Move();
    protected final State getBearings = new GetBearings();
    protected State state = getBearings;

    /**
     * Creates a new drill.
     * 
     * @param plugin
     *            The MachinaCore plugin
     * @param anchor
     *            The anchor location of the drill
     * @param yaw
     *            The direction of the drill
     * @param modules
     *            The active modules for the drill
     */
    Builder(final Blueprint blueprint, final List<Integer> modules, final BlockRotation yaw, Player player, BlockLocation anchor, //
            BlueprintBlock furnace, BlueprintBlock centralBase, BlueprintBlock primaryHead, BlueprintBlock supplyChest) {
        super(blueprint.blueprint, modules, yaw, player);
        this.blueprint = blueprint;
        this.furnace = furnace;
        this.centralBase = centralBase;
        this.primaryHead = primaryHead;
        this.supplyChest = supplyChest;

        // Set furnace to burning state.
        setFurnace(anchor, true);
        setContainers(anchor);
        startingState = getStartingState();
    }

    /**
     * Initiates the current move or build action.
     */
    public HeartBeatEvent heartBeat(final BlockLocation anchor) {
        // Builder will not function for offline players.
        if (!player.isOnline())
            return null;

        newAnchor = null;
        if (state == null)
            state = getBearings;
        else {
            state = state.run(anchor);
        }

        if (state == null)
            return null;

        if (newAnchor == null) {
            return new HeartBeatEvent(state.enqueue(anchor));
        } else {
            return new HeartBeatEvent(state.enqueue(newAnchor), newAnchor);
        }
    }

    /**
     * Rotates the builder to the new direction, if this would not cause a
     * collision.
     * 
     * @param anchor
     *            The anchor of the builder
     * @param newYaw
     *            The new direction
     */
    protected void doRotate(final BlockLocation anchor, final BlockRotation newYaw) {
        BlockRotation rotateBy = newYaw.subtract(yaw);
        if (rotateBy == BlockRotation.ROTATE_0) {
            return;
        }
        if (detectCollisionRotate(anchor, rotateBy)) {
            return;
        }
        rotate(anchor, rotateBy);
        // Set furnace to correct direction.
        setFurnace(anchor, true);
        setContainers(anchor);
        state = null;
    }

    /**
     * Uses the given amount of energy and returns true if successful.
     * 
     * @param anchor
     *            The anchor of the Builder
     * @param energy
     *            The amount of energy needed for the next action
     * @return True if enough energy could be used up
     */
    protected boolean useEnergy(final BlockLocation anchor, final int energy) {
        if (!useEnergy)
            return true;

        while (currentEnergy < energy) {
            int newFuel = Fuel.consume((Furnace) anchor.getRelative(furnace.vector(yaw)).getBlock().getState());
            if (newFuel > 0) {
                currentEnergy += newFuel;
            } else {
                return false;
            }
        }
        currentEnergy -= energy;
        return true;
    }

    /**
     * Simply checks the appropriate deactivate permission to determine whether
     * the player may deactivate the Builder.
     */
    public boolean onLever(final BlockLocation anchor, Player player, ItemStack itemInHand) {
        if ((this.player == player && player.hasPermission("machinabuilder.deactivate-own")) || player.hasPermission("machinabuilder.deactivate-all")) {
            if (itemInHand != null && itemInHand.getType() == Blueprint.rotateMaterial) {
                doRotate(anchor, BlockRotation.yawFromLocation(player.getLocation()));
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Increments the number of active builders.
     */
    void increment() {
        final Integer newActive = active.get(player);
        if (newActive == null)
            active.put(player, 1);
        else
            active.put(player, newActive + 1);
    }

    /**
     * Returns the burning furnace to its normal state and decrements the number
     * of active drills.
     * 
     * @param anchor
     *            The anchor of the Drill being deactivated
     */
    @Override
    public void onDeActivate(final BlockLocation anchor) {
        setFurnace(anchor, false);
        final Integer newActive = active.get(player);
        if (newActive == null)
            return;
        else if (newActive == 1)
            active.remove(player);
        else
            active.put(player, newActive - 1);
    }

    /**
     * Sets the builder's furnace to the given state and set correct direction.
     * 
     * @param anchor
     *            The builder's anchor
     * @param burning
     *            Whether the furnace should be burning.
     */
    private final void setFurnace(final BlockLocation anchor, final boolean burning) {
        Block furnaceBlock = anchor.getRelative(furnace.vector(yaw)).getBlock();
        Fuel.setFurnace(furnaceBlock, yaw.getOpposite(), burning);
    }

    /**
     * Sets a chest facing backwards.
     * 
     * @param anchor
     */
    protected void setChest(final BlockLocation anchor, final BlueprintBlock chest) {
        Block chestBlock = anchor.getRelative(chest.vector(yaw)).getBlock();
        if (chestBlock.getType() == Material.CHEST)
            chestBlock.setData(yaw.getOpposite().getYawData());
    }

    /**
     * Method used to set all the containers on a builder after it has rotated.
     * 
     * @param anchor
     */
    protected abstract void setContainers(final BlockLocation anchor);

    /**
     * Represents a state in the operation of the Builder
     */
    protected interface State {
        /**
         * Executes the state and returns the next state.
         * 
         * @param anchor
         * @return
         */
        State run(BlockLocation anchor);

        /**
         * Enqueues the state and returns the delay needed before it can
         * execute.
         * 
         * @param anchor
         * @return
         */
        int enqueue(BlockLocation anchor);
    }

    /**
     * Returns the starting state of the builder. This method is called before
     * your subclass is fully constructed, so you can't rely on any internal
     * fields yet. The builder's startingState field will be set to the return
     * value of this field.
     * 
     * @return The starting state the builder will use.
     */
    protected abstract State getStartingState();

    /**
     * In this stage, the builder attempts to move forward.
     */
    private class Move implements State {
        /**
         * Moves the drill forward if there is empty space to move into, and
         * ground to stand on.
         * 
         * @param anchor
         *            The anchor of the Drill to move
         */
        @Override
        public State run(BlockLocation anchor) {
            // Check if a sign pointing left or right is present and rotate.
            BlockRotation signRotation = readRotationSign(anchor);

            // Check for ground at the new base
            BlockFace face = yaw.getYawFace();
            BlockLocation movedAnchor = anchor.getRelative(face);
            BlockLocation ground = movedAnchor.getRelative(centralBase.vector(yaw).add(BlockFace.DOWN));
            if (!BlockData.isSolid(ground.getTypeId())) {
                return null;
            }

            if (detectCollision(anchor, face)) {
                return null;
            }

            // Simulate a block place event to give protection plugins a chance
            // to stop the move
            if (!canMove(movedAnchor, primaryHead)) {
                return null;
            }

            // Use energy
            if (!useEnergy(anchor, moveDelay)) {
                return null;
            }

            moveByFace(anchor, face);

            buildRail(movedAnchor);

            newAnchor = movedAnchor;
            if (signRotation != null) {
                doRotate(newAnchor, signRotation);
                return getBearings;
            }
            return startingState;
        }

        protected void buildRail(BlockLocation anchor) {
            BlockLocation target = anchor.getRelative(furnace.vector(yaw).add(yaw.getOpposite().getYawFace()));
            BlockLocation ground = target.getRelative(BlockFace.DOWN);

            if (!validBuildLocation(target))
                return;

            if (!BlockData.isSolid(ground.getTypeId()))
                return;

            Block chestBlock = anchor.getRelative(supplyChest.vector(yaw)).getBlock();
            InventoryManager manager = new InventoryManager(InventoryManager.getSafeInventory(chestBlock));

            if (!manager.find(isRail))
                return;

            ItemStack railItem = manager.get();
            int typeId = railItem.getTypeId();
            if (!canPlace(target, typeId, (byte) 0, ground))
                return;

            manager.decrement();
            target.setTypeId(typeId);
        }

        protected BlockRotation readRotationSign(BlockLocation anchor) {
            BlockLocation signLocation = anchor.getRelative(primaryHead.vector(yaw).add(yaw.getYawVector(), 2));
            if (!signLocation.checkTypes(Material.SIGN_POST, Material.SIGN))
                return null;
            String[] lines = ((Sign) signLocation.getBlock().getState()).getLines();
            for (String s : lines) {
                if (s == null)
                    continue;
                if (s.equals("->") || s.toLowerCase().equals("right"))
                    return yaw.getRight();
                if (s.equals("<-") || s.toLowerCase().equals("left"))
                    return yaw.getLeft();
            }
            return null;
        }

        @Override
        public int enqueue(BlockLocation anchor) {
            return moveDelay;
        }
    }
    
    private class GetBearings implements State {
        @Override
        public State run(BlockLocation anchor) {
            return startingState;
        }

        @Override
        public int enqueue(BlockLocation anchor) {
            return 10;
        }
        
    }

    // **** Static stuff ****
    /**
     * Returns true if the current limit allows activating another builder for
     * this player.
     * 
     * @param player
     * @return True if the player can activate another builder.
     */
    public static boolean canActivate(Player player) {
        if (activeLimit == 0)
            return true;
        final Integer newActive = active.get(player);
        if (newActive == null || newActive < activeLimit)
            return true;

        return false;
    }

    /**
     * Tests whether the {@link ItemStack} is a building block.
     */
    protected final static Predicate<ItemStack> isBuildingBlock = new Predicate<ItemStack>() {
        public boolean apply(ItemStack item) {
            return (item != null && BlockData.isSolid(item.getTypeId()));
        }
    };

    /**
     * Tests whether the {@link ItemStack} is a piece of rail.
     */
    protected final static Predicate<ItemStack> isRail = new Predicate<ItemStack>() {
        public boolean apply(ItemStack item) {
            if (item == null)
                return false;
            Material material = item.getType();
            return (material.equals(Material.RAILS) || material.equals(Material.POWERED_RAIL) || material.equals(Material.DETECTOR_RAIL));
        }
    };

    /**
     * Returns true if the given target location is valid to build in.
     * 
     * @param target
     * @return True if this location is valid
     */
    protected static boolean validBuildLocation(BlockLocation target) {
        return target.checkTypes(Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.LONG_GRASS, Material.SNOW);
    }

    /**
     * Returns true if the given target location is valid to place a block
     * against.
     * 
     * @param target
     * @return True if this location is valid to place against
     */
    protected static boolean validPlaceAgainst(BlockLocation target) {
        return (!target.checkTypes(Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.LONG_GRASS, Material.SNOW));
    }

    /**
     * Loads the given configuration.
     * 
     * @param configuration
     */
    static void loadConfiguration(ConfigurationSection configuration) {
        moveDelay = Math.max(configuration.getInt("move-delay", moveDelay), 1);
        buildDelay = Math.max(configuration.getInt("build-delay", buildDelay), 1);
        BlockDropperBuilder.maxDepth = Math.min(Math.max(configuration.getInt("max-depth", BlockDropperBuilder.maxDepth), 1), 256);
        useEnergy = configuration.getBoolean("use-energy", useEnergy);
        useTool = configuration.getBoolean("use-tool", useTool);
        activeLimit = Math.max(configuration.getInt("active-limit", activeLimit), 0);
    }
}
