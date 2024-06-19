package moe.protasis.yukimessenger.api.impl;

import lombok.AllArgsConstructor;
import moe.protasis.yukimessenger.api.IInboundMessageHandler;
import moe.protasis.yukimessenger.message.InboundMessage;
import moe.protasis.yukimessenger.message.ServerInboundMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@AllArgsConstructor
public class EndpointMethodHandler implements IInboundMessageHandler {
    private final Object object;
    private final Method method;

    @Override
    @Deprecated
    public void Handle(ServerInboundMessage msg) {
        try {
            method.setAccessible(true);
            method.invoke(object, msg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void Handle(InboundMessage msg) {
        try {
            method.setAccessible(true);
            method.invoke(object, msg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
