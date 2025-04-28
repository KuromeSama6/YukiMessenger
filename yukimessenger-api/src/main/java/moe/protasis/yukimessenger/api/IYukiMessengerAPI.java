package moe.protasis.yukimessenger.api;

import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukimessenger.api.annotation.EndpointHandler;
import moe.protasis.yukimessenger.api.message.IConnectedClient;
import moe.protasis.yukimessenger.api.message.IMessageNode;
import moe.protasis.yukimessenger.api.message.MessageResponse;
import moe.protasis.yukimessenger.api.message.OutboundMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public interface IYukiMessengerAPI {
    boolean IsProxy();
    boolean IsReady();
    String GetIdent();
    Logger GetLogger();

    IMessageNode GetProxyNode();

    default <T extends IConnectedClient> T GetServer(Predicate<IConnectedClient> pred) {
        return (T)GetAllServers().stream()
                .filter(pred)
                .findFirst()
                .orElse(null);
    }
    default <T extends IConnectedClient> T GetServer(String name) {
        return GetServer(c -> c.GetId().equalsIgnoreCase(name));
    }

    <T extends IConnectedClient> List<T> GetAllServers();
    default <T extends IConnectedClient> List<T> GetAllServers(Predicate<IConnectedClient> pred) {
        return GetAllServers().stream()
                .filter(pred)
                .map(c -> (T)c)
                .collect(Collectors.toList());
    }
    default <T extends IConnectedClient> List<T> GetAllServers(String regex) {
        return GetAllServers(c -> c.GetId().matches(regex));
    }

    void Subscribe(EndpointHandler handler);
    void SendAsync(OutboundMessage msg, Consumer<MessageResponse> callback);

    default MessageResponse SendSync(OutboundMessage msg) {
        return SendSync(msg, 10000L);
    }
    default MessageResponse SendSync(OutboundMessage msg, long timeout) {
        CompletableFuture<MessageResponse> future = new CompletableFuture<>();

        SendAsync(msg, future::complete);

        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            return new MessageResponse(false, new JsonWrapper());
        }
    }

    static IYukiMessengerAPI Get() {
        return YukiMessengerAPI.Get();
    }
}
