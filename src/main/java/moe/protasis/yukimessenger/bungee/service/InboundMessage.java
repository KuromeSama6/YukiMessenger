package moe.protasis.yukimessenger.bungee.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import moe.protasis.yukimessenger.bungee.MessengerServer;

import java.util.UUID;

public class InboundMessage {
    public final SpigotServer source;
    public final JsonNode data;
    public final UUID id;

    public InboundMessage(SpigotServer source, JsonNode data) {
        this.source = source;
        this.data = data;

        if (data.has("__id")) id = UUID.fromString(data.get("__id").textValue());
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
    public void Squawk(ObjectNode data) throws SquawkWithoutIdentException {
        if (id == null) throw new SquawkWithoutIdentException();
        data.put("__id", id.toString());

        source.conn.send(data.toString());
    }

    private static class SquawkWithoutIdentException extends RuntimeException {}
}
