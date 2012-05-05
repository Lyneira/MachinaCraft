package me.lyneira.MachinaPlanter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockVector;

/**
 * Represents the moving rail of the planter.
 * 
 * @author Lyneira
 */
class MovingRail {
    private final List<BlockVector> rail;
    private final int railLastSlot;
    private final BlockFace direction;
    private BlockLocation anchor;
    private final Head head;

    // Only this class can instantiate itself
    private MovingRail(List<BlockVector> rail, BlockFace direction, BlockLocation anchor, int headSlot, boolean headActive) {
        this.rail = rail;
        railLastSlot = rail.size() - 1;
        this.direction = direction;
        this.anchor = anchor;
        this.head = new Head(headSlot, headActive);
    }

    final boolean verify() {
        if (!anchor.checkType(Blueprint.planterMovingRailMaterial))
            return false;

        int i = 0;
        for (BlockVector v : rail) {
            BlockLocation location = anchor.getRelative(v);
            if (i == head.slot) {
                if (!head.verify()) {
                    return false;
                }
            } else if (!location.checkType(Blueprint.planterMovingRailMaterial)) {
                return false;
            }
            i++;
        }
        return true;
    }

    final boolean activate() {
        return head.activate();
    }

    final boolean deactivate() {
        return head.deactivate();
    }

    /**
     * Returns the current tile for planting below the head.
     * 
     * @return The block two spaces below the head. If the head was not active
     *         this will return null.
     */
    final BlockLocation currentTile() {
        return head.tile();
    }

    /**
     * Attempts to move the head to the next tile for planting.
     * 
     * @return
     */
    final HeadNextResult nextTile() {
        return head.next();
    }

    /**
     * @return True if the head is currently moving backward.
     */
    final boolean isHeadMovingBackward() {
        return (head.slotDirection != 1);
    }

    final HeadNextResult retractHead() {
        return head.retract();
    }

    /**
     * Attempts to move the entire moving rail.
     * 
     * @return True if successful, false if there was a collision.
     */
    boolean move(BlockFace direction) {
        // Check collisions first
        if (head.moveSidewaysCollision(direction))
            return false;

        BlockLocation newAnchor = anchor.getRelative(direction);

        if (!newAnchor.isEmptyForCollision())
            return false;

        int i = 0;
        for (BlockVector v : rail) {
            if (i != head.slot) {
                if (!newAnchor.getRelative(v).isEmptyForCollision())
                    return false;
            }
            i++;
        }

        // OK to move.
        anchor.setEmpty();
        newAnchor.setType(Blueprint.planterMovingRailMaterial);
        i = 0;
        for (BlockVector v : rail) {
            if (i != head.slot) {
                anchor.getRelative(v).setEmpty();
                newAnchor.getRelative(v).setType(Blueprint.planterMovingRailMaterial);
            }
            i++;
        }

        anchor = newAnchor;
        head.moveSideways(direction);
        return true;
    }

    private class Head {
        private int slot;
        private int slotDirection;
        private BlockFace direction;
        private boolean active;
        private BlockLocation top;
        private BlockLocation middle;
        private BlockLocation bottom;

        Head(int slot, boolean active) {
            this.slot = slot;
            this.active = active;
            if (slot == railLastSlot) {
                direction = MovingRail.this.direction.getOppositeFace();
                slotDirection = -1;
            } else {
                direction = MovingRail.this.direction;
                slotDirection = 1;
            }
            if (active) {
                top = anchor.getRelative(rail.get(slot));
                middle = top.getRelative(BlockFace.DOWN);
                bottom = middle.getRelative(BlockFace.DOWN);
            } else {
                middle = anchor.getRelative(rail.get(slot));
                top = middle.getRelative(BlockFace.UP);
                bottom = middle.getRelative(BlockFace.DOWN);
            }
        }

        final boolean verify() {
            return (top.checkType(Blueprint.planterMovingRailMaterial) //
                    && middle.checkType(Blueprint.planterHeadBlockMaterial) //
            && bottom.checkType(Blueprint.planterHeadMaterial));
        }

        final void put(byte data) {
            top.setType(Blueprint.planterMovingRailMaterial);
            middle.setTypeIdAndData(Blueprint.planterHeadBlockMaterial.getId(), data, true);
            bottom.setType(Blueprint.planterHeadMaterial);
        }

        /**
         * Activates the head by moving it down.
         * 
         * @return True if the head was moved, false if there was a collision.
         */
        boolean activate() {
            if (active)
                return true;

            BlockLocation newBottom = bottom.getRelative(BlockFace.DOWN);
            if (!newBottom.isEmptyForCollision())
                return false;

            // Preserve the data for the middle block
            byte data = middle.getBlock().getData();
            top.setEmpty();
            top = middle;
            middle = bottom;
            bottom = newBottom;
            put(data);
            active = true;
            return true;
        }

        /**
         * Deactivates the head by moving it up.
         * 
         * @return True if the head was moved, false if there was a collision.
         */
        boolean deactivate() {
            if (!active)
                return true;

            BlockLocation newTop = top.getRelative(BlockFace.UP);
            if (!newTop.isEmptyForCollision())
                return false;

            // Preserve the data for the middle block
            byte data = middle.getBlock().getData();
            bottom.setEmpty();
            bottom = middle;
            middle = top;
            top = newTop;
            put(data);
            active = false;
            return true;
        }

