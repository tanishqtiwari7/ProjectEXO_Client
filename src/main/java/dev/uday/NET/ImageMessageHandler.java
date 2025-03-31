package dev.uday.NET;

import org.jetbrains.annotations.NotNull;

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
        Result result = getResult(imageData);
        displayImage(result.imageIcon(), result.senderUsername(), true);
    }

    private static void handleGeneralImageMessage(byte[] imageData) {
        Result result = getResult(imageData);
        displayImage(result.imageIcon(), result.senderUsername(), false);
    }

    private static @NotNull Result getResult(byte[] imageData) {
        byte[] senderBytes = new byte[30];
        System.arraycopy(imageData, 0, senderBytes, 0, 30);
        String senderUsername = new String(senderBytes).trim();
        byte[] imageBytes = new byte[imageData.length - 30];
        System.arraycopy(imageData, 30, imageBytes, 0, imageBytes.length);

        // Show the image in the GUI (separate frame)
        ImageIcon imageIcon = new ImageIcon(imageBytes);
        return new Result(senderUsername, imageIcon);
    }

    private record Result(String senderUsername, ImageIcon imageIcon) {
    }

    private static void displayImage(ImageIcon imageIcon, String senderUsername, boolean isPrivate) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // This method runs in a background thread
                return null;
            }

            @Override
            protected void done() {
                // This method runs on the Event Dispatch Thread (EDT)
                JFrame imageFrame = new JFrame();
                if (isPrivate) {
                    imageFrame.setTitle("Private Image from " + senderUsername);
                } else {
                    imageFrame.setTitle("Image from " + senderUsername);
                }
                JLabel imageLabel = new JLabel(imageIcon);
                imageFrame.add(imageLabel);
                imageFrame.setSize(800, 600);
                imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                imageFrame.setLocationRelativeTo(null);
                imageFrame.setVisible(true);
            }
        };
        worker.execute();
    }
}
