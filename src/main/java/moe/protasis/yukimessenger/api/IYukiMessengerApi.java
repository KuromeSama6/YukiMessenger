package moe.protasis.yukimessenger.api;

import moe.protasis.yukicommons.json.JsonWrapper;
import moe.protasis.yukicommons.util.EnvironmentType;
import moe.protasis.yukicommons.util.Util;
import moe.protasis.yukimessenger.annotation.EndpointHandler;
import moe.protasis.yukimessenger.api.impl.MessengerApiBukkit;
import moe.protasis.yukimessenger.api.impl.MessengerApiBungeecord;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import moe.protasis.yukimessenger.message.MessageResponse;
import moe.protasis.yukimessenger.message.OutboundMessage;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public interface IYukiMessengerApi {
    boolean IsProxy();
    boolean IsReady();
    String GetIdent();
    Logger GetLogger();
    default SpigotServer GetServer(Predicate<SpigotServer> pred) {
        return GetAllServers().stream()
                .filter(pred)
                .findFirst().orElse(null);
    }
    default SpigotServer GetServer(String name) {
        return GetServer(c -> c.identName.equalsIgnoreCase(name));
    }
    default SpigotServer GetServer(ServerInfo server) {
        return GetServer(server.getName());
    }

    List<SpigotServer> GetAllServers();
    default List<SpigotServer> GetAllServers(Predicate<SpigotServer> pred) {
        return GetAllServers().stream()
                .filter(pred)
                .collect(Collectors.toList());
    }
    default List<SpigotServer> GetAllServers(String regex) {
        return GetAllServers(c -> c.identName.matches(regex));
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

    static IYukiMessengerApi Get() {
        if (Util.GetEnvironment() == EnvironmentType.PROXY) return new MessengerApiBungeecord();
        else return new MessengerApiBukkit();
    }
}
