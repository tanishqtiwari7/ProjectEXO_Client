package dev.uday.NET.Packets;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            protected Void doInBackground() {
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

                // Use BorderLayout to manage components
                imageFrame.setLayout(new BorderLayout());

                // Create a panel for the image that's exactly 800x600
                JPanel imagePanel = new JPanel();
                imagePanel.setPreferredSize(new Dimension(800, 600));
                imagePanel.setLayout(new BorderLayout());

                // Add image to the panel
                JLabel imageLabel = new JLabel(imageIcon);
                imagePanel.add(imageLabel, BorderLayout.CENTER);

                // Generate filename based on sender and timestamp
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String defaultFileName = senderUsername + "_" + sdf.format(new Date()) + ".png";

                // Create a panel for the filename and save button
                JPanel controlPanel = new JPanel(new BorderLayout(5, 0));
                controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                // Add filename display
                JTextField fileNameField = new JTextField(defaultFileName);
                fileNameField.setEditable(true);
                controlPanel.add(fileNameField, BorderLayout.CENTER);

                // Add save button
                JButton saveButton = new JButton("Save Image");
                saveButton.addActionListener(e -> {
                    // Convert ImageIcon to BufferedImage for saving
                    Image img = imageIcon.getImage();
                    BufferedImage bufferedImage = new BufferedImage(
                            img.getWidth(null),
                            img.getHeight(null),
                            BufferedImage.TYPE_INT_ARGB
                    );

                    Graphics2D g2 = bufferedImage.createGraphics();
                    g2.drawImage(img, 0, 0, null);
                    g2.dispose();

                    // Get filename from field (ensure it ends with .png)
                    String fileName = fileNameField.getText();
                    if (!fileName.toLowerCase().endsWith(".png")) {
                        fileName += ".png";
                        fileNameField.setText(fileName);
                    }

                    // Create directory path for saving images
                    String homeDir = System.getProperty("user.home");
                    File saveDir = new File(homeDir + File.separator + ".exo" +
                            File.separator + "Client" +
                            File.separator + "Images");

                    // Create directories if they don't exist
                    if (!saveDir.exists()) {
                        if (!saveDir.mkdirs()) {
                            JOptionPane.showMessageDialog(imageFrame,
                                    "Error creating directory: " + saveDir.getAbsolutePath(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    // Create the file in the target directory
                    File selectedFile = new File(saveDir, fileName);

                    try {
                        // Save the image
                        ImageIO.write(bufferedImage, "png", selectedFile);
                        JOptionPane.showMessageDialog(imageFrame,
                                "Image saved successfully to: " + selectedFile.getAbsolutePath(),
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(imageFrame,
                                "Error saving image: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                });
                controlPanel.add(saveButton, BorderLayout.EAST);

                // Add components to frame
                imageFrame.add(imagePanel, BorderLayout.CENTER);
                imageFrame.add(controlPanel, BorderLayout.SOUTH);

                // Set frame properties
                imageFrame.pack();
                imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                imageFrame.setLocationRelativeTo(null);
                imageFrame.setResizable(false);
                imageFrame.setVisible(true);
            }
        };
        worker.execute();
    }
}