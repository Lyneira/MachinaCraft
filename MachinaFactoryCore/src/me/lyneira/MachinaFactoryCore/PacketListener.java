package me.lyneira.MachinaFactoryCore;

public interface PacketListener<P> {
    public boolean handle(PipelineEndpoint endpoint, P payload);
    public Class<P> payloadType();
}
