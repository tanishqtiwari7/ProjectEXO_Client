package dev.uday.GUI;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class MainFrame {
    public static JFrame mainFrame;

    public static void setMainFrame() {
        FlatMacDarkLaf.setup();
        mainFrame = new JFrame("ProjectEXO");
        mainFrame.setSize(800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(MainFrame.class.getClassLoader().getResource("icons/logo.png")));
        // Scale the image to appropriate icon size (64x64 is common for window icons)
        Image scaledImage = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        mainFrame.setIconImage(scaledImage);

        LoginPanel.setLoginPanel();
        mainFrame.add(LoginPanel.loginPanel);
        mainFrame.setVisible(true);
        mainFrame.revalidate();
    }
}
