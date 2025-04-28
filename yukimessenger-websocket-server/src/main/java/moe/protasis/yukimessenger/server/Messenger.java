package moe.protasis.yukimessenger.server;

import lombok.Getter;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukicommons.api.plugin.IAbstractPlugin;
import moe.protasis.yukicommons.api.scheduler.PooledScheduler;
import moe.protasis.yukimessenger.api.IInboundMessageHandler;
import moe.protasis.yukimessenger.api.message.IConnectedClient;
import moe.protasis.yukimessenger.api.message.InboundMessage;
import moe.protasis.yukimessenger.api.message.MessageProcessor;
import moe.protasis.yukimessenger.api.message.MessageResponse;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.logging.Logger;

public abstract class Messenger {
    private final MessengerWebsocketServer wsServer;
    @Getter
    private final IAbstractPlugin plugin;
    @Getter
    private final PooledScheduler scheduler;
    @Getter
    private final List<IConnectedClient> clients = new ArrayList<>();
    private final Map<String, IInboundMessageHandler> susbcribers = new HashMap<>();
    @Getter
    private final MessageProcessor processor = new MessageProcessor();

    public Messenger(IAbstractPlugin plugin, String ip, int port) {
        this.plugin = plugin;
        this.scheduler = new PooledScheduler(plugin);
        GetLogger().info("Starting websocket server");

        wsServer = new MessengerWebsocketServer(this, new InetSocketAddress(ip, port));
    }

    protected abstract Logger GetLogger();
    protected abstract IConnectedClient CreateClient(WebSocket conn, String ident, ClientHandshake clientHandshake);
    protected abstract void OnClosed(IConnectedClient client, int code, String message);

    public void HandleOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        // this connection has already been verified
        String ident = clientHandshake.getFieldValue("ident");
        var client = CreateClient(webSocket, ident, clientHandshake);

        if (client == null) {
            GetLogger().severe("Unable to connect.");
            webSocket.close();
            return;
        }

        clients.add(client);
        GetLogger().info(String.format("§aServer %s connection established.", ident));
    }


    public void HandleClose(WebSocket conn, int code, String message) {
        var client = clients.stream()
                .filter(c -> c.GetConnection() == conn)
                .findFirst()
                .orElse(null);
        if (client == null) return;

        GetLogger().warning(String.format("[%s] Websocket closed with code %s: %s", client.GetId(), code, message));
        RemoveServer(client);

        OnClosed(client, code, message);
    }

    public IConnectedClient GetServer(WebSocket conn) {
        return clients.stream()
                .filter(c -> c.GetConnection() == conn)
                .findFirst().orElse(null);
    }

    public IConnectedClient GetServer(String ident) {
        return clients.stream()
                .filter(c -> c.GetId().equals(ident))
                .findFirst().orElse(null);
    }

    public void Subscribe(String action, IInboundMessageHandler callback) {
        susbcribers.put(action, callback);
    }

    
    public void ProcessMessage(IConnectedClient server, InboundMessage msg) {
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
                scheduler.RunAsync(() -> {
                    synchronized (susbcribers) {
                        try {
                            susbcribers.get(action).Handle(msg);
                        } catch (Exception e) {
                            GetLogger().severe(String.format("An error occured while processing a message (server=%s, action=%s)", server.GetId(), action));
                            e.printStackTrace();
                            msg.Reject(e.getMessage());
                        }
                    }

                    if (!msg.isProxyIgnore() && !msg.getForwardedServers().isEmpty()) {
                        for (String serverName : msg.getForwardedServers()) {
                            var target = GetServer(serverName);
                            var node = msg.GetNode(IConnectedClient.class);
                            if (target == null || target == node) return;

                            JsonWrapper json = new JsonWrapper(msg.getData().toString());
                            json.Set("__forwarded_by", node.GetId());
                            target.Send(json);
                        }
                    }
                });
            }
        }

    }

    public void RemoveServer(IConnectedClient server) {
        clients.remove(server);
    }

    public void Close() {
        for (var client : clients) {
            client.GetConnection().close(1012, "proxy restarting");
        }

        try {
            wsServer.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
