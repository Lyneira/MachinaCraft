package me.lyneira.MachinaFactoryCore;

import java.util.List;
import java.util.ListIterator;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.Machina;

/**
 * Base class representing any factory component. Inside the heartBeat function,
 * the Component is guaranteed to be in the active state.
 * 
 * @author Lyneira
 */
public abstract class Component implements Machina, EndpointVerify {
    protected final BlockLocation anchor;
    protected final BlockRotation yaw;
    private final ComponentBlueprint blueprint;

    /**
     * Constructs a new Component from the ComponentBlueprint and activates it.
     * The constructor takes care of detecting the non-key blocks of the
     * blueprint and throws an exception if it failed to detect them.
     * @param blueprint
     * @param anchor
     * @param yaw
     * @throws ComponentActivateException The component could not activate due to a collision.
     * @throws ComponentDetectException The component could not be detected.
     */
    protected Component(ComponentBlueprint blueprint, BlockLocation anchor, BlockRotation yaw) throws ComponentActivateException, ComponentDetectException {
        this.anchor = anchor;
        this.yaw = yaw;
        this.blueprint = blueprint;

        boolean active = blueprint.detectOther(anchor, yaw);

        if (active)
            return;

        if (detectCollision(blueprint.activateDiffPlus))
            throw new ComponentActivateException();

        // Clear the negative difference in reverse order.
        for (ListIterator<BlueprintBlock> it = blueprint.activateDiffMinus.listIterator(blueprint.activateDiffMinus.size()); it.hasPrevious();) {
            anchor.getRelative(it.previous().vector(yaw)).setEmpty();
        }

        // Place the active blueprint now.
        for (BlueprintBlock i : blueprint.blueprintActive) {
            anchor.getRelative(i.vector(yaw)).setTypeId(i.typeId);
        }
    }

    @Override
    public final boolean verify(BlockLocation anchor) {
        return verify();
    }

    @Override
    public boolean verify() {
        return verify(blueprint.blueprintBase) && verify(blueprint.blueprintActive);
    }

    /**
     * Verifies the given blueprintblock list.
     * 
     * @param blueprint
     * @return True if successful.
     */
    private boolean verify(List<BlueprintBlock> blueprint) {
        for (BlueprintBlock i : blueprint) {
            if (anchor.getRelative(i.vector(yaw)).getTypeId() != i.typeId) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onLever(BlockLocation anchor, Player player, ItemStack itemInHand) {
        return false;
    }

    @Override
    public void onDeActivate(BlockLocation anchor) {

        if (!verify(blueprint.blueprintActive)) {
            return;
        }

        if (detectCollision(blueprint.deactivateDiffPlus))
            return;

        // Clear the negative difference in reverse order.
        for (ListIterator<BlueprintBlock> it = blueprint.deactivateDiffMinus.listIterator(blueprint.deactivateDiffMinus.size()); it.hasPrevious();) {
            this.anchor.getRelative(it.previous().vector(yaw)).setEmpty();
        }

        // Place the inactive blueprint now.
        for (BlueprintBlock i : blueprint.blueprintInactive) {
            this.anchor.getRelative(i.vector(yaw)).setTypeId(i.typeId);
        }
    }

    /**
     * Returns true if a collision would happen with the given diffPlus set.
     * 
     * @param anchor
     * @param diffPlus
     * @return True if a collision was detected
     */
    private boolean detectCollision(List<BlueprintBlock> diffPlus) {
        for (BlueprintBlock i : diffPlus) {
            if (!anchor.getRelative(i.vector(yaw)).isEmpty())
                return true;
        }
        return false;
    }
}
