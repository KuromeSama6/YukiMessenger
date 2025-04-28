package moe.protasis.yukimessenger.bungeecord.impl;

import lombok.Getter;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukimessenger.api.message.IConnectedClient;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.java_websocket.WebSocket;

/**
 * Represents a client (spigot instance) that is connected and has an ident.
 */
public class SpigotDownstream implements IConnectedClient {
    private final String identName;
    private final WebSocket connection;
    @Getter
    private final ServerInfo serverInfo;

    public SpigotDownstream(String identName, WebSocket conn) {
        this.identName = identName;
        this.connection = conn;

        serverInfo = ProxyServer.getInstance().getServerInfo(identName);
        if (serverInfo == null)
            throw new IllegalArgumentException("No server found with name " + identName);
    }

    @Override
    public void Send(JsonWrapper data) {
        connection.send(data.toString());
    }

    @Override
    public String GetId() {
        return identName;
    }

    @Override
    public WebSocket GetConnection() {
        return connection;
    }

    @Override
    public String toString() {
        return "Downstream(%s)".formatted(identName);
    }
}