        /**
         * Attempts to move the head to the next tile for planting.
         * 
         * @return The result of the move.
         */
        HeadNextResult next() {
            if (!active) {
                MachinaPlanter.log("Attempted to move head forward while it isn't active!");
                return HeadNextResult.COLLISION;
            }

            // We're at the end of the rail, the head does not need to be moved
            // by this method.
            if (isAtRailEnd()) {
                return HeadNextResult.RAIL_END;
            }

            BlockLocation newTop = top.getRelative(direction);
            BlockLocation newMiddle = middle.getRelative(direction);
            BlockLocation newBottom = bottom.getRelative(direction);

            if (!(newMiddle.isEmptyForCollision() && newBottom.isEmptyForCollision()))
                return HeadNextResult.COLLISION;

            // Preserve the data for the middle block
            byte data = middle.getBlock().getData();
            middle.setEmpty();
            bottom.setEmpty();
            top = newTop;
            middle = newMiddle;
            bottom = newBottom;
            // Minimize writes to the world
            middle.setTypeIdAndData(Blueprint.planterHeadBlockMaterial.getId(), data, true);
            bottom.setType(Blueprint.planterHeadMaterial);
            slot += slotDirection;
            return HeadNextResult.OK;
        }

        HeadNextResult retract() {
            if (active) {
                MachinaPlanter.log("Attempted to retract head while it is active!");
                return HeadNextResult.COLLISION;
            }

            // Set direction to backwards
            slotDirection = -1;
            direction = MovingRail.this.direction.getOppositeFace();
            if (isAtRailEnd()) {
                return HeadNextResult.RAIL_END;
            }

            BlockLocation newTop = top.getRelative(direction);
            BlockLocation newMiddle = middle.getRelative(direction);
            BlockLocation newBottom = bottom.getRelative(direction);

            if (!(newTop.isEmptyForCollision() && newBottom.isEmptyForCollision()))
                return HeadNextResult.COLLISION;

            // Preserve the data for the middle block
            byte data = middle.getBlock().getData();
            top.setEmpty();
            middle.setType(Blueprint.planterMovingRailMaterial);
            bottom.setEmpty();
            top = newTop;
            middle = newMiddle;
            bottom = newBottom;
            put(data);
            slot += slotDirection;
            return HeadNextResult.OK;
        }

        boolean moveSidewaysCollision(BlockFace direction) {
            return (!(top.getRelative(direction).isEmptyForCollision() && middle.getRelative(direction).isEmptyForCollision() && bottom.getRelative(direction).isEmptyForCollision()));
        }

        void moveSideways(BlockFace direction) {
            BlockLocation newTop = top.getRelative(direction);
            BlockLocation newMiddle = middle.getRelative(direction);
            BlockLocation newBottom = bottom.getRelative(direction);
            // Preserve the data for the current middle block
            byte data = middle.getBlock().getData();

            top.setEmpty();
            middle.setEmpty();
            bottom.setEmpty();
            top = newTop;
            middle = newMiddle;
            bottom = newBottom;
            put(data);
        }

        /**
         * Checks if the head is at the rail's end and switches the direction if
         * true.
         * 
         * @return True if the head was at the rail's end.
         */
        boolean isAtRailEnd() {
            if (slotDirection == 1) {
                if (slot == railLastSlot) {
                    direction = direction.getOppositeFace();
                    slotDirection = -1;
                    return true;
                }
            } else {
                if (slot == 0) {
                    direction = direction.getOppositeFace();
                    slotDirection = 1;
                    return true;
                }
            }
            return false;
        }

        BlockLocation tile() {
            if (!active)
                return null;

            return bottom.getRelative(BlockFace.DOWN, 2);
        }
    }

    static MovingRail detect(BlockLocation anchor, BlockFace direction) {
        List<BlockVector> rail = new ArrayList<BlockVector>(Planter.maxWidth);
        if (!anchor.checkType(Blueprint.planterMovingRailMaterial))
            return null;

        int head = -1;
        boolean headActive = false;
        BlockLocation next = anchor.getRelative(direction);
        for (int i = 0; i < Planter.maxWidth; i++) {
            Material material = next.getType();
            BlockVector vector = next.subtract(anchor);
            if (material == Blueprint.planterMovingRailMaterial) {
                BlockLocation headBlock = next.getRelative(BlockFace.DOWN);
                rail.add(vector);
                if (headBlock.checkType(Blueprint.planterHeadBlockMaterial) && headBlock.getRelative(BlockFace.DOWN).checkType(Blueprint.planterHeadMaterial)) {
                    // Head is moved down
                    head = i;
                    headActive = true;
                }
            } else if (material == Blueprint.planterHeadBlockMaterial && next.getRelative(BlockFace.UP).checkType(Blueprint.planterMovingRailMaterial)
                    && next.getRelative(BlockFace.DOWN).checkType(Blueprint.planterHeadMaterial)) {
                rail.add(vector);
                head = i;
                headActive = false;
            } else {
                break;
            }
            next = next.getRelative(direction);
        }

        if (head == -1)
            return null;

        return new MovingRail(rail, direction, anchor, head, headActive);
    }
}
