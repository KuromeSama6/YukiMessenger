package moe.protasis.yukimessenger.api.message;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.Getter;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukimessenger.api.IYukiMessengerAPI;

import java.util.Arrays;
import java.util.UUID;

@Getter
public class OutboundMessage {
    private final UUID id;
    private final String action;
    private final JsonWrapper data;
    private final MessageDestination destination;
    private IMessageNode receiver;

    private OutboundMessage(IMessageNode receiver, MessageDestination destination, String action, JsonWrapper data) {
        id = UuidCreator.getTimeBased();
        this.action = action;
        this.data = data;

        data.Set("__id", id.toString());
        data.Set("__action", action);

        this.destination = MessageDestination.SERVER;
        this.receiver = receiver;
    }

    public static OutboundMessage ToProxy(String action, JsonWrapper data) {
        return new OutboundMessage(IYukiMessengerAPI.Get().GetProxyNode(), MessageDestination.SERVER, action, data);
    }

    public static OutboundMessage ToClient(IConnectedClient receiver, String action, JsonWrapper data) {
        return new OutboundMessage(receiver, MessageDestination.CLIENT, action, data);
    }

    public static OutboundMessage ToOtherServers(String action, JsonWrapper data, String... servers) {
        var ret = ToProxyAndOtherServers(action, data, servers);
        data.Set("__proxy_ignore", true);
        return ret;
    }
    public static OutboundMessage ToProxyAndOtherServers(String action, JsonWrapper data, String... servers) {
        var ret = new OutboundMessage(IYukiMessengerAPI.Get().GetProxyNode(), MessageDestination.SERVER, action, data);
        data.Set("__forward_to", Arrays.asList(servers));
        return ret;
    }


}
