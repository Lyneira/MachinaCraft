package me.lyneira.MachinaCore;

import org.bukkit.Material;

/**
 * Represents a single block in a blueprint.
 * 
 * @author Lyneira
 */
class BlueprintBlock {
    /**
     * Where this block is located in relation to the anchor.
     */
    public final BlockVector vector;
    /**
     * The type of this block
     */
    public final int typeId;
    /**
     * Whether this block needs special handling during detection.
     */
    public final boolean key;

    /**
     * Whether this block is attached to another block in the device.
     */
    public final boolean attached;

    /**
     * Constructs a new {@link BlueprintBlock} from the given parameters.
     * 
     * @param vector
     *            {@link BlockVector} specifying where this block is located in
     *            relation to the anchor
     * @param type
     *            {@link Material}
     * @param key
     *            Whether this block needs special handling during detection
     */
    BlueprintBlock(BlockVector vector, Material type, boolean key) {
        this.vector = vector;
        this.typeId = type.getId();
        this.key = key;
        this.attached = BlockData.isAttached(this.typeId);
    }

    /**
     * Constructs a new {@link BlueprintBlock} from the given vector and another
     * {@link BlueprintBlock}.
     * 
     * @param vector
     *            {@link BlockVector} specifying where this block is located in
     *            relation to the anchor
     * @param other
     *            {@link BlueprintBlock} that will be copied, except from the
     *            vector
     */
    BlueprintBlock(BlockVector vector, BlueprintBlock other) {
        this.vector = vector;
        this.typeId = other.typeId;
        this.key = other.key;
        this.attached = other.attached;
    }

    @Override
    public int hashCode() {
        return vector.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BlueprintBlock other = (BlueprintBlock) obj;

        return this.vector == other.vector;
    }
}
