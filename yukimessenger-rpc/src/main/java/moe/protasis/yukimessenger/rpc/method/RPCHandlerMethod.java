package moe.protasis.yukimessenger.rpc.method;

import lombok.Getter;
import moe.protasis.yukimessenger.api.IYukiMessengerAPI;
import moe.protasis.yukimessenger.api.message.MessageDestination;
import moe.protasis.yukimessenger.rpc.RPCHandler;
import moe.protasis.yukimessenger.rpc.RPCUtil;
import moe.protasis.yukimessenger.rpc.annotation.ClientRPC;
import moe.protasis.yukimessenger.rpc.annotation.ServerRPC;
import moe.protasis.yukimessenger.rpc.exception.RPCHandlerRegistrationException;

import java.lang.reflect.Method;

public class RPCHandlerMethod {
    @Getter
    private final String name;

    private final RPCHandler handler;
    private final Method method;
    private final MessageDestination bound;

    public RPCHandlerMethod(IYukiMessengerAPI api, RPCHandler handler, Method method) throws RPCHandlerRegistrationException {
        this.handler = handler;
        this.method = method;

        var serverAnnotation = method.getDeclaredAnnotation(ServerRPC.class);
        var clientAnnotation = method.getDeclaredAnnotation(ClientRPC.class);

        if (serverAnnotation != null && clientAnnotation != null)
            throw new RPCHandlerRegistrationException("Method " + method.getName() + " cannot be annotated with both @ServerRPC and @ClientRPC");

        if (serverAnnotation == null && clientAnnotation == null)
            throw new RPCHandlerRegistrationException("Method " + method.getName() + " must be annotated with either @ServerRPC or @ClientRPC");

        bound = serverAnnotation != null ? MessageDestination.SERVER : MessageDestination.CLIENT;
        name = "$RPC$%s$%s$%s$%s".formatted(bound, handler.getClass().getName(), method.getName(), RPCUtil.GetMethodParameterSignature(method.getParameters()));
    }

    public String GetMethodName() {
        return handler.getClass().getName() + "#" + method.getName();
    }
}
