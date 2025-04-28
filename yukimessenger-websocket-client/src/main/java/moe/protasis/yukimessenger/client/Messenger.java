package moe.protasis.yukimessenger.client;

import lombok.Getter;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukicommons.api.plugin.IAbstractPlugin;
import moe.protasis.yukicommons.api.scheduler.PooledScheduler;
import moe.protasis.yukimessenger.api.IInboundMessageHandler;
import moe.protasis.yukimessenger.api.message.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public abstract class Messenger implements IMessageNode {
    @Getter
    private MessengerWebsocketClient wsClient;
    private final Map<String, IInboundMessageHandler> susbcribers = new HashMap<>();
    @Getter
    private final MessageProcessor processor = new MessageProcessor();
    private final PooledScheduler scheduler;

    public Messenger(IAbstractPlugin plugin, long reconnectInterval) {
        GetLogger().info(String.format("§bStarting websocket client with ident %s", GetIdent()));
        wsClient = CreateClient();
        scheduler = new PooledScheduler(plugin);

        if (reconnectInterval > 0) {
            scheduler.AddRepeating(this::AttemptReconnect, reconnectInterval / 1000 * 20, reconnectInterval / 1000 * 20);
        }
    }


    protected abstract Logger GetLogger();
    protected abstract void OnDisconnect();

    private void AttemptReconnect() {
        if (wsClient.isReady()) return;
        GetLogger().info("§7Attempting to reconnect websocket...");
        wsClient = CreateClient();
    }

    public void SendAsync(OutboundMessage msg, Consumer<MessageResponse> callback) {
        if (!wsClient.isReady()) {
            GetLogger().warning("Attempted to send a message when the service is not ready. Queued it for retry.");
            processor.getRetryQueue().put(msg, callback);
            return;
        }

        if (callback != null) processor.getCallbacks().put(msg.getId(), callback);
        msg.getReceiver().Send(msg.getData());
    }

    public void FlushRetryQueue() {
        //TODO Implement
    }

    public void Subscribe(String action, IInboundMessageHandler callback) {
        susbcribers.put(action, callback);
    }

    public void ReceiveMessage(InboundMessage msg) {
        JsonWrapper data = msg.getData();
        if (msg.getId() != null) {
            // idented message for a request
            boolean hasError = data.Has("__error");

            MessageResponse response = new MessageResponse(!hasError, data);
            processor.HandleResponse(msg.getId(), response);
        }

        if (msg.getAction() != null) {
            // unidented message
            String action = msg.getAction();
            if (susbcribers.containsKey(action)) {
                scheduler.RunAsync(() -> {
                    synchronized (susbcribers) {
                        try {
                            susbcribers.get(action).Handle(msg);
                        } catch (Exception e) {
                            GetLogger().severe(String.format("An error occured while processing a message (action=%s)", action));
                            e.printStackTrace();
                            msg.Reject(e.getMessage());
                        }
                    }
                });
            }
        }
    }

    public abstract String GetIdent();
    protected abstract String GetIP();
    protected abstract String GetClientIP();
    protected abstract int GetClientPort();
    protected abstract int GetPort();

    public void Close() {
        wsClient.close(1001, "downstream server restarting");
    }

    private MessengerWebsocketClient CreateClient() {
        if (wsClient != null && wsClient.isOpen()) wsClient.close();

        String ip = GetIP();
        int port = GetPort();
        String uri = String.format("ws://%s:%s", ip, port);
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("ident", GetIdent());
            headers.put("ip", GetClientIP());
            headers.put("port", String.valueOf(GetClientPort()));
            GetLogger().info(String.format("Websocket client starting on %s", uri));
            return new MessengerWebsocketClient(this, new URI(uri), headers);
        } catch (URISyntaxException e) {
            GetLogger().severe(String.format("Unable to parse URI (%s)!", uri));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void Send(JsonWrapper data) {
        wsClient.send(data.toString());
    }
}
