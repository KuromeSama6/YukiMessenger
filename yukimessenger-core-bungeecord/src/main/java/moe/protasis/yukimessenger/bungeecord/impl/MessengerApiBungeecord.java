package moe.protasis.yukimessenger.bungeecord.impl;

import moe.protasis.yukimessenger.bungeecord.YukiMessenger;
import moe.protasis.yukimessenger.api.annotation.EndpointHandler;
import moe.protasis.yukimessenger.api.annotation.MessageHandler;
import moe.protasis.yukimessenger.api.IYukiMessengerAPI;
import moe.protasis.yukimessenger.api.impl.EndpointMethodHandler;
import moe.protasis.yukimessenger.api.message.IConnectedClient;
import moe.protasis.yukimessenger.api.message.IMessageNode;
import moe.protasis.yukimessenger.api.message.MessageResponse;
import moe.protasis.yukimessenger.api.message.OutboundMessage;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class MessengerApiBungeecord implements IYukiMessengerAPI {
    @Override
    public boolean IsProxy() {
        return true;
    }

    @Override
    public boolean IsReady() {
        return true;
    }

    @Override
    public String GetIdent() {
        return null;
    }

    @Override
    public Logger GetLogger() {
        return YukiMessenger.getInstance().GetLogger();
    }

    @Override
    public IMessageNode GetProxyNode() {
        throw new UnsupportedOperationException("Call to GetProxyNode() on a proxy instance");
    }

    @Override
    public <T extends IConnectedClient> List<T> GetAllServers() {
        return YukiMessenger.getInstance().getServer().getClients()
                .stream()
                .filter(c -> c != null)
                .map(c -> (T) c)
                .toList();
    }

    @Override
    public void Subscribe(EndpointHandler handler) {
        try {
            for (Method method : handler.getClass().getDeclaredMethods()) {
                MessageHandler annotation = method.getAnnotation(MessageHandler.class);
                if (annotation == null || !annotation.destination().Matches()) continue;
                YukiMessenger.getInstance().getServer().Subscribe(annotation.value(), new EndpointMethodHandler(handler, method));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void SendAsync(OutboundMessage msg, Consumer<MessageResponse> callback) {
        if (YukiMessenger.getInstance().getServer().getProcessor().AddTask(msg, callback)) {
            msg.getReceiver().Send(msg.getData());
        }
    }
}
