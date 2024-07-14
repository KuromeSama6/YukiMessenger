package moe.protasis.yukimessenger.bungee.service;

import com.google.gson.JsonParser;
import moe.protasis.yukicommons.json.JsonWrapper;
import moe.protasis.yukimessenger.bungee.MessengerServer;
import moe.protasis.yukimessenger.bungee.YukiMessenger;
import moe.protasis.yukimessenger.bungee.event.DownstreamServerDisconnectEvent;
import moe.protasis.yukimessenger.message.InboundMessage;
import moe.protasis.yukimessenger.util.EnvUtil;
import moe.protasis.yukimessenger.util.PortKiller;
import moe.protasis.yukimessenger.util.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class WSServer extends WebSocketServer {
    public WSServer(InetSocketAddress addr) {
        super(addr);
        setReuseAddr(true);

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

        MessengerServer.getInstance().ProcessMessage(server, new InboundMessage(server, new JsonWrapper(s)));
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        SpigotServer server = MessengerServer.getInstance().GetServer(webSocket);
        YukiMessenger.GetLogger().severe(String.format("[%s] An error occured.", server.identName));
        e.printStackTrace();
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
        // handshake validation

        // check if there is an ident field
        String ident = request.getFieldValue("ident");
        if (ident == null) {
            YukiMessenger.GetLogger().severe("ident name was not found in the header of this connection! Rejecting.");
            throw new InvalidDataException(1002, "missing ident header");
        }

        // check if the server is listed
        YukiMessenger.GetLogger().info(String.format("Ident name is %s", ident));
        ServerInfo server = ProxyServer.getInstance().getServerInfo(ident);
        String ip = request.getFieldValue("ip");
//        int port = Integer.parseInt(request.getFieldValue("port"));

        if (server == null) {
            YukiMessenger.GetLogger().severe("No server with that name defined");
            throw new InvalidDataException(1002, "server not found");
        }

        return builder;
    }

    private void AddStartServerScheduler() {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
            YukiMessenger.GetLogger().warning(String.format("Port %s is in use. Retrying in 1 second.", getAddress().getPort()));
            ProxyServer.getInstance().getScheduler().schedule(YukiMessenger.getInstance(), this::AddStartServerScheduler, 1, TimeUnit.SECONDS);
        }
//        if (EnvUtil.CheckPortUsable(getAddress().getPort())) {
//            start();
//        } else {
//            YukiMessenger.GetLogger().warning(String.format("Port %s is in use. Attempting to kill it and retrying in 1 second.", getAddress().getPort()));
////            try {
////                Util.KillProcess(Util.GetPidByPort(getAddress().getPort()));
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//            ProxyServer.getInstance().getScheduler().schedule(YukiMessenger.getInstance(), this::AddStartServerScheduler, 1, TimeUnit.SECONDS);
//        }
    }
}
