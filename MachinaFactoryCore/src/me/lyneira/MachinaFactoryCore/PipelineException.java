package me.lyneira.MachinaFactoryCore;

import me.lyneira.MachinaCore.BlockLocation;

/**
 * Thrown when a pipeline fails to find an endpoint, or fails to verify.
 * 
 * @author Lyneira
 */
public class PipelineException extends Exception {

    private static final long serialVersionUID = -719552834534112022L;
    public final BlockLocation source;

    PipelineException(BlockLocation source) {
        this.source = source;
    }
    
    PipelineException(BlockLocation source, String message) {
        super(message);
        this.source = source;
    }
    
    PipelineException(BlockLocation source, Throwable cause) {
        super(cause);
        this.source = source;
    }
    
    PipelineException(BlockLocation source, String message, Throwable cause) {
        super(message, cause);
        this.source = source;
    }
}
