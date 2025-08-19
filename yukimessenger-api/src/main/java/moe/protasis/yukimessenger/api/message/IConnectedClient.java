package moe.protasis.yukimessenger.api.message;

import org.java_websocket.WebSocket;

public interface IConnectedClient extends IMessageNode {
    WebSocket GetConnection();
}
