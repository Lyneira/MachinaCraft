package me.lyneira.MachinaFactory;

/**
 * Represents a listener for a certain type of packet payload.<br>
 * <br>
 * Suggested implementation:<br>
 * private static final PacketListener
 * <P>
 * inside the implementing MyEndpoint class.<br>
 * Suggested implementation of handle():<br>
 * return ((MyEndpointClass) endpoint).handle(payload);<br>
 * <br>
 * Suggested implementation of payloadType():<br>
 * return P.class;<br>
 * <br>
 * <b>Important</b>:<br>
 * payloadType() must not return null, or the {@link PacketHandler} will throw a
 * NullPointerException.
 * 
 * @param <P>
 *            The type of payload this listener can handle.<br>
 * @author Lyneira
 */
public interface PacketListener<P> {
    /**
     * Dispatches the payload to the endpoint.
     * 
     * <p><b>Suggested implementation:</b><br>
     * return ((MyEndpointClass) endpoint).handle(payload);</p>
     * 
     * @param endpoint
     *            The endpoint to dispatch to
     * @param payload
     *            The payload being sent
     * @return True if the payload was successfully handled.
     */
    public boolean handle(PipelineEndpoint endpoint, P payload);

    /**
     * Returns the class handled by this packet listener. Null return values
     * will generate a NullPointerException.
     * 
     * <p><b>Suggested implementation:</b><br>
     * return P.class;</p>
     * 
     * @return The class object for this packetlistener.
     * 
     */
    public Class<P> payloadType();
}
