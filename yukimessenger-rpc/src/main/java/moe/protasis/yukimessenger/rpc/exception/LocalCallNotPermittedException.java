package moe.protasis.yukimessenger.rpc.exception;

import moe.protasis.yukimessenger.rpc.method.RPCHandlerMethod;

public class LocalCallNotPermittedException extends RuntimeException {
    public LocalCallNotPermittedException(RPCHandlerMethod handlerMethod) {
        super("Method %s is not permitted to be called locally. To allow local calls, set `allowLocalCall` to true in the method's RPC annotation.".formatted(handlerMethod.GetMethodName()));
    }
}
