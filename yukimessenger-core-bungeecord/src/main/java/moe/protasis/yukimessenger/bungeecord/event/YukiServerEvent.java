package moe.protasis.yukimessenger.bungeecord.event;

import lombok.Getter;
import moe.protasis.yukimessenger.bungeecord.impl.SpigotDownstream;
import net.md_5.bungee.api.plugin.Event;

/**
 * A base class for events that involve a SpigotServer.
 */
public class YukiServerEvent extends Event {
    /**
     * The SpigotServer that is involved in this event.
     */
    @Getter private SpigotDownstream server;

    public YukiServerEvent(SpigotDownstream server) {
        this.server = server;
    }

}
