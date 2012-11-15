package me.lyneira.MachinaDrill;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import me.lyneira.MachinaCore.block.BlockRotation;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;
import me.lyneira.MachinaCore.machina.MachinaBlueprint;
import me.lyneira.MachinaCore.machina.MachinaController;
import me.lyneira.MachinaCore.machina.MachinaDetector;
import me.lyneira.MachinaCore.machina.model.BlueprintModel;
import me.lyneira.MachinaCore.machina.model.ConstructionModel;

class Detector implements MachinaDetector {
    static int activationDepthLimit = 0;
    static int materialCore = Material.GOLD_BLOCK.getId();
    static int materialBase = Material.WOOD.getId();
    static int materialHeadNormal = Material.IRON_BLOCK.getId();
    static int materialHeadFast = Material.DIAMOND_BLOCK.getId();
    static final int materialFurnace = Material.FURNACE.getId();
    static final int materialFurnaceBurning = Material.BURNING_FURNACE.getId();
    private static final int[] furnaceTypes = new int[] { materialFurnace, materialFurnaceBurning };

    private final MachinaBlueprint blueprint;
    final int core;
    final int chest;

    Detector() {
        MachinaBlock trigger = new MachinaBlock(0, 0, 0, materialCore);
        BlueprintModel model = new BlueprintModel();
        core = model.addBlock(trigger);
        chest = model.addBlock(new MachinaBlock(-1, 0, 0, Material.CHEST.getId()));
        // Wooden base left and right
        model.addBlock(new MachinaBlock(0, -1, -1, materialBase));
        model.addBlock(new MachinaBlock(0, -1, 1, materialBase));
        // TODO
        blueprint = new MachinaBlueprint(trigger, model);
    }

    @Override
    public MachinaBlueprint getBlueprint() {
        return blueprint;
    }

    @Override
    public MachinaController detect(ConstructionModel model, Player player, World world, BlockRotation yaw, BlockVector origin) {
        model.dumpTree();
        MachinaDrill.plugin.logInfo("Retrieving model block " + core);
        BlockVector coreBlock = model.getBlock(core);
        BlockVector baseBlock = coreBlock.add(BlockFace.DOWN);
        int centralBase = model.extend(baseBlock, materialBase);
        if (centralBase < 0)
            return null;
        BlockVector furnaceBlock = baseBlock.add(yaw.getOpposite().getYawFace());
        int furnace = model.extend(furnaceBlock, furnaceTypes);
        if (furnace < 0)
            return null;
        int drillHead = model.extend(coreBlock.add(yaw.getYawFace()), materialHeadNormal);
        if (drillHead < 0)
            return null;

        return new Drill();
    }
}
