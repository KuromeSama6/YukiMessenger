package moe.protasis.yukimessenger.velocity.event;

import lombok.Getter;
import moe.protasis.yukimessenger.velocity.impl.VelocityDownstream;

/**
 * Called when a downstream server is connected to the proxy. All operations to the downstream server instance are safe when this event is called.
 */
public class DownstreamServerConnectedEvent extends YukiServerEvent {
    public DownstreamServerConnectedEvent(VelocityDownstream server) {
        super(server);
    }
}
