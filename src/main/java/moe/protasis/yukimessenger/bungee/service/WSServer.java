package moe.protasis.yukimessenger.bungee.service;

import com.google.gson.JsonParser;
import moe.protasis.yukimessenger.bungee.MessengerServer;
import moe.protasis.yukimessenger.bungee.YukiMessenger;
import moe.protasis.yukimessenger.bungee.event.DownstreamServerDisconnectEvent;
import moe.protasis.yukimessenger.util.EnvUtil;
import moe.protasis.yukimessenger.util.PortKiller;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class WSServer extends WebSocketServer {
    public WSServer(InetSocketAddress addr) {
        super(addr);

        AddStartServerScheduler();
    }

    @Override
    public void onStart() {
        YukiMessenger.GetLogger().info(String.format("§aWebsocket server running on %s", getAddress()));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        YukiMessenger.GetLogger().info(String.format("§eIncoming connection from %s", webSocket.getRemoteSocketAddress()));
        MessengerServer.getInstance().ProcessIncomingConnection(webSocket, clientHandshake);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        SpigotServer server = MessengerServer.getInstance().GetServer(webSocket);
        YukiMessenger.GetLogger().warning(String.format("[%s] Websocket closed with code %s: %s", server.identName, i, s));
        MessengerServer.getInstance().RemoveServer(server);

        DownstreamServerDisconnectEvent e = new DownstreamServerDisconnectEvent(server, i, s);
        ProxyServer.getInstance().getPluginManager().callEvent(e);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        SpigotServer server = MessengerServer.getInstance().GetServer(webSocket);
        if (server == null) return;

        MessengerServer.getInstance().ProcessMessage(server, JsonParser.parseString(s).getAsJsonObject());
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        SpigotServer server = MessengerServer.getInstance().GetServer(webSocket);
        YukiMessenger.GetLogger().severe(String.format("[%s] An error occured.", server.identName));
        e.printStackTrace();
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer( conn, draft, request );
        // handshake validation

        // check if there is an ident field
        String ident = request.getFieldValue("ident");
        if (ident == null) {
            YukiMessenger.GetLogger().severe("ident name was not found in the header of this connection! Rejecting.");
            throw new InvalidDataException(1002, "missing ident header");
        }

        // check if the server is listed
        ServerInfo server = ProxyServer.getInstance().getServerInfo(ident);
        if (server == null) {
            YukiMessenger.GetLogger().severe(String.format("[%s] is not found in this proxy's server list!" +
                    "Check if you have it listed in config.yml. Rejecting.", ident));
            throw new InvalidDataException(1002, "server not found");
        }

        return builder;
    }

    private void AddStartServerScheduler() {
        if (EnvUtil.CheckPortUsable(getAddress().getPort())) {
            start();
        } else {
            YukiMessenger.GetLogger().warning(String.format("Port %s is in use. Retrying in 1 second.", getAddress().getPort()));
            try {
                PortKiller.KillProcessByPort(getAddress().getPort());
            } catch (Exception ignored) {}
            ProxyServer.getInstance().getScheduler().schedule(YukiMessenger.getInstance(), this::AddStartServerScheduler, 1, TimeUnit.SECONDS);
        }
    }
}
