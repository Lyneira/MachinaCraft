package me.lyneira.MachinaFactory;

/**
 * Represents a valid endpoint for a pipeline.
 * 
 * @author Lyneira
 */
public interface PipelineEndpoint extends EndpointVerify {
    /**
     * Returns the PacketHandler for this endpoint.
     * 
     * @return
     */
    public PacketHandler getHandler();
}
