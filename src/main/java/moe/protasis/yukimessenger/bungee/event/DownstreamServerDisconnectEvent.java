package moe.protasis.yukimessenger.bungee.event;

import lombok.Getter;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import net.md_5.bungee.api.plugin.Event;

/**
 * Called when a websocket connection to a downstream server is broken.
 * When this event is called, the SpigotServer has already been removed from the list
 * of servers.
 */
public class DownstreamServerDisconnectEvent extends YukiServerEvent {
    /**
     * The code returned by the Websocket service.
     */
    @Getter private int code;
    /**
     * The message returned by the Websocket service.
     */
    @Getter private String message;

    public DownstreamServerDisconnectEvent(SpigotServer server, int code, String message) {
        super(server);
        this.code = code;
        this.message = message;
    }
}
