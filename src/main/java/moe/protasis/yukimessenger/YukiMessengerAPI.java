package moe.protasis.yukimessenger;

import com.google.gson.JsonObject;
import moe.protasis.yukimessenger.bungee.MessengerServer;
import moe.protasis.yukimessenger.bungee.YukiMessenger;
import moe.protasis.yukimessenger.bungee.service.InboundMessage;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import moe.protasis.yukimessenger.spigot.MessengerClient;
import moe.protasis.yukimessenger.spigot.service.ServerboundMessage;
import moe.protasis.yukimessenger.util.EnvUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import okio.Timeout;
import oracle.jdbc.proxy.annotation.Pre;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class YukiMessengerAPI {
    /**
     * Whether this instance is running on the proxy sever or a spigot server.
     * If true, this instance is running on the proxy.
     * Otherwise, this instance is running on a spigot server.
     */
    public final boolean isProxy;

    /**
     * <b>Spigot only.</b>
     * Gets the ident name of the server that this YukiMessenger instance is running on.
     * @return The ident name.
     */
    public String GetIdent() {
        EnvUtil.EnsureEnv(false);
        return MessengerClient.getInstance().GetIdent();
    }

    /**
     * <b>Proxy only.</b>
     * Gets the first connected SpigotServer instance matching the predicate.
     * @param pred The predicate to match against.
     * @return The SpigotServer. Null if not found.
     */
    public SpigotServer GetServer(Predicate<SpigotServer> pred) {
        EnvUtil.EnsureEnv(true);
        return MessengerServer.getInstance().getClients().stream()
                .filter(pred)
                .findFirst().orElse(null);
    }

    public SpigotServer GetServer(String name) {
        return GetServer(c -> c.identName.matches(name));
    }
    public SpigotServer GetServer(ServerInfo server) {
        return GetServer(c -> c.spigot == server);
    }

    /**
     * <b>Proxy only.</b>
     * Gets a list of all connected SpigotServer instances.
     * @return The list.
     */
    public List<SpigotServer> GetAllServers() {
        EnvUtil.EnsureEnv(true);
        return MessengerServer.getInstance().getClients();
    }

    public List<SpigotServer> GetAllServers(Predicate<SpigotServer> pred) {
        EnvUtil.EnsureEnv(true);
        return MessengerServer.getInstance().getClients().stream()
                .filter(pred)
                .collect(Collectors.toList());
    }

    public List<SpigotServer> GetAllServers(String regex) {
        EnvUtil.EnsureEnv(true);
        return GetAllServers(c -> c.identName.matches(regex));
    }

    /**
     * <b>Proxy only.</b>
     * Subscribes to an action.
     * @param action The action to subscribe to.
     * @param callback The inbound message when this action is triggered.
     */
    public void SubscribeServerbound(String action, Consumer<InboundMessage> callback) {
        EnvUtil.EnsureEnv(true);
        MessengerServer.getInstance().Subscribe(action, callback);
    }

    /**
     * <b>Spigot only.</b>
     * Subscribes to an action.
     * @param action The action to subscribe to.
     * @param callback The inbound message when this action is triggered.
     */
    public void SubscribeClientbound(String action, Consumer<JsonObject> callback) {
        EnvUtil.EnsureEnv(false);
        MessengerClient.getInstance().Subscribe(action, callback);
    }

    /**
     * <b>Spigot only.</b>
     * Sends a request to the proxy server in a non-blocking fashion.
     * @param msg The message.
     * @param callback The callback that is invoked when a response is received. If null, responses will not be processed.
     */
    public void SendAsync(ServerboundMessage msg, Consumer<ServerboundMessage.Response> callback) {
        EnvUtil.EnsureEnv(false);
        MessengerClient.getInstance().SendAsync(msg, callback);
    }

    /**
     *<b>Spigot only. BLOCKING.</b>
     * Blocking implementation of SendAsync.
     * @param msg The message.
     * @param timeout Time (ms) to wait before failing. A failed request will return a <code>ServerboundMessage.Response</code>
     *                object with its <code>processed</code> field set to <code>false</code>.
     * @return The callback that is invoked when a response is received.
     */
    public ServerboundMessage.Response SendSync(ServerboundMessage msg, long timeout) {
        EnvUtil.EnsureEnv(false);
        CompletableFuture<ServerboundMessage.Response> future = new CompletableFuture<>();

        SendAsync(msg, future::complete);

        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            return new ServerboundMessage.Response(false, null);
        }

    }

    public YukiMessengerAPI(boolean isProxy) {
        this.isProxy = isProxy;
    }
}
