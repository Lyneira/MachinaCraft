package me.lyneira.MachinaCore;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Marks this event as being an artificial BlockPlaceEvent. It is generated not
 * by a player's actions but by a machina.
 * 
 * @author Lyneira
 */
public class ArtificialBlockPlaceEvent extends BlockPlaceEvent {

    public ArtificialBlockPlaceEvent(Block placedBlock, BlockState replacedBlockState, Block placedAgainst, ItemStack itemInHand, Player thePlayer, boolean canBuild) {
        super(placedBlock, replacedBlockState, placedAgainst, itemInHand, thePlayer, canBuild);
    }

}
