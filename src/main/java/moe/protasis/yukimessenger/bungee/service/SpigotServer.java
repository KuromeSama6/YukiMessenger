package moe.protasis.yukimessenger.bungee.service;

import com.google.gson.JsonObject;
import moe.protasis.yukimessenger.util.EnvUtil;
import net.md_5.bungee.api.config.ServerInfo;
import org.java_websocket.WebSocket;

/**
 * Represents a client (spigot instance) that is connected and has an ident.
 */
public class SpigotServer {
    public final String identName;
    public final WebSocket conn;
    public final ServerInfo spigot;

    public SpigotServer(String identName, WebSocket conn, ServerInfo spigot) {
        this.identName = identName;
        this.conn = conn;
        this.spigot = spigot;
    }

    /**
     * <b>Proxy only.</b>
     * Sends a message to this downstream spigot instance.
     * A subscriber to the action on the downstream spigot instance is required for the message to be processed.
     * @param action The action.
     * @param data The data.
     */
    public void SendUnidented(String action, JsonObject data) {
        EnvUtil.EnsureEnv(true);
        data.addProperty("__action", action);
        conn.send(data.toString());
    }
}
