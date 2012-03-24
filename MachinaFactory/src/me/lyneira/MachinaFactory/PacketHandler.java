package me.lyneira.MachinaFactory;

/**
 * Represents a packet handler for {@link PipelineEndpoint} implementations.
 * Suggested usage in your implementation:<br>
 * private static final PacketHandler handler = new PacketHandler(PacketListener<?>...);
 * 
 * @author Lyneira
 * 
 */
public class PacketHandler {
    private final PacketListener<?>[] listeners;
    private final Class<?>[] types;

    /**
     * Constructs a new PacketHandler for the given PacketListeners.
     * @param listeners A list of PacketListeners for class types this handler should handle.
     */
    public PacketHandler(PacketListener<?>... listeners) {
        this.listeners = listeners;
        types = new Class<?>[listeners.length];
        for (int i = 0; i < listeners.length; i++) {
            Class<?> payloadType = listeners[i].payloadType();
            if (payloadType == null)
                throw new NullPointerException("PacketHandler constructor got a PacketListener with a null payloadType()!");
            types[i] = payloadType;
        }
    }

    /**
     * Dispatches the payload to the first listener able to handle it.
     * @param endpoint
     * @param payload
     * @return True if the payload was handled successfully.
     */
    <P> boolean handle(PipelineEndpoint endpoint, P payload) throws PacketTypeUnsupportedException {
        for (int i = 0; i < listeners.length; i++) {
            if (types[i].isInstance(payload)) {
                @SuppressWarnings("unchecked")
                PacketListener<P> l = (PacketListener<P>) listeners[i];
                return l.handle(endpoint, payload);
            }
        }
        throw new PacketTypeUnsupportedException();
    }
}
