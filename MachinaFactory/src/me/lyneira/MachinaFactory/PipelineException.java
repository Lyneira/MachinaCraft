package me.lyneira.MachinaFactory;

import me.lyneira.MachinaCore.BlockLocation;

/**
 * Thrown when a pipeline failed to find an endpoint, or fails to verify.
 * 
 * @author Lyneira
 */
public class PipelineException extends Exception {

    private static final long serialVersionUID = -719552834534112022L;
    public final BlockLocation location;

    PipelineException(BlockLocation location) {
        this.location = location;
    }
    
    PipelineException(BlockLocation location, String message) {
        super(message);
        this.location = location;
    }
    
    PipelineException(BlockLocation location, Throwable cause) {
        super(cause);
        this.location = location;
    }
    
    PipelineException(BlockLocation location, String message, Throwable cause) {
        super(message, cause);
        this.location = location;
    }
}
