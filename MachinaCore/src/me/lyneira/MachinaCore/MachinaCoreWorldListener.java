package me.lyneira.MachinaCore;

import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

final class MachinaCoreWorldListener extends WorldListener {
    
    @Override
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (!event.isCancelled()) {
            MachinaRunner.notifyChunkUnload(event.getChunk());
        }
    }
}
