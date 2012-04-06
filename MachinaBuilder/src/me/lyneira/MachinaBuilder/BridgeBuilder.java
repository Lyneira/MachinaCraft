package me.lyneira.MachinaBuilder;

import java.util.List;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;

import org.bukkit.entity.Player;

public class BridgeBuilder extends Builder {

    BridgeBuilder(Blueprint blueprint, List<Integer> modules, BlockRotation yaw, Player player, BlockLocation anchor) {
        super(blueprint, modules, yaw, player, anchor, Blueprint.bridgeFurnace, Blueprint.bridgeCentralBase, Blueprint.bridgeHeadPrimary, Blueprint.bridgeSupplyChest);
    }

    @Override
    protected void setContainers(BlockLocation anchor) {
        // TODO Auto-generated method stub

    }

    @Override
    protected State getStartingState() {
        return new Move();
    }

}
