package moe.protasis.yukimessenger.api.message;

import org.java_websocket.WebSocket;

public interface IConnectedClient extends IMessageNode {
    String GetId();
    WebSocket GetConnection();
}
