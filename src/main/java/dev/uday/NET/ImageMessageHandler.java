package dev.uday.NET;

import javax.swing.*;

public class ImageMessageHandler {
    public static void handleImageMessage(byte[] packetData) {
     byte msgType = packetData[0];
     byte[] imageData = new byte[packetData.length - 1];
     System.arraycopy(packetData, 1, imageData, 0, imageData.length);
        switch (msgType) {
            case 0:
                // Handle general image message
                handleGeneralImageMessage(imageData);
                break;
            case 1:
                // Handle private image message
                handlePrivateImageMessage(imageData);
                break;
        }
    }

    private static void handlePrivateImageMessage(byte[] imageData) {
        byte[] senderBytes = new byte[30];
        System.arraycopy(imageData, 0, senderBytes, 0, 30);
        String senderUsername = new String(senderBytes).trim();
        byte[] imageBytes = new byte[imageData.length - 30];
        System.arraycopy(imageData, 30, imageBytes, 0, imageBytes.length);

        // Show the image in the GUI (separate frame)
        JFrame imageFrame = new JFrame("Private Image from " + senderUsername);
        ImageIcon imageIcon = new ImageIcon(imageBytes);
        JLabel imageLabel = new JLabel(imageIcon);
        imageFrame.add(imageLabel);
        imageFrame.setSize(800, 600);
        imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        imageFrame.setLocationRelativeTo(null);
        imageFrame.setVisible(true);
    }

    private static void handleGeneralImageMessage(byte[] imageData) {
        byte[] senderBytes = new byte[30];
        System.arraycopy(imageData, 0, senderBytes, 0, 30);
        String senderUsername = new String(senderBytes).trim();
        byte[] imageBytes = new byte[imageData.length - 30];
        System.arraycopy(imageData, 30, imageBytes, 0, imageBytes.length);

        // Show the image in the GUI (separate frame)
        JFrame imageFrame = new JFrame("Image from " + senderUsername);
        ImageIcon imageIcon = new ImageIcon(imageBytes);
        JLabel imageLabel = new JLabel(imageIcon);
        imageFrame.add(imageLabel);
        imageFrame.setSize(800, 600);
        imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        imageFrame.setLocationRelativeTo(null);
        imageFrame.setVisible(true);
    }
}
