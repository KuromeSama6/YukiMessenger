package moe.protasis.yukimessenger.api;

import com.google.gson.JsonObject;
import moe.protasis.yukimessenger.bungee.MessengerServer;
import moe.protasis.yukimessenger.annotation.EndpointHandler;
import moe.protasis.yukimessenger.annotation.MessageHandler;
import moe.protasis.yukimessenger.api.impl.EndpointMethodHandler;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import moe.protasis.yukimessenger.spigot.MessengerClient;
import moe.protasis.yukimessenger.message.ServerBoundMessage;
import moe.protasis.yukimessenger.util.EnvUtil;
import net.md_5.bungee.api.config.ServerInfo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Deprecated
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
     * @deprecated Please use the <code>SubscribeServerbound</code> and <code>HandlerMethod</code> to
     * listen for events.
     * @see MessageHandler
     * @see EndpointHandler
     */
    @Deprecated
    public void SubscribeServerbound(String action, IInboundMessageHandler callback) {
        EnvUtil.EnsureEnv(true);
        MessengerServer.getInstance().Subscribe(action, callback);
    }

    /**
     * <b>Proxy only.</b>
     * Subscribes to all actions specified by each of the <code>HandlerMethod</code> annotations on
     * methods of this class. The method corresponding to the annotation will be called.
     * @param handler The <code>EndpointHandler</code> instance.
     * @see EndpointHandler
     * @see MessageHandler
     */
    public void SubscribeServerbound(EndpointHandler handler) {
        EnvUtil.EnsureEnv(true);
        try {
            for (Method method : handler.getClass().getDeclaredMethods()) {
                MessageHandler annotation = method.getAnnotation(MessageHandler.class);
                if (annotation == null) continue;
                MessengerServer.getInstance().Subscribe(annotation.value(), new EndpointMethodHandler(handler, method));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    public void SendAsync(ServerBoundMessage msg, Consumer<ServerBoundMessage.Response> callback) {
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
    public ServerBoundMessage.Response SendSync(ServerBoundMessage msg, long timeout) {
        EnvUtil.EnsureEnv(false);
        CompletableFuture<ServerBoundMessage.Response> future = new CompletableFuture<>();

        SendAsync(msg, future::complete);

        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            return new ServerBoundMessage.Response(false, null);
        }

    }

    public YukiMessengerAPI(boolean isProxy) {
        this.isProxy = isProxy;
    }
}
