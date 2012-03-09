package me.lyneira.MachinaFactoryCore;

public interface PipelineEndpoint extends EndpointVerify {
    public PacketHandler getHandler();
}
