package moe.protasis.yukimessenger.util;

import moe.protasis.yukimessenger.YukiMessengerAPI;
import moe.protasis.yukimessenger.bungee.MessengerServer;
import moe.protasis.yukimessenger.spigot.MessengerClient;

import java.io.IOException;
import java.net.ServerSocket;

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

    public static boolean CheckPortUsable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Port is free, able to bind to it
            return true;
        } catch (IOException e) {
            // Port is already in use
            return false;
        }
    }
}
