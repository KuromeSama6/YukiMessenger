package moe.protasis.yukimessenger.api.impl;

import lombok.AllArgsConstructor;
import moe.protasis.yukimessenger.api.IInboundMessageHandler;
import moe.protasis.yukimessenger.api.message.InboundMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@AllArgsConstructor
public class EndpointMethodHandler implements IInboundMessageHandler {
    private final Object obj;
    private final Method method;

    @Override
    public void Handle(InboundMessage msg) {
        try {
            method.setAccessible(true);
            method.invoke(obj, msg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
