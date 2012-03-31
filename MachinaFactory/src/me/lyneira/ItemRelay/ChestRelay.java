package me.lyneira.ItemRelay;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentDetectException;

import org.bukkit.entity.Player;

/**
 * Item Relay with a chest as container.
 * 
 * @author Lyneira
 */
public class ChestRelay extends ItemRelay {

    ChestRelay(Blueprint blueprint, BlockLocation anchor, BlockRotation yaw, Player player) throws ComponentActivateException, ComponentDetectException {
        super(blueprint, blueprint.blueprintChest, anchor, yaw, player);
    }

    @Override
    protected BlockLocation container() {
        return anchor.getRelative(blueprint.chest.vector(yaw));
    }

}
