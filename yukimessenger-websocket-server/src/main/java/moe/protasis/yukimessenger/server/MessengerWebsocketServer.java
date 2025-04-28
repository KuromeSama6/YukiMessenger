package moe.protasis.yukimessenger.server;

import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukimessenger.api.message.InboundMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class MessengerWebsocketServer extends WebSocketServer {
    private final Messenger messenger;

    public MessengerWebsocketServer(Messenger messenger, InetSocketAddress addr) {
        super(addr);
        this.messenger = messenger;
        setReuseAddr(true);

        AddStartServerScheduler();
    }
    
    @Override
    public void onStart() {
        messenger.GetLogger().info(String.format("§aWebsocket server running on %s", getAddress()));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        messenger.GetLogger().info(String.format("§eIncoming connection from %s", webSocket.getRemoteSocketAddress()));
        messenger.HandleOpen(webSocket, clientHandshake);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        messenger.HandleClose(webSocket, i, s);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        var server = messenger.GetServer(webSocket);
        if (server == null) return;

        messenger.ProcessMessage(server, new InboundMessage(server, new JsonWrapper(s)));
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        var server = messenger.GetServer(webSocket);
        messenger.GetLogger().severe(String.format("[%s] An error occured.", server.GetId()));
        e.printStackTrace();
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
        // handshake validation

        // check if there is an ident field
        String ident = request.getFieldValue("ident");
        if (ident == null) {
            messenger.GetLogger().severe("ident name was not found in the header of this connection! Rejecting.");
            throw new InvalidDataException(1002, "missing ident header");
        }

        return builder;
    }

    private void AddStartServerScheduler() {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
            messenger.GetLogger().warning(String.format("Port %s is in use. Retrying in 1 second.", getAddress().getPort()));
            messenger.getScheduler().Add(this::AddStartServerScheduler, 1000);
        }
    }
}
