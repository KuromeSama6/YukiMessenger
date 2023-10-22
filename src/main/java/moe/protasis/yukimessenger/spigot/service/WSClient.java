package moe.protasis.yukimessenger.spigot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import lombok.Getter;
import moe.protasis.yukimessenger.bungee.MessengerServer;
import moe.protasis.yukimessenger.spigot.MessengerClient;
import moe.protasis.yukimessenger.spigot.YukiMessenger;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WSClient extends WebSocketClient {
    @Getter private boolean isReady;

    public WSClient(URI serverUri,  Map<String, String> headers) {
        super(serverUri, headers);
        connect();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        YukiMessenger.GetLogger().info(String.format("§bConnected to §f%s", getURI()));
        isReady = true;
        MessengerClient.getInstance().FlushRetryQueue();
    }

    @Override
    public void onMessage(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            MessengerClient.getInstance().ReceiveMessage(mapper.readTree(s));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        YukiMessenger.GetLogger().warning(String.format("Websocket closed with code %s: %s", i, s));
        isReady = false;
    }

    @Override
    public void onError(Exception e) {
        YukiMessenger.GetLogger().severe("An error occured in the websocket client.");
        e.printStackTrace();
    }
}
