package me.lyneira.MachinaCore;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.Material;

/**
 * Represents a single block in a blueprint. The 'south' {@link BlockVector} is
 * the location of this block in relation to the anchor (0,0,0) if the blueprint
 * were facing southward.
 * <p>
 * In the south direction, x+ is forward, y+ is up, z+ is right
 * 
 * @author Lyneira
 */
public class BlueprintBlock {
    /**
     * Where this block is located in relation to the anchor.
     */
    private final Map<BlockRotation, BlockVector> vectors = new EnumMap<BlockRotation, BlockVector>(BlockRotation.class);
    public final BlockVector south;

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
     * @param south
     *            {@link BlockVector} specifying where this block is located in
     *            relation to the anchor
     * @param type
     *            {@link Material}
     * @param key
     *            Whether this block needs special handling during detection
     */
    public BlueprintBlock(BlockVector south, Material type, boolean key) {
        this.south = south;
        vectors.put(BlockRotation.ROTATE_0, south);
        vectors.put(BlockRotation.ROTATE_90, south.rotated(BlockRotation.ROTATE_90));
        vectors.put(BlockRotation.ROTATE_180, south.rotated(BlockRotation.ROTATE_180));
        vectors.put(BlockRotation.ROTATE_270, south.rotated(BlockRotation.ROTATE_270));
        this.typeId = type.getId();
        this.key = key;
        this.attached = BlockData.isAttached(this.typeId);
    }

    /**
     * Returns the {@link BlockVector} for this {@link BlueprintBlock} at the
     * specified yaw.
     * 
     * @param yaw
     * @return
     */
    public BlockVector vector(BlockRotation yaw) {
        return vectors.get(yaw);
    }

    @Override
    public int hashCode() {
        return south.hashCode();
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

        return this.south.equals(other.south);
    }
}
