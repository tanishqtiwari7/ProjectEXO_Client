package dev.uday.GUI;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;

public class MainFrame {
    public static JFrame mainFrame;

    public static void setMainFrame() {
        FlatMacDarkLaf.setup();
        mainFrame = new JFrame("ProjectEXO");
        mainFrame.setSize(800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);


        LoginPanel.setLoginPanel();
        mainFrame.add(LoginPanel.loginPanel);
        mainFrame.setVisible(true);
        mainFrame.revalidate();
    }
}
