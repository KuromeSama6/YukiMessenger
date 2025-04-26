package moe.protasis.yukimessenger.message;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.ToString;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukicommons.util.EnvironmentType;
import moe.protasis.yukicommons.util.Util;
import moe.protasis.yukimessenger.api.IYukiMessengerApi;
import moe.protasis.yukimessenger.bungee.YukiMessenger;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;

import java.util.List;
import java.util.UUID;

@ToString
public class InboundMessage {
    private final MessageDestination destination;
    @Getter
    private final IMessageNode source;
    @Getter
    private final JsonWrapper data;
    @Getter
    private final UUID id;
    @Getter
    private final String action;
    @Getter
    private final List<String> forwardedServers;
    @Getter
    private final boolean proxyIgnore, error;
    @Getter
    private final String errorMessage;

    public InboundMessage(IMessageNode source, JsonWrapper data) {
        destination = Util.GetEnvironment() == EnvironmentType.BUNGEECORD ? MessageDestination.SERVER : MessageDestination.CLIENT;
        this.source = source;
        this.data = data;

        id = data.GetUuid("__id");
        action = data.GetString("__action");
        proxyIgnore = data.GetBool("__proxy_ignore");
        forwardedServers = data.GetList("__forwarded_to", String.class);

        error = data.GetBool("__error");
        errorMessage = data.GetString("__error_message");

        data.Set("__id", null);
        data.Set("__action", null);
    }

    /**
     * <b>Proxy only.</b>
     * Sends a "response" to this message to the server.
     * A handler (callback) must be passed in to MessengerClient#SendAsync/YukiMessengerAPI#SendAsync
     * for this response to be processed.
     * @param data The data of this response
     */
    public void Respond(JsonWrapper data) {
        data.Set("__id", id.toString());
        source.Send(data);
    }

    /**
     * <b>Proxy only.</b>
     * Rejects this message from a client.
     * On the client, the <code>processed</code> field of the <code>ServerboundMessage</code>
     * will be set to false.
     * @param msg The reason for rejection.
     */
    public void Reject(String msg) {
        IYukiMessengerApi.Get().GetLogger().severe(String.format("An inbound message was rejected: %s", msg));

        JsonWrapper res = new JsonWrapper();
        res.Set("__error", true);
        res.Set("__error_message", msg);
        Respond(res);
    }

    public SpigotServer GetSpigot() {
        return (SpigotServer)source;
    }
}
