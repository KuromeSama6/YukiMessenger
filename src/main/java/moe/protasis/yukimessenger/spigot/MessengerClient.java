package moe.protasis.yukimessenger.spigot;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import moe.protasis.yukimessenger.bungee.service.InboundMessage;
import moe.protasis.yukimessenger.spigot.service.ServerboundMessage;
import moe.protasis.yukimessenger.spigot.service.WSClient;
import moe.protasis.yukimessenger.util.ObjectNodeBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;

public class MessengerClient {
    @Getter private static MessengerClient instance;
    private WSClient wsClient;
    private final Map<ServerboundMessage, Consumer<ServerboundMessage.Response>> retryQueue = new HashMap<>();
    private final Map<UUID, Consumer<ServerboundMessage.Response>> callbacks = new HashMap<>();
    private final Map<String, Consumer<JsonNode>> susbcribers = new HashMap<>();


    public MessengerClient() {
        instance = this;

        String ident = GetIdent();
        YukiMessenger.GetLogger().info(String.format("§bStarting websocket client with ident %s", ident));

        String ip = YukiMessenger.config.getString("ip", "127.0.0.1");
        String port = YukiMessenger.config.getString("port", "8633");
        String uri = String.format("ws://%s:%s", ip, port);
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("ident", ident);
            wsClient = new WSClient(new URI(uri), headers);
        } catch (URISyntaxException e) {
            YukiMessenger.GetLogger().severe(String.format("Unable to parse URI (%s)!", uri));
            e.printStackTrace();
            return;
        }

        YukiMessenger.GetLogger().info(String.format("Websocket client started on %s", uri));

    }

    public void SendAsync(ServerboundMessage message, Consumer<ServerboundMessage.Response> callback) {
        if (!wsClient.isReady()) {
            YukiMessenger.GetLogger().warning("Attempted to send a message when the service is not ready. Queued it for retry.");
            retryQueue.put(message, callback);
            return;
        }

        if (callback != null) callbacks.put(message.id, callback);
        wsClient.send(message.data.toString());
    }

    public void FlushRetryQueue() {
        for (ServerboundMessage msg : retryQueue.keySet()) {
            SendAsync(msg, retryQueue.get(msg));
        }

        retryQueue.clear();
    }

    public void Subscribe(String action, Consumer<JsonNode> callback) {
        susbcribers.put(action, callback);
    }

    public void ReceiveMessage(JsonNode data) {
        if (data.has("__id")) {
            // idented message for a request
            UUID id = UUID.fromString(data.get("__id").textValue());
            ServerboundMessage.Response response = new ServerboundMessage.Response(true, data);
            Consumer<ServerboundMessage.Response> callback = callbacks.get(id);
            if (callback != null) {
                callback.accept(response);
                callbacks.remove(id);
            }
        }

        if (data.has("__action")) {
            // unidented message
            String action = data.get("__action").textValue();
            Consumer<JsonNode> callback = susbcribers.get(action);
            if (callback != null) callback.accept(data);
        }
    }

    public String GetIdent() {
        String ret = YukiMessenger.config.getString("ident");
        if (ret == null) {
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
}
