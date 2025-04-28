package moe.protasis.yukimessenger.bukkit.impl;

import moe.protasis.yukimessenger.api.annotation.EndpointHandler;
import moe.protasis.yukimessenger.api.annotation.MessageHandler;
import moe.protasis.yukimessenger.api.IYukiMessengerAPI;
import moe.protasis.yukimessenger.api.impl.EndpointMethodHandler;
import moe.protasis.yukimessenger.api.message.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class MessengerApiBukkit implements IYukiMessengerAPI {
    @Override
    public boolean IsProxy() {
        return false;
    }

    @Override
    public boolean IsReady() {
        return YukiMessenger.getInstance().getClient().getWsClient().isReady();
    }

    @Override
    public String GetIdent() {
        return YukiMessenger.getInstance().getClient().GetIdent();
    }

    @Override
    public Logger GetLogger() {
        return YukiMessenger.getInstance().GetLogger();
    }

    @Override
    public IMessageNode GetProxyNode() {
        return YukiMessenger.getInstance().getClient();
    }

    @Override
    public <T extends IConnectedClient> List<T> GetAllServers() {
        throw new UnsupportedOperationException("Call to proxy-only method on non proxy server");
    }

    @Override
    public void Subscribe(EndpointHandler handler) {
        try {
            for (Method method : handler.getClass().getDeclaredMethods()) {
                MessageHandler annotation = method.getAnnotation(MessageHandler.class);
                if (annotation == null || !annotation.destination().Matches()) continue;
                YukiMessenger.getInstance().getClient().Subscribe(annotation.value(), new EndpointMethodHandler(handler, method));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void SendAsync(OutboundMessage msg, Consumer<MessageResponse> callback) {
        MessageProcessor processor = YukiMessenger.getInstance().getClient().getProcessor();
        if (processor.AddTask(msg, callback)) {
            msg.getReceiver().Send(msg.getData());
        }
    }
}
