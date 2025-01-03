package moe.protasis.yukimessenger.bungee;

import com.google.gson.JsonObject;
import lombok.Getter;
import moe.protasis.yukicommons.json.JsonWrapper;
import moe.protasis.yukimessenger.api.IInboundMessageHandler;
import moe.protasis.yukimessenger.message.InboundMessage;
import moe.protasis.yukimessenger.message.MessageProcessor;
import moe.protasis.yukimessenger.message.MessageResponse;
import moe.protasis.yukimessenger.message.ServerInboundMessage;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import moe.protasis.yukimessenger.bungee.service.WSServer;
import moe.protasis.yukimessenger.util.JsonObjectBuilder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.*;

public class MessengerServer {
    @Getter private static MessengerServer instance;
    private final WSServer wsServer;
    @Getter private final List<SpigotServer> clients = new ArrayList<>();
    private final Map<String, IInboundMessageHandler> susbcribers = new HashMap<>();
    @Getter
    private final MessageProcessor processor = new MessageProcessor();

    public MessengerServer() {
        instance = this;
        YukiMessenger.GetLogger().info("Starting websocket server");

        String ip = YukiMessenger.getConfig().GetString("ip", "127.0.0.1");
        int port = YukiMessenger.getConfig().GetInt("port", 8633);
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

    public void Subscribe(String action, IInboundMessageHandler callback) {
        susbcribers.put(action, callback);
    }

    public void ProcessMessage(SpigotServer server, InboundMessage msg) {
        JsonWrapper data = msg.getData();
        if (msg.getId() != null) {
            // idented message for a request
            UUID id = msg.getId();
            boolean hasError = data.Has("__error");

            MessageResponse response = new MessageResponse(!hasError, data);
            processor.HandleResponse(id, response);
        }

        if (msg.getAction() != null) {
            String action = msg.getAction();
            if (susbcribers.containsKey(action)) {
                ProxyServer.getInstance().getScheduler().runAsync(YukiMessenger.getInstance(), () -> {
                    synchronized (susbcribers) {
                        try {
                            susbcribers.get(action).Handle(msg);
                        } catch (Exception e) {
                            YukiMessenger.GetLogger().severe(String.format("An error occured while processing a message (server=%s, action=%s)", server.identName, action));
                            e.printStackTrace();
                            msg.Reject(e.getMessage());
                        }
                    }
                });
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
