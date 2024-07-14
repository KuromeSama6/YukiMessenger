package moe.protasis.yukimessenger.util;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@UtilityClass
public class Util {
    public static int GetPidByPort(int port) throws IOException {
        Process process = new ProcessBuilder(
                "lsof", "-i", String.format(":%d", port)
        ).start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("(LISTEN)")) {
                    String[] args = line.split("\\s+");
                    return Integer.parseInt(args[1]);
                }
            }
        }

        return -1;
    }

    public static boolean KillProcess(int pid) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(
                "kill", String.format("%d", pid)
        ).start();
        return process.waitFor() == 0;
    }
}
