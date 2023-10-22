package moe.protasis.yukimessenger.util;

import moe.protasis.yukimessenger.YukiMessengerAPI;
import moe.protasis.yukimessenger.bungee.MessengerServer;
import moe.protasis.yukimessenger.spigot.MessengerClient;

public class EnvUtil {
    public static void EnsureEnv(boolean isProxy) {

        if (isProxy && MessengerServer.getInstance() == null) throw new InvalidEnvironmentException("This method can only be called on the proxy.");
        if (!isProxy && MessengerClient.getInstance() == null) throw new InvalidEnvironmentException("This method can only be called on a spigot instance.");
    }

    private static class InvalidEnvironmentException extends RuntimeException {
        public InvalidEnvironmentException(String message) {
            super(message);
        }
    }
}
