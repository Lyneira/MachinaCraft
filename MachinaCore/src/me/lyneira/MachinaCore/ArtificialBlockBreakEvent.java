package me.lyneira.MachinaCore;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Marks this event as being an artificial BlockBreakEvent. It is generated not
 * by a player's actions but by a machina.
 * 
 * @author Lyneira
 */
public class ArtificialBlockBreakEvent extends BlockBreakEvent {

    public ArtificialBlockBreakEvent(Block theBlock, Player player, List<ItemStack> drops) {
        super(theBlock, player, drops);
    }

}
