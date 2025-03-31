package dev.uday.NET;

import dev.uday.GUI.AIPanel;
import dev.uday.GUI.ChatPanel;
import dev.uday.GUI.MainPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

import static dev.uday.NET.TextMessageHandler.*;

public class PacketHandler {
    public static void handlePacket(byte[] packet) {
        byte packetType = packet[0];
        byte[] packetData = Arrays.copyOfRange(packet, 1, packet.length);
        System.out.println("Received packet of type " + packetType);
        switch (packetType) {
            // Handle broadcasted userList
            case 0:
                handleBroadcastedUserList(packetData);
                break;
            // Handle  message
            case 1:
                handleTextMessage(packetData);
                break;
            // Handle image
            case 3:
                ImageMessageHandler.handleImageMessage(packetData);
            // Handle AI prompts
            case 9:
                handleAIResponse(packetData);
                break;
        }
    }

    private static void handleAIResponse(byte[] packetData) {
        byte responseType = packetData[0];
        byte[] responseData = Arrays.copyOfRange(packetData, 1, packetData.length);
        String response = new String(responseData);

        SwingUtilities.invokeLater(() -> {
            AIPanel.receiveAIResponse(response);
        });
    }

    public static void handleBroadcastedUserList(byte[] data) {
        // Handle broadcasted userList
        System.out.println("Received userList");
        String userList = new String(data);
        System.out.println(userList);
        String[] users = userList.split(",");
        SocketClient.onlineUsers = new ArrayList<>(Arrays.asList(users));
        SocketClient.onlineUsersCount = SocketClient.onlineUsers.size();
        MainPanel.updateOnlineCount();
        SwingUtilities.invokeLater(ChatPanel::updateUserList);
        System.out.println(SocketClient.onlineUsers);
    }
}
