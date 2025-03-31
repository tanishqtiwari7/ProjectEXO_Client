package dev.uday.NET;

import dev.uday.GUI.LoginPanel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class ServerBroadcastReceiver implements Runnable {
    private static final int BROADCAST_PORT = 7415;
    private boolean isRunning = true;
    public static Map<String, ServerInfo> availableServers = new HashMap<>();

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(BROADCAST_PORT);
            byte[] buffer = new byte[90]; // Same size as the broadcaster's data

            System.out.println("Server discovery started - listening for broadcasts on port " + BROADCAST_PORT);

            while (isRunning) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                // Extract server information
                String ipAddress = new String(packet.getData(), 0, 30).trim();
                String port = new String(packet.getData(), 30, 30).trim();
                String serverName = new String(packet.getData(), 60, 30).trim();

                // Store server information
                ServerInfo serverInfo = new ServerInfo(ipAddress, port, serverName);
                availableServers.put(ipAddress + ":" + port, serverInfo);

                // Update UI if needed
                try {
                    // Call UI update method if available
                    // This would update a server list in the UI
                    LoginPanel.updateServerList(availableServers);
                } catch (Exception e) {
                    System.out.println("Failed to update UI: " + e.getMessage());
                }
            }
            socket.close();
        } catch (SocketException e) {
            System.err.println("Socket error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }

    public void stop() {
        isRunning = false;
    }

    // Inner class to store server information
    public static class ServerInfo {
        private final String ipAddress;
        private final String port;
        private final String serverName;

        public ServerInfo(String ipAddress, String port, String serverName) {
            this.ipAddress = ipAddress;
            this.port = port;
            this.serverName = serverName;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public String getPort() {
            return port;
        }

        public String getServerName() {
            return serverName;
        }

        @Override
        public String toString() {
            return serverName + " (" + ipAddress + ":" + port + ")";
        }
    }
}