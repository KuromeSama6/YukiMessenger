package moe.protasis.yukimessenger.spigot.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;
import moe.protasis.yukimessenger.bungee.MessengerServer;
import moe.protasis.yukimessenger.spigot.MessengerClient;
import moe.protasis.yukimessenger.spigot.YukiMessenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        MessengerClient.getInstance().ReceiveMessage(new Gson().fromJson(s, JsonElement.class).getAsJsonObject());
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        YukiMessenger.GetLogger().warning(String.format("Websocket closed with code %s: %s", i, s));
        isReady = false;
        // kick all players
        if (YukiMessenger.config.getBoolean("kickOnDisconnect", true)) {
            for (Player player : Bukkit.getOnlinePlayers())
                player.kickPlayer("messenger connection dropped");
        }
    }

    @Override
    public void onError(Exception e) {
        YukiMessenger.GetLogger().severe("An error occured in the websocket client.");
        e.printStackTrace();
    }
}
