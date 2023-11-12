package moe.protasis.yukimessenger.spigot.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.UUID;

/**
 * Represents a message that is sent from a client (a Spigot instance) to the server (the proxy).
 */
public class ServerboundMessage {
    public final String action;
    public final JsonObject data;
    public final UUID id;

    public ServerboundMessage(String action, JsonObject data) {
        this.action = action;
        this.data = data;
        this.id = UuidCreator.getTimeBased();

        data.addProperty("__id", id.toString());
        data.addProperty("__action", action);
    }

    public static class Response {
        /**
         * Whether this response had made it to the proxy or not.
         */
        public final boolean processed;
        public final JsonObject data;

        public Response(boolean processed, JsonObject data) {
            this.processed = processed;
            this.data = data;
        }
    }

}
