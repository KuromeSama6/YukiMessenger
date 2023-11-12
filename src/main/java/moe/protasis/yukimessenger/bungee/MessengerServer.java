package moe.protasis.yukimessenger.bungee;

import com.google.gson.JsonObject;
import lombok.Getter;
import moe.protasis.yukimessenger.bungee.service.InboundMessage;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import moe.protasis.yukimessenger.bungee.service.WSServer;
import moe.protasis.yukimessenger.util.JsonObjectBuilder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MessengerServer {
    @Getter private static MessengerServer instance;
    private final WSServer wsServer;
    @Getter private final List<SpigotServer> clients = new ArrayList<>();
    private final Map<String, Consumer<InboundMessage>> susbcribers = new HashMap<>();

    public MessengerServer() {
        instance = this;
        YukiMessenger.GetLogger().info("Starting websocket client");

        String ip = YukiMessenger.getConfig().getString("ip", "127.0.0.1");
        int port = YukiMessenger.getConfig().getInt("port", 8633);
        wsServer = new WSServer(new InetSocketAddress(ip, port));
    }

    public void ProcessIncomingConnection(WebSocket webSocket, ClientHandshake clientHandshake) {
        // this connection has already been verified
        String ident = clientHandshake.getFieldValue("ident");
        ServerInfo spigotServer = ProxyServer.getInstance().getServerInfo(ident);
        clients.add(new SpigotServer(ident, webSocket, spigotServer));

        YukiMessenger.GetLogger().info(String.format("§aServer %s connection established.", ident));
    }

    public SpigotServer GetServer(WebSocket conn) {
        return clients.stream()
                .filter(c -> c.conn == conn)
                .findFirst().orElse(null);
    }

    public void Subscribe(String action, Consumer<InboundMessage> callback) {
        susbcribers.put(action, callback);
    }

    public void ProcessMessage(SpigotServer server, JsonObject message) {
        if (!message.has("__action")) return;
        String action = message.get("__action").getAsString();

        if (susbcribers.containsKey(action)) {
            InboundMessage msg = new InboundMessage(server, message);
            try {
                susbcribers.get(action).accept(msg);
            } catch (Exception e) {
                YukiMessenger.GetLogger().severe(String.format("An error occured while processing a message (server=%s, action=%s)", server.identName, action));
                e.printStackTrace();
                new InboundMessage(server, message).Squawk(new JsonObjectBuilder()
                        .put("__error", true)
                        .put("__error_message", e.getMessage())
                        .finish());
            }
        }
    }

    public void RemoveServer(SpigotServer server) {
        clients.remove(server);
    }

    public void Close() {
        try {
            wsServer.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
