package me.lyneira.MachinaBuilder;

import java.util.List;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;

import org.bukkit.entity.Player;

public class BasicBuilder extends BlockDropperBuilder {

    BasicBuilder(Blueprint blueprint, List<Integer> modules, BlockRotation yaw, Player player, BlockLocation anchor) {
        super(blueprint, modules, yaw, player, anchor, Blueprint.basicFurnace);
    }

    @Override
    protected void setContainers(BlockLocation anchor) {
        setChest(anchor, Blueprint.basicChest);
    }

    @Override
    protected State getStartingState() {
        return new Build();
    }
}
