package moe.protasis.yukimessenger.velocity.event;

import lombok.Getter;
import moe.protasis.yukimessenger.velocity.impl.VelocityDownstream;

/**
 * A base class for events that involve a SpigotServer.
 */
public abstract class YukiServerEvent  {
    /**
     * The SpigotServer that is involved in this event.
     */
    @Getter private VelocityDownstream server;

    public YukiServerEvent(VelocityDownstream server) {
        this.server = server;
    }

}
