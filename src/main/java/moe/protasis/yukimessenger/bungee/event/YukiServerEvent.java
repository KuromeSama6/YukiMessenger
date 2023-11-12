package moe.protasis.yukimessenger.bungee.event;

import lombok.Getter;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import net.md_5.bungee.api.plugin.Event;

/**
 * A base class for events that involve a SpigotServer.
 */
public class YukiServerEvent extends Event {
    /**
     * The SpigotServer that is involved in this event.
     */
    @Getter private SpigotServer server;

    public YukiServerEvent(SpigotServer server) {
        this.server = server;
    }

}
