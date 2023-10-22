package moe.protasis.yukimessenger.bungee.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.protasis.yukimessenger.bungee.MessengerServer;
import moe.protasis.yukimessenger.bungee.YukiMessenger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WSServer extends WebSocketServer {
    public WSServer(InetSocketAddress addr) {
        super(addr);
        start();
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
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        SpigotServer server = MessengerServer.getInstance().GetServer(webSocket);
        if (server == null) return;

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode data = mapper.readTree(s);
            MessengerServer.getInstance().ProcessMessage(server, data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
}
