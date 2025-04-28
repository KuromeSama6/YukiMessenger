package moe.protasis.yukimessenger.util;

import java.io.IOException;
import java.net.ServerSocket;

public class EnvUtil {
    public static boolean CheckPortUsable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Port is free, able to bind to it
            serverSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            // Port is already in use
            return false;
        }
    }
}
