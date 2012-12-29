package me.lyneira.MachinaCore;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import me.lyneira.util.InventoryManager;

import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Represents blueprint data of a single module in a {@link Movable} machina.
 * 
 * @author Lyneira
 */
class BlueprintModule {
    final BlueprintBlock[] blueprint;
    final Map<BlockRotation, BlockVector[]> blueprintVectors = new EnumMap<BlockRotation, BlockVector[]>(BlockRotation.class);
    final int size;
    private final int[] dataIndices;
    private final int[] inventoryIndices;

    /**
     * Constructs a {@link BlueprintModule} from the given Blueprint
     * 
     * @param blueprintEast
     *            The blueprint to use
     */
    BlueprintModule(final ModuleFactory blueprintEast) {
        List<BlueprintBlock> blueprintFinal = blueprintEast.getBlueprintFinal();
        size = blueprintFinal.size();
        blueprint = blueprintFinal.toArray(new BlueprintBlock[size]);

        // Initialize the vector arrays for each rotation.
        for (BlockRotation rotation : BlockRotation.values()) {
            BlockVector[] vectors = new BlockVector[size];
            for (int i = 0; i < size; i++) {
                vectors[i] = blueprint[i].vector(rotation);
            }
            blueprintVectors.put(rotation, vectors);
        }

        dataIndices = calculateDataIndices();
        inventoryIndices = calculateInventoryIndices();
    }

    /**
     * Detects whether all non-key blocks are present in relation to the anchor.
     * 
     * @param anchor
     *            {@link BlockLocation} to detect at
     * @return True if the non-key blocks are present.
     */
    boolean detectOther(final BlockLocation anchor, final BlockRotation yaw) {
        BlockVector[] vectors = blueprintVectors.get(yaw);
        for (int i = 0; i < size; i++) {
            if (!blueprint[i].key) {
                if (anchor.getRelative(vectors[i]).getTypeId() != blueprint[i].typeId) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns an array of bytes containing the block data for this blueprint.
     * 
     * @param anchor
     *            The anchor for which to grab data from the blocks
     * @return An array of bytes of block data
     */
    byte[] getBlockData(final BlockLocation anchor, final BlockRotation yaw) {
        BlockVector[] vectors = blueprintVectors.get(yaw);
        byte[] result = new byte[dataIndices.length];
        for (int i = 0; i < dataIndices.length; i++) {
            BlockLocation location = anchor.getRelative(vectors[dataIndices[i]]);
            result[i] = location.getBlock().getData();
        }
        return result;
    }

    /**
     * Sets the data for the blocks around anchor using the given data byte
     * array.
     * 
     * @param anchor
     *            The anchor for which to set data
     * @param data
     *            The byte array to use
     */
    void setBlockData(final BlockLocation anchor, final byte[] data, final BlockRotation yaw) {
        BlockVector[] vectors = blueprintVectors.get(yaw);
        for (int i = 0; i < dataIndices.length; i++) {
            Block block = anchor.getRelative(vectors[dataIndices[i]]).getBlock();
            block.setData(data[i]);
        }
    }

    /**
     * Returns a {@link List} of {@link ItemStack} arrays grabbed from the
     * inventories in this blueprint. The inventories are cleared in the
     * process.
     * 
     * @param anchor
     *            The anchor for which to grab inventory from the blocks
     * @return A {@link List} of {@link ItemStack} arrays
     */
    ItemStack[][] getBlockInventories(final BlockLocation anchor, final BlockRotation yaw) {
        BlockVector[] vectors = blueprintVectors.get(yaw);
        ItemStack[][] result = new ItemStack[inventoryIndices.length][];
        for (int i = 0; i < inventoryIndices.length; i++) {
            Block block = anchor.getRelative(vectors[inventoryIndices[i]]).getBlock();
            Inventory inventory = InventoryManager.getSafeInventory(block);
            result[i] = inventory.getContents();
            inventory.clear();
        }
        return result;
    }

    /**
     * Sets the inventory for the blocks around the anchor using the given array
     * of inventories
     * 
     * @param anchor
     *            The anchor for which to set inventory
     * @param inventories
     *            The inventory array to use
     */
    void setBlockInventories(final BlockLocation anchor, final ItemStack[][] inventories, final BlockRotation yaw) {
        BlockVector[] vectors = blueprintVectors.get(yaw);
        for (int i = 0; i < inventoryIndices.length; i++) {
            Block block = anchor.getRelative(vectors[inventoryIndices[i]]).getBlock();
            Inventory inventory = InventoryManager.getSafeInventory(block);
            inventory.setContents(inventories[i]);
        }
    }

    /**
     * Returns an array of indices whose blocks need their data copied during a
     * move.
     * 
     * @return An array of indices for data blocks
     */
    private int[] calculateDataIndices() {
        boolean[] copyData = new boolean[size];
        int count = 0;
        for (int index = 0; index < size; index++) {
            if (BlockData.copyData(blueprint[index].typeId)) {
                copyData[index] = true;
                count++;
            } else {
                copyData[index] = false;
            }
        }
        int[] result = new int[count];
        int j = 0;
        for (int index = 0; index < size; index++) {
            if (copyData[index]) {
                result[j] = index;
                j++;
            }
        }
        return result;
    }

    /**
     * Returns an array of indices whose blocks need their inventory copied
     * during a move.
     * 
     * @return An array of indices for inventory blocks
     */
    private int[] calculateInventoryIndices() {
        boolean[] copyInventory = new boolean[size];
        int count = 0;
        for (int index = 0; index < size; index++) {
            if (BlockData.hasInventory(blueprint[index].typeId)) {
                copyInventory[index] = true;
                count++;
            } else {
                copyInventory[index] = false;
            }
        }
        int[] result = new int[count];
        int j = 0;
        for (int index = 0; index < size; index++) {
            if (copyInventory[index]) {
                result[j] = index;
                j++;
            }
        }
        return result;
    }
}
