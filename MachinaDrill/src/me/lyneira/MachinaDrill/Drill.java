package me.lyneira.MachinaDrill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lyneira.MachinaCore.BlockData;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.EventSimulator;
import me.lyneira.MachinaCore.Fuel;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.Movable;
import me.lyneira.MachinaCore.Tool;
import me.lyneira.MachinaCore.block.BlockRotation;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.util.InventoryManager;
import me.lyneira.util.InventoryTransaction;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;

/**
 * A Machina that moves forward, drilling up blocks in its path.
 * 
 * @author Lyneira
 * @author 5phinX
 */
final class Drill extends Movable {
    /**
     * The number of server ticks to wait for a move action.
     */
    private static int moveDelay = 20;

    /**
     * Whether the drill should use energy.
     */
    private static boolean useEnergy = true;

    /**
     * Whether the drill should use a pickaxe while drilling.
     */
    private static boolean useTool = false;

    /**
     * How many drills a player can have active at any one time. 0 means no
     * limit.
     */
    private static int activeLimit = 0;

    /**
     * Keeps track of how many active drills a player has.
     */
    private final static Map<Player, Integer> active = new HashMap<Player, Integer>();

    /**
     * Array of vectors that determines where the drill looks for blocks to
     * break.
     */
    private BlockVector[] drillPattern;

    /**
     * The amount of energy stored. This is just the number of server ticks left
     * before needing to consume new fuel.
     */
    private int currentEnergy = 0;

    /**
     * The next target location for the drill.
     */
    private BlockLocation queuedTarget = null;

    private int queuedDrillTime = 1;

    /**
     * The typeId of the next target location.
     */
    private int nextTypeId;

    private final boolean fastMode;

    private final Predicate<ItemStack> toolType;

    private BlockFace direction;
    private final BlueprintBlock chest;
    private final BlueprintBlock head;
    private final BlueprintBlock furnace;

    /**
     * Creates a new drill.
     * 
     * @param plugin
     *            The MachinaCore plugin
     * @param anchor
     *            The anchor location of the drill
     * @param yaw
     *            The direction of the drill
     * @param moduleIndices
     *            The active modules for the drill
     * @throws Exception
     */
    Drill(final Blueprint blueprint, final List<Integer> moduleIndices, final BlockRotation yaw, Player player, BlockLocation anchor, BlueprintBlock chest, BlueprintBlock head, BlueprintBlock furnace) {
        super(blueprint, moduleIndices, yaw, player);

        this.chest = chest;
        this.head = head;
        this.furnace = furnace;
        if (hasModule(Blueprint.mainModule)) {
            drillPattern = Blueprint.horizontalDrillPattern.get(yaw);
            direction = yaw.getYawFace();
            if (hasModule(Blueprint.headFast)) {
                toolType = diamondToolType;
                fastMode = true;
            } else {
                toolType = ironToolType;
                fastMode = false;
            }
        } else {
            drillPattern = Blueprint.verticalDrillPattern;
            direction = BlockFace.DOWN;
            if (hasModule(Blueprint.verticalHeadFast)) {
                toolType = diamondToolType;
                fastMode = true;
            } else {
                toolType = ironToolType;
                fastMode = false;
            }
        }
        // Set furnace to burning state.
        setFurnace(anchor, true);
        setChest(anchor);
    }

    /**
     * Initiates the current move or drill action in the action queue.
     */
    public HeartBeatEvent heartBeat(final BlockLocation anchor) {
        // Drills will not function for offline players.
        if (!player.isOnline())
            return null;

        BlockLocation target = nextTarget(anchor);
        if (target == null && queuedTarget == null) {
            BlockLocation newAnchor = doMove(anchor);
            if (newAnchor == null) {
                return null;
            }
            return new HeartBeatEvent(queueAction(newAnchor), newAnchor);
        } else if (target != null && target.equals(queuedTarget) && target.getTypeId() == nextTypeId) {
            if (!doDrill(anchor)) {
                return null;
            }
        }
        return new HeartBeatEvent(queueAction(anchor));
    }

    /**
     * Attempts to drill the next block in the drill pattern, and drop the
     * resulting item.
     * 
     * @param anchor
     *            The anchor of the machina
     * @return False if there is no energy/fuel left to complete the drill. True
     *         if the drill was successful or there was nothing to drill.
     */
    private boolean doDrill(final BlockLocation anchor) {
        if (BlockData.isDrillable(nextTypeId)) {
            Block chestBlock = anchor.getRelative(chest.vector(yaw)).getBlock();

            List<ItemStack> results = BlockData.breakBlock(queuedTarget);

            if (!useEnergy(anchor, queuedDrillTime))
                return false;
            
            if (!useTool(anchor))
                return false;

            if (!EventSimulator.blockBreak(queuedTarget, player))
                return false;
            
            // Initiate the transaction after useTool since the inventory may change because of it.
            InventoryTransaction transaction = new InventoryTransaction(InventoryManager.getSafeInventory(chestBlock));
            transaction.add(results);

            // Put results in the container
            if (!transaction.execute())
                return false;

            queuedTarget.setEmpty();
        }
        return true;
    }

    /**
     * Moves the drill forward if there is empty space to move into, and ground
     * to stand on.
     * 
     * @param anchor
     *            The anchor of the Drill to move
     * @return The new anchor location of the Drill, or null on failure.
     */
    private BlockLocation doMove(final BlockLocation anchor) {
        // Check if a sign pointing left or right is present and rotate.
        BlockRotation signRotation = readRotationSign(anchor);

        BlockLocation newAnchor = anchor.getRelative(direction);
        // For horizontal drills, check for ground at the new base
        if (direction != BlockFace.DOWN) {
            BlockLocation ground = newAnchor.getRelative(Blueprint.centralBase.vector(yaw).add(BlockFace.DOWN));
            if (!BlockData.isSolid(ground.getTypeId())) {
                return null;
            }
        }

        // Collision detection
        if (detectCollision(anchor, direction)) {
            return null;
        }

        // Simulate a block place event to give protection plugins a chance to
        // stop the drill move
        if (!canMove(newAnchor, head)) {
            return null;
        }

        // Use energy
        if (!useEnergy(anchor, moveDelay)) {
            return null;
        }

        // Okay to move.
        moveByFace(anchor, direction);

        if (signRotation != null) {
            doRotate(newAnchor, signRotation);
        }

        return newAnchor;
    }

    /**
     * Rotates the drill to the new direction, if this would not cause a
     * collision.
     * 
     * @param anchor
     *            The anchor of the Drill
     * @param newYaw
     *            The new direction
     */
    void doRotate(final BlockLocation anchor, final BlockRotation newYaw) {
        BlockRotation rotateBy = newYaw.subtract(yaw);
        if (rotateBy == BlockRotation.ROTATE_0 || detectCollisionRotate(anchor, rotateBy)) {
            return;
        }
        rotate(anchor, rotateBy);
        if (direction != BlockFace.DOWN) {
            // Reinitialize the drill pattern and direction since we rotated.
            drillPattern = Blueprint.horizontalDrillPattern.get(yaw);
            direction = yaw.getYawFace();
        }
        // Set furnace to correct direction.
        setFurnace(anchor, true);
        setChest(anchor);
    }

    /**
     * Determines the next target block for the drill and returns its location.
     * 
     * @param anchor
     *            The anchor of the drill
     * @return The BlockLocation of the next target, or null if no drillable
     *         target was found.
     */
    private BlockLocation nextTarget(final BlockLocation anchor) {
        for (BlockVector i : drillPattern) {
            BlockLocation location = anchor.getRelative(i);
            int typeId = location.getTypeId();
            if (BlockData.isDrillable(typeId)) {
                // Obsidian can only be dug with a diamond head.
                if (!fastMode && typeId == Material.OBSIDIAN.getId()) {
                    return null;
                }
                return location;
            }
        }
        return null;
    }

    /**
     * Determines the delay for the next action.
     * 
     * @param anchor
     *            The anchor of the Drill
     * @return Delay in server ticks for the next action
     */
    private int queueAction(final BlockLocation anchor) {
        queuedTarget = nextTarget(anchor);
        if (queuedTarget == null) {
            return moveDelay;
        } else {
            nextTypeId = queuedTarget.getTypeId();
            queuedDrillTime = BlockData.getDrillTime(nextTypeId);
            if (fastMode) {
                queuedDrillTime = Math.round(queuedDrillTime * 0.8F);
            }
            return queuedDrillTime;
        }
    }

    /**
     * Uses the given amount of energy and returns true if successful.
     * 
     * @param anchor
     *            The anchor of the Drill
     * @param energy
     *            The amount of energy needed for the next action
     * @return True if enough energy could be used up
     */
    private boolean useEnergy(final BlockLocation anchor, final int energy) {
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
     * Uses the appropriate tool for this drill type and returns true if
     * successful.
     * 
     * @param anchor
     *            The anchor of the Drill
     * @return True if a use of the pickaxe was expended.
     */
    private boolean useTool(final BlockLocation anchor) {
        if (!useTool)
            return true;
        return Tool.useInFurnace(((Furnace) anchor.getRelative(furnace.vector(yaw)).getBlock().getState()).getInventory(), toolType, ((InventoryHolder) anchor.getRelative(chest.vector(yaw))
                .getBlock().getState()).getInventory());
    }

    /**
     * If the player has permission to deactivate the drill, deactivate it. Or
     * rotate it instead if the player rightclicked with the appropriate item.
     */
    public boolean onLever(final BlockLocation anchor, Player player, ItemStack itemInHand) {
        if ((this.player == player && player.hasPermission("machinadrill.deactivate-own")) || player.hasPermission("machinadrill.deactivate-all")) {
            if (direction != BlockFace.DOWN && itemInHand != null && itemInHand.getType() == Blueprint.rotateMaterial) {
                doRotate(anchor, BlockRotation.yawFromLocation(player.getLocation()));
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Increments the number of active drills.
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
     * Sets the drill's furnace to the given state and set correct direction.
     * 
     * @param anchor
     *            The drill's anchor
     * @param burning
     *            Whether the furnace should be burning.
     */
    void setFurnace(final BlockLocation anchor, final boolean burning) {
        Block furnaceBlock = anchor.getRelative(furnace.vector(yaw)).getBlock();
        Fuel.setFurnace(furnaceBlock, yaw.getOpposite(), burning);
    }

    /**
     * Sets the drill's chest facing backwards.
     * 
     * @param anchor
     */
    void setChest(final BlockLocation anchor) {
        Block chestBlock = anchor.getRelative(chest.vector(yaw)).getBlock();
        if (chestBlock.getType() == Material.CHEST)
            chestBlock.setData(yaw.getOpposite().getYawData());
    }

    private BlockRotation readRotationSign(BlockLocation anchor) {
        BlockLocation signLocation = anchor.getRelative(Blueprint.centralBase.vector(yaw).add(yaw.getYawVector(), 3));
        if (!signLocation.checkTypes(Material.SIGN_POST, Material.SIGN)) {
            signLocation = signLocation.getRelative(BlockFace.UP);
            if (!signLocation.checkTypes(Material.SIGN_POST, Material.SIGN))
                return null;
        }
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

    // **** Static stuff ****
    /**
     * Returns true if the current limit allows activating another drill for
     * this player.
     * 
     * @param player
     * @return True if the player can activate another drill.
     */
    static boolean canActivate(Player player) {
        if (activeLimit == 0)
            return true;
        final Integer newActive = active.get(player);
        if (newActive == null || newActive < activeLimit)
            return true;

        return false;
    }

    private final static Predicate<ItemStack> ironToolType = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack item) {
            return item != null && item.getType() == Material.IRON_PICKAXE;
        }
    };

    private final static Predicate<ItemStack> diamondToolType = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack item) {
            return item != null && item.getType() == Material.DIAMOND_PICKAXE;
        }
    };

    /**
     * Loads the given configuration.
     * 
     * @param configuration
     */
    static void loadConfiguration(ConfigurationSection configuration) {
        moveDelay = Math.max(configuration.getInt("move-delay", moveDelay), 1);
        useEnergy = configuration.getBoolean("use-energy", useEnergy);
        useTool = configuration.getBoolean("use-tool", useTool);
        activeLimit = Math.max(configuration.getInt("active-limit", activeLimit), 0);
        Blueprint.activationDepthLimit = configuration.getInt("depth-limit", Blueprint.activationDepthLimit);
    }
}