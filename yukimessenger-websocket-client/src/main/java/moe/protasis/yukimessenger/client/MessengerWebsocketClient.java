package moe.protasis.yukimessenger.client;

import lombok.Getter;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukimessenger.api.message.InboundMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

public class MessengerWebsocketClient extends WebSocketClient {
    private final Messenger messenger;
    @Getter
    private boolean isReady;
    private final Logger logger;

    public MessengerWebsocketClient(Messenger messenger, URI serverUri, Map<String, String> headers) {
        super(serverUri, headers);
        this.messenger = messenger;
        this.logger = messenger.GetLogger();
        connect();
    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info(String.format("§bConnected to §f%s", getURI()));
        isReady = true;
        messenger.FlushRetryQueue();
    }

    @Override
    public void onMessage(String s) {
        JsonWrapper data = new JsonWrapper(s);
        messenger.ReceiveMessage(new InboundMessage(messenger, data));
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.warning(String.format("Websocket closed with code %s: %s", i, s));
        isReady = false;
    }

    @Override
    public void onError(Exception e) {
        logger.severe("An error occured in the websocket client.");
        e.printStackTrace();
    }
}
