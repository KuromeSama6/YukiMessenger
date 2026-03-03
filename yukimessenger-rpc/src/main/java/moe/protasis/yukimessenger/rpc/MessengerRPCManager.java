package moe.protasis.yukimessenger.rpc;

import jdk.jshell.spi.ExecutionControl;
import moe.protasis.yukimessenger.api.IYukiMessengerAPI;
import moe.protasis.yukimessenger.rpc.annotation.ClientRPC;
import moe.protasis.yukimessenger.rpc.annotation.ServerRPC;
import moe.protasis.yukimessenger.rpc.exception.RPCHandlerRegistrationException;
import moe.protasis.yukimessenger.rpc.method.RPCHandlerMethod;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.utility.nullability.MaybeNull;

import java.lang.reflect.Method;

/**
 * Manager for registering and handling RPC calls.
 */
public class MessengerRPCManager {
    private final IYukiMessengerAPI api;
    private final ByteBuddy byteBuddy;

    public MessengerRPCManager(IYukiMessengerAPI api) {
        this.api = api;
        byteBuddy = new ByteBuddy();
    }

    public void RegisterHandler(RPCHandler handler) throws RPCHandlerRegistrationException {
        var clazz = handler.getClass();

        for (var method : clazz.getDeclaredMethods()) {
            if (!IsValidRPCMethod(method)) continue;
            method.setAccessible(true);

            var handlerMethod = new RPCHandlerMethod(api, handler, method);
        }
    }

    public void UnregisterHandler(RPCHandler handler) {

    }

    /**
     * Returns the RPC instance of the specified class. YukiMessenger allows only one instance of each RPC handler class, so this method will return the existing instance if it has already been created. This method is useful for when you need to call RPC methods from another class.
     *
     * @param clazz The class of the RPC handler to get the instance of.
     * @param <T> The type of the RPC handler.
     * @return The RPC instance of the specified class, or null if the instance does not exist.
     */
    @MaybeNull
    public <T> T GetRPCInstance(Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    private boolean IsValidRPCMethod(Method method) {
        if (method.getDeclaredAnnotation(ServerRPC.class) != null) return true;
        if (method.getDeclaredAnnotation(ClientRPC.class) != null) return true;

        return false;
    }
}
