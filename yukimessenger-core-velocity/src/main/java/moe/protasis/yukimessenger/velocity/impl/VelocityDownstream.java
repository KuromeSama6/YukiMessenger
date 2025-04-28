package moe.protasis.yukimessenger.velocity.impl;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Getter;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukimessenger.velocity.YukiMessenger;
import moe.protasis.yukimessenger.api.message.IConnectedClient;
import org.java_websocket.WebSocket;

/**
 * Represents a client (spigot instance) that is connected and has an ident.
 */
public class VelocityDownstream implements IConnectedClient {
    private final String identName;
    private final WebSocket connection;
    @Getter
    private final RegisteredServer serverInfo;

    public VelocityDownstream(String identName, WebSocket conn) {
        this.identName = identName;
        this.connection = conn;

        var info  = YukiMessenger.getInstance().getProxyServer().getServer(identName).orElse(null);
        if (info == null)
            throw new IllegalArgumentException("No server found with name " + identName);

        serverInfo = info;
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
