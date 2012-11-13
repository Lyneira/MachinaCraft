package me.lyneira.MachinaDrill;

import org.bukkit.World;
import org.bukkit.entity.Player;

import me.lyneira.MachinaCore.block.BlockRotation;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.machina.MachinaBlueprint;
import me.lyneira.MachinaCore.machina.MachinaController;
import me.lyneira.MachinaCore.machina.MachinaDetector;
import me.lyneira.MachinaCore.machina.model.ModelTree;

class Detector implements MachinaDetector {
    static int activationDepthLimit = 0;
    
    @Override
    public MachinaBlueprint getBlueprint() {
        return null;
    }

    @Override
    public MachinaController detect(ModelTree model, Player player, World world, BlockRotation rotation, BlockVector origin) {
        // TODO Auto-generated method stub
        return null;
    }
}
