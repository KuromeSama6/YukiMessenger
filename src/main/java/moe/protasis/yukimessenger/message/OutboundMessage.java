package moe.protasis.yukimessenger.message;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.Getter;
import lombok.var;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukicommons.util.EnvironmentType;
import moe.protasis.yukicommons.util.Util;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import moe.protasis.yukimessenger.spigot.MessengerClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class OutboundMessage {
    private final UUID id;
    private final String action;
    private final JsonWrapper data;
    private final MessageDestination destination;
    private IMessageNode receiver;

    private OutboundMessage(String action) {
        this(action, new JsonWrapper());
    }

    private OutboundMessage(String action, JsonWrapper data) {
        id = UuidCreator.getTimeBased();
        this.action = action;
        this.data = data;

        data.Set("__id", id.toString());
        data.Set("__action", action);

        destination = MessageDestination.CLIENT;
        receiver = MessengerClient.getInstance();
    }

    private OutboundMessage(SpigotServer receiver, String action, JsonWrapper data) {
        id = UuidCreator.getTimeBased();
        this.action = action;
        this.data = data;

        data.Set("__id", id.toString());
        data.Set("__action", action);

        destination = MessageDestination.SERVER;
        this.receiver = receiver;
    }

    public static OutboundMessage ToProxy(String action, JsonWrapper data) {
        return new OutboundMessage(action, data);
    }
    public static OutboundMessage ToOtherServers(String action, JsonWrapper data, String... servers) {
        var ret = ToProxyAndOtherServers(action, data, servers);
        data.Set("__proxy_ignore", true);
        return ret;
    }
    public static OutboundMessage ToProxyAndOtherServers(String action, JsonWrapper data, String... servers) {
        var ret = new OutboundMessage(action, data);
        data.Set("__forward_to", Arrays.asList(servers));
        return ret;
    }

    public static OutboundMessage ToClient(SpigotServer receiver, String action, JsonWrapper data) {
        return new OutboundMessage(receiver, action, data);
    }

}
