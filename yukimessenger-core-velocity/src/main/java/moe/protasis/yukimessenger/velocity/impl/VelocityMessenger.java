package moe.protasis.yukimessenger.velocity.impl;

import moe.protasis.yukicommons.api.plugin.IAbstractPlugin;
import moe.protasis.yukimessenger.velocity.YukiMessenger;
import moe.protasis.yukimessenger.api.message.IConnectedClient;
import moe.protasis.yukimessenger.velocity.event.DownstreamServerDisconnectEvent;
import moe.protasis.yukimessenger.server.Messenger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.util.logging.Logger;

public class VelocityMessenger extends Messenger {
    public VelocityMessenger(IAbstractPlugin plugin, String ip, int port) {
        super(plugin, YukiMessenger.getConfig().GetString("ip", "127.0.0.1"), YukiMessenger.getConfig().GetInt("port", 8633));
    }

    @Override
    protected Logger GetLogger() {
        return YukiMessenger.getInstance().GetLogger();
    }

    @Override
    protected IConnectedClient CreateClient(WebSocket conn, String ident, ClientHandshake clientHandshake) {
        try {
            return new VelocityDownstream(ident, conn);
        } catch (Exception e) {
            YukiMessenger.getInstance().GetLogger().severe("Failed to create client for " + ident);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void OnClosed(IConnectedClient client, int code, String message) {
        DownstreamServerDisconnectEvent e = new DownstreamServerDisconnectEvent((VelocityDownstream)client, code, message);
        YukiMessenger.getInstance().getProxyServer().getEventManager().fireAndForget(e);
    }

    @Override
    public void HandleClose(WebSocket conn, int code, String message) {
        super.HandleClose(conn, code, message);
    }
}
