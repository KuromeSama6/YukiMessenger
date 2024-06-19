package moe.protasis.yukimessenger.message;

import com.google.gson.JsonObject;
import moe.protasis.yukimessenger.bungee.YukiMessenger;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import moe.protasis.yukimessenger.util.JsonObjectBuilder;

import java.util.UUID;

@Deprecated
public class ServerInboundMessage {
    public final SpigotServer source;
    public final JsonObject data;
    public final UUID id;

    public ServerInboundMessage(SpigotServer source, JsonObject data) {
        this.source = source;
        this.data = data;

        if (data.has("__id")) id = UUID.fromString(data.get("__id").getAsString());
        else id = null;
    }

    /**
     * <b>Proxy only.</b>
     * Sends a "response" to this message to the server.
     * A handler (callback) must be passed in to MessengerClient#SendAsync/YukiMessengerAPI#SendAsync
     * for this response to be processed.
     * @param data The data of this response
     * @throws SquawkWithoutIdentException If a callback id was not present in the server-bound message.
     */
    public void Squawk(JsonObject data) throws SquawkWithoutIdentException {
        if (id == null) throw new SquawkWithoutIdentException();
        data.addProperty("__id", id.toString());

        source.conn.send(data.toString());
    }

    /**
     * <b>Proxy only.</b>
     * Rejects this message from a client.
     * On the client, the <code>processed</code> field of the <code>ServerboundMessage</code>
     * will be set to false.
     * @param msg The reason for rejection.
     */
    public void Reject(String msg) {
        YukiMessenger.GetLogger().severe(String.format("An inbound message was rejected: %s", msg));

        Squawk(new JsonObjectBuilder()
                .put("__error", true)
                .put("__error_message", msg)
                .finish());
    }

    private static class SquawkWithoutIdentException extends RuntimeException {}
}
