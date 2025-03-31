package dev.uday.NET.Packets;

import dev.uday.GUI.ChatPanel;

import javax.swing.*;
import java.util.Arrays;

public class TextMessageHandler {
    public static void handleTextMessage(byte[] packet) {
        byte messageType = packet[0];
        byte[] messageData = Arrays.copyOfRange(packet, 1, packet.length);
        switch (messageType) {
            // Handle general message
            case 0:
                TextMessageHandler.handleGeneralMessage(messageData);
                break;

            //Handle  private message
            case 1:
                TextMessageHandler.handlePrivateMessage(messageData);
                break;
        }
    }

    private static void handleGeneralMessage(byte[] messageData) {
        // Handle general message
        String[] messageDataString = new String(messageData).split("x1W1x");
        String senderUsername = messageDataString[0];
        String message = messageDataString[1];
        SwingUtilities.invokeLater(() -> ChatPanel.receiveMessage(senderUsername, message, false));
        System.out.println("Received general message from " + senderUsername + ": " + message);
    }

    private static void handlePrivateMessage(byte[] messageData) {
        // Handle private message
        String[] messageDataString = new String(messageData).split("x1W1x");
        String senderUsername = messageDataString[0];
        String message = messageDataString[1];
        SwingUtilities.invokeLater(() -> ChatPanel.receiveMessage(senderUsername, message, true));
        System.out.println("Received private message from " + senderUsername + ": " + message);
    }
}
