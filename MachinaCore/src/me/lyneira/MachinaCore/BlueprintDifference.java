package me.lyneira.MachinaCore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.BlockFace;

/**
 * Represents the positive and negative difference for a {@link Machina} moving
 * in the direction of a given {@link BlockFace}
 * <p>
 * The plus difference represents the empty blocks that will become filled with
 * a part of the {@link Machina}, and can be used for collision detection.
 * <p>
 * The minus difference represents machina blocks that will become empty, and
 * can be used to clear the space behind a moved object.
 * 
 * @author Lyneira
 */
class BlueprintDifference {
    final BlockVector[] plus;
    final BlockVector[] minus;

    /**
     * Constructs a {@link BlueprintDifference} for the given blueprint in the
     * direction of BlockFace.
     * 
     * @param blueprint
     *            An array representing the blueprint to be calculated for.
     * @param blueprintSize
     *            The size of the blueprint array.
     * @param face
     *            The direction to calculate for.
     */
    BlueprintDifference(final BlueprintBlock[] blueprint, final BlockVector[] vectors, final int blueprintSize, final BlockFace face) {
        List<BlockVector> differenceMinus = new ArrayList<BlockVector>(blueprintSize);
        List<BlockVector> differencePlus = new ArrayList<BlockVector>(blueprintSize);
        List<BlockVector> originalVectors = new ArrayList<BlockVector>(blueprintSize);
        List<BlockVector> movedVectors = new ArrayList<BlockVector>(blueprintSize);

        for (BlockVector vector : vectors) {
            originalVectors.add(vector);
            movedVectors.add(vector.add(face));
        }

        differencePlus.addAll(movedVectors);
        differencePlus.removeAll(originalVectors);
        plus = differencePlus.toArray(new BlockVector[0]);

        // The negative difference must always include attached blocks.
        for (int i = 0; i < blueprintSize; i++) {
            BlockVector vector = originalVectors.get(i);
            if (blueprint[i].attached || !movedVectors.contains(vector)) {
                differenceMinus.add(vector);
            }
        }
        minus = differenceMinus.toArray(new BlockVector[0]);
    }

    /**
     * Calculates the positive difference for a teleportation of the given
     * blueprint by the given {@link BlockVector}.
     * 
     * @param vectors
     *            An array representing the vectors to calculate for.
     * @param blueprintSize
     *            The size of the array.
     * @param teleportBy
     *            The vector to calculate for.
     * @return An array of vectors representing the positive difference.
     */
    static BlockVector[] teleportDifference(final BlockVector[] vectors, final int blueprintSize, final BlockVector teleportBy) {
        List<BlockVector> originalVectors = new ArrayList<BlockVector>(blueprintSize);
        List<BlockVector> movedVectors = new ArrayList<BlockVector>(blueprintSize);

        for (BlockVector vector : vectors) {
            originalVectors.add(vector);
            movedVectors.add(vector.add(teleportBy));
        }

        movedVectors.removeAll(originalVectors);

        return movedVectors.toArray(new BlockVector[0]);
    }

    /**
     * Calculates the positive difference for a rotation of the given blueprint
     * by the given {@link BlockRotation}.
     * 
     * @param vectors
     *            An array representing the vectors to calculate for.
     * @param blueprintSize
     *            The size of the array.
     * @param rotateBy
     *            The rotation to calculate for.
     * @return An array of vectors representing the positive difference.
     */
    static BlockVector[] rotateDifference(final BlockVector[] vectors, final int blueprintSize, final BlockRotation rotateBy) {
        List<BlockVector> originalVectors = new ArrayList<BlockVector>(blueprintSize);
        List<BlockVector> rotatedVectors = new ArrayList<BlockVector>(blueprintSize);

        for (BlockVector vector : vectors) {
            originalVectors.add(vector);
            rotatedVectors.add(vector.rotated(rotateBy));
        }

        rotatedVectors.removeAll(originalVectors);

        return rotatedVectors.toArray(new BlockVector[0]);
    }
}
