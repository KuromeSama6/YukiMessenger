package moe.protasis.yukimessenger.spigot;

import com.google.gson.JsonObject;
import lombok.Getter;
import moe.protasis.yukicommons.json.JsonWrapper;
import moe.protasis.yukimessenger.api.IInboundMessageHandler;
import moe.protasis.yukimessenger.api.impl.EndpointMethodHandler;
import moe.protasis.yukimessenger.api.impl.LegacyEndpointHandler;
import moe.protasis.yukimessenger.message.*;
import moe.protasis.yukimessenger.spigot.service.WSClient;
import org.bukkit.Bukkit;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;

public class MessengerClient implements IMessageNode {
    @Getter private static MessengerClient instance;
    @Getter
    private WSClient wsClient;
    @Getter
    @Deprecated
    private final Map<ServerBoundMessage, Consumer<ServerBoundMessage.Response>> retryQueue = new HashMap<>();
    @Deprecated
    private final Map<UUID, Consumer<ServerBoundMessage.Response>> callbacks = new HashMap<>();
    private final Map<String, IInboundMessageHandler> susbcribers = new HashMap<>();
    @Getter
    private final MessageProcessor processor = new MessageProcessor();

    public MessengerClient() {
        instance = this;
        YukiMessenger.GetLogger().info(String.format("§bStarting websocket client with ident %s", GetIdent()));
        wsClient = CreateClient();

        // reconnection timer
        long interval = YukiMessenger.config.GetLong("reconnectInterval", 5000);
        if (interval > 0) {
            YukiMessenger.GetLogger().info(String.format("§7Will reattempt websocket connection at interval %sms", interval));
            Bukkit.getScheduler().scheduleSyncRepeatingTask(YukiMessenger.getInstance(), this::AttemptReconnect, interval / 1000 * 20, interval / 1000 * 20);
        }
    }

    private void AttemptReconnect() {
        if (wsClient.isReady()) return;
        YukiMessenger.GetLogger().info("§7Attempting to reconnect websocket...");
        wsClient = CreateClient();
    }

    @Deprecated
    public void SendAsync(ServerBoundMessage message, Consumer<ServerBoundMessage.Response> callback) {
        if (!wsClient.isReady()) {
            YukiMessenger.GetLogger().warning("Attempted to send a message when the service is not ready. Queued it for retry.");
            retryQueue.put(message, callback);
            return;
        }

        if (callback != null) callbacks.put(message.id, callback);
        wsClient.send(message.data.toString());
    }

    public void SendAsync(OutboundMessage msg, Consumer<MessageResponse> callback) {
        if (!wsClient.isReady()) {
            YukiMessenger.GetLogger().warning("Attempted to send a message when the service is not ready. Queued it for retry.");
            processor.getRetryQueue().put(msg, callback);
            return;
        }

        if (callback != null) processor.getCallbacks().put(msg.getId(), callback);
        msg.getReceiver().Send(msg.getData());
    }

    public void FlushRetryQueue() {
        for (ServerBoundMessage msg : retryQueue.keySet()) {
            SendAsync(msg, retryQueue.get(msg));
        }

        retryQueue.clear();
    }

    @Deprecated
    public void Subscribe(String action, Consumer<JsonObject> callback) {
        susbcribers.put(action, new LegacyEndpointHandler(callback));
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
                try {
                    susbcribers.get(action).Handle(msg);
                } catch (Exception e) {
                    moe.protasis.yukimessenger.bungee.YukiMessenger.GetLogger().severe(String.format("An error occured while processing a message (action=%s)", action));
                    e.printStackTrace();
                    msg.Reject(e.getMessage());
                }
            }
        }
    }

    public String GetIdent() {
        String ret = YukiMessenger.config.GetString("ident");
        if (ret == null || ret.isEmpty()) {
            // use the folder name
            ret = YukiMessenger.getInstance()
                    .getDataFolder()
                    .getAbsoluteFile()
                    .getParentFile()
                    .getParentFile()
                    .getName();
        }

        return ret;
    }

    public void Close() {
        wsClient.close(1001, "downstream server restarting");
    }

    private WSClient CreateClient() {
        if (wsClient != null && wsClient.isOpen()) wsClient.close();

        String ip = YukiMessenger.config.GetString("ip", "127.0.0.1");
        int port = YukiMessenger.config.GetInt("port", 8633);
        String uri = String.format("ws://%s:%s", ip, port);
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("ident", GetIdent());
            headers.put("ip", Bukkit.getIp());
            headers.put("port", String.valueOf(Bukkit.getPort()));
            YukiMessenger.GetLogger().info(String.format("Websocket client starting on %s", uri));
            return new WSClient(new URI(uri), headers);
        } catch (URISyntaxException e) {
            YukiMessenger.GetLogger().severe(String.format("Unable to parse URI (%s)!", uri));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void Send(JsonWrapper data) {
        wsClient.send(data.toString());
    }
}
