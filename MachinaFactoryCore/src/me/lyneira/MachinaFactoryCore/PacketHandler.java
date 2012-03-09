package me.lyneira.MachinaFactoryCore;

public class PacketHandler {
    private final PacketListener<?>[] listeners;
    private final Class<?>[] types;

    public PacketHandler(PacketListener<?>... listeners) {
        this.listeners = listeners;
        types = new Class<?>[listeners.length];
        for (int i = 0; i < listeners.length; i++) {
            types[i] = listeners[i].payloadType();
        }
    }

    public <P> boolean handle(PipelineEndpoint endpoint, P payload) {
        for (int i = 0; i < listeners.length; i++) {
            if (types[i].isInstance(payload)) {
                @SuppressWarnings("unchecked")
                PacketListener<P> l = (PacketListener<P>) listeners[i];
                return l.handle(endpoint, payload);
            }
        }
        return false;
    }
}
