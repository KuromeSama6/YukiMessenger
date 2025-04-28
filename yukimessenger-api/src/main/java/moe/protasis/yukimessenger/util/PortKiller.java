package moe.protasis.yukimessenger.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PortKiller {

    public static void main(String[] args) {
        int port = 8080; // Replace with the port you want to free
        try {
            KillProcessByPort(port);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void KillProcessByPort(int port) throws IOException, InterruptedException {
        String os = System.getProperty("os.name").toLowerCase();

        String command;
        if (os.contains("win")) {
            command = "netstat -ano | findstr :" + port;
        } else {
            command = "lsof -i :" + port;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            // Extract the process ID (PID) from the command output
            String[] parts = line.trim().split("\\s+");
            if (os.contains("win")) {
                // On Windows, the PID is the last column in the output
                int pid = Integer.parseInt(parts[parts.length - 1]);
                killProcessOnWindows(pid);
            } else {
                // On Unix-based systems, the PID is the second column in the output
                int pid = Integer.parseInt(parts[1]);
                killProcessOnUnix(pid);
            }
        }

        process.waitFor();
    }

    private static void killProcessOnUnix(int pid) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("kill", "-9", String.valueOf(pid));
        Process process = processBuilder.start();
        process.waitFor();
    }

    private static void killProcessOnWindows(int pid) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("taskkill", "/F", "/PID", String.valueOf(pid));
        Process process = processBuilder.start();
        process.waitFor();
    }
}