package me.lyneira.MachinaFactory;

/**
 * Represents any verifiable endpoint. This interface serves to allow
 * {@link Component} to handle being verified when an external Component
 * implements PipelineEndpoint.
 * 
 * @author Lyneira
 */
public interface EndpointVerify {
    /**
     * Verifies this endpoint and returns true if successful.
     * 
     * @return True if verification was a success.
     */
    public boolean verify();
}
