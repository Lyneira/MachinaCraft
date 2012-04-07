package me.lyneira.MachinaBuilder;

import java.util.List;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;

import org.bukkit.entity.Player;

/**
 * Basic BlockDropperBuilder
 * 
 * @author Lyneira
 */
public class BasicBuilder extends BlockDropperBuilder {

    BasicBuilder(Blueprint blueprint, List<Integer> modules, BlockRotation yaw, Player player, BlockLocation anchor) {
        super(blueprint, modules, yaw, player, anchor, blueprint.basicFurnace);
    }

    @Override
    protected void setContainers(BlockLocation anchor) {
        setChest(anchor, blueprint.basicChest);
    }

    @Override
    protected State getStartingState() {
        return new Build();
    }
}
