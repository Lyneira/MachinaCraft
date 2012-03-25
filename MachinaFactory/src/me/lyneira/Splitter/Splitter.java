package me.lyneira.Splitter;

import org.bukkit.entity.Player;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaFactory.Component;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentDetectException;
import me.lyneira.MachinaFactory.PacketHandler;
import me.lyneira.MachinaFactory.PipelineEndpoint;

public class Splitter extends Component implements PipelineEndpoint {
    
    Blueprint blueprint;
    
    protected Splitter(Blueprint blueprint, BlockLocation anchor, BlockRotation yaw, Player player) throws ComponentActivateException, ComponentDetectException {
        super(blueprint.blueprint, anchor, yaw);
        this.blueprint = blueprint;
        // TODO Auto-generated constructor stub
    }

    @Override
    public HeartBeatEvent heartBeat(BlockLocation anchor) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PacketHandler getHandler() {
        // TODO Auto-generated method stub
        return null;
    }

}
