package me.lyneira.MachinaFactory;

/**
 * Represents a listener for a certain type of packet payload.<br>
 * <br>
 * Suggested implementation:<br>
 * private static final PacketListener<P> inside the implementing MyEndpoint class.<br>
 * Suggested implementation of handle():<br>
 * return ((MyEndpoint) endpoint).handle(payload);<br>
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
    public boolean handle(PipelineEndpoint endpoint, P payload);

    public Class<P> payloadType();
}
