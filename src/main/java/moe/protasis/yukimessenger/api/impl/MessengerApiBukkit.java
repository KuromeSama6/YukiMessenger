package moe.protasis.yukimessenger.api.impl;

import moe.protasis.yukimessenger.annotation.EndpointHandler;
import moe.protasis.yukimessenger.annotation.MessageHandler;
import moe.protasis.yukimessenger.api.IYukiMessengerApi;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import moe.protasis.yukimessenger.message.MessageProcessor;
import moe.protasis.yukimessenger.message.MessageResponse;
import moe.protasis.yukimessenger.message.OutboundMessage;
import moe.protasis.yukimessenger.spigot.MessengerClient;
import moe.protasis.yukimessenger.spigot.YukiMessenger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class MessengerApiBukkit implements IYukiMessengerApi {
    @Override
    public boolean IsProxy() {
        return false;
    }

    @Override
    public boolean IsReady() {
        return MessengerClient.getInstance().getWsClient().isReady();
    }

    @Override
    public String GetIdent() {
        return MessengerClient.getInstance().GetIdent();
    }

    @Override
    public Logger GetLogger() {
        return YukiMessenger.GetLogger();
    }

    @Override
    public List<SpigotServer> GetAllServers() {
        throw new IllegalStateException("Call to proxy-only method on non proxy server");
    }

    @Override
    public void Subscribe(EndpointHandler handler) {
        try {
            for (Method method : handler.getClass().getDeclaredMethods()) {
                MessageHandler annotation = method.getAnnotation(MessageHandler.class);
                if (annotation == null || !annotation.destination().Matches()) continue;
                MessengerClient.getInstance().Subscribe(annotation.value(), new EndpointMethodHandler(handler, method));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void SendAsync(OutboundMessage msg, Consumer<MessageResponse> callback) {
        MessageProcessor processor = MessengerClient.getInstance().getProcessor();
        if (processor.AddTask(msg, callback)) {
            msg.getReceiver().Send(msg.getData());
        }
    }
}
