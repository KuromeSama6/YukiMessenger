package moe.protasis.yukimessenger.api.impl;

import moe.protasis.yukimessenger.annotation.EndpointHandler;
import moe.protasis.yukimessenger.annotation.MessageHandler;
import moe.protasis.yukimessenger.api.IYukiMessengerApi;
import moe.protasis.yukimessenger.bungee.MessengerServer;
import moe.protasis.yukimessenger.bungee.YukiMessenger;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import moe.protasis.yukimessenger.message.MessageResponse;
import moe.protasis.yukimessenger.message.OutboundMessage;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class MessengerApiBungeecord implements IYukiMessengerApi {
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
        return YukiMessenger.GetLogger();
    }

    @Override
    public List<SpigotServer> GetAllServers() {
        return MessengerServer.getInstance().getClients();
    }

    @Override
    public void Subscribe(EndpointHandler handler) {
        try {
            for (Method method : handler.getClass().getDeclaredMethods()) {
                MessageHandler annotation = method.getAnnotation(MessageHandler.class);
                if (annotation == null || !annotation.destination().Matches()) continue;
                MessengerServer.getInstance().Subscribe(annotation.value(), new EndpointMethodHandler(handler, method));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void SendAsync(OutboundMessage msg, Consumer<MessageResponse> callback) {
        if (MessengerServer.getInstance().getProcessor().AddTask(msg, callback)) {
            msg.getReceiver().Send(msg.getData());
        }
    }
}
