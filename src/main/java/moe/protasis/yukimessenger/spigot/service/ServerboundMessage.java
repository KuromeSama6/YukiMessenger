package moe.protasis.yukimessenger.spigot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.f4b6a3.uuid.UuidCreator;
import lombok.Getter;

import java.util.UUID;

/**
 * Represents a message that is sent from a client (a Spigot instance) to the server (the proxy).
 */
public class ServerboundMessage {
    public final String action;
    public final ObjectNode data;
    public final UUID id;

    public ServerboundMessage(String action, ObjectNode data) {
        this.action = action;
        this.data = data;
        this.id = UuidCreator.getTimeBased();

        data.put("__id", id.toString());
        data.put("__action", action);
    }

    public static class Response {
        /**
         * Whether this response had made it to the proxy or not.
         */
        public final boolean processed;
        public final JsonNode data;

        public Response(boolean processed, JsonNode data) {
            this.processed = processed;
            this.data = data;
        }
    }

}
