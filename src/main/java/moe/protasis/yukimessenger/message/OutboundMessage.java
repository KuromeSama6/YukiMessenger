package moe.protasis.yukimessenger.message;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.Getter;
import moe.protasis.yukicommons.json.JsonWrapper;
import moe.protasis.yukicommons.util.EnvironmentType;
import moe.protasis.yukicommons.util.Util;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import moe.protasis.yukimessenger.spigot.MessengerClient;

import java.util.UUID;

@Getter
public class OutboundMessage {
    private final UUID id;
    private final String action;
    private final JsonWrapper data;
    private final MessageDestination destination;
    private IMessageNode receiver;

    public OutboundMessage(String action) {
        this(action, new JsonWrapper());
    }

    public OutboundMessage(String action, JsonWrapper data) {
        id = UuidCreator.getTimeBased();
        this.action = action;
        this.data = data;

        data.Set("__id", id.toString());
        data.Set("__action", action);

        destination = MessageDestination.CLIENT;
        receiver = MessengerClient.getInstance();
    }

    public OutboundMessage(SpigotServer receiver, String action, JsonWrapper data) {
        id = UuidCreator.getTimeBased();
        this.action = action;
        this.data = data;

        data.Set("__id", id.toString());
        data.Set("__action", action);

        destination = MessageDestination.SERVER;
        this.receiver = receiver;
    }
}
