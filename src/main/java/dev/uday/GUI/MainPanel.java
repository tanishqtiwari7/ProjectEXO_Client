package dev.uday.GUI;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightIJTheme;
import dev.uday.NET.SocketClient;

import javax.swing.*;
import java.awt.*;

import static dev.uday.GUI.FeaturePanel.setFeaturePanel;

public class MainPanel {
    public static JFrame mainFrame = MainFrame.mainFrame;
    public static JPanel mainPanel = new JPanel();
    public static JPanel contentPanel = new JPanel();
    private static JPanel bottomPanel;
    private static JLabel onlineCountLabel;
    private static boolean isDarkTheme = true; // Track current theme state

    public static void setMainPanel() {
        mainFrame.getContentPane().removeAll();

        // Set layout to BorderLayout
        mainPanel.setLayout(new BorderLayout());

        // Add top content panel to the mainPanel
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setFeaturePanel();

        // Add bottom panel to the menuPanel
        bottomPanel = getBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainFrame.add(mainPanel);
        mainFrame.revalidate();
    }

    private static JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Create upper border
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));

        // Create online count label on the left
        int onlineUsersCount = SocketClient.onlineUsersCount;
        onlineCountLabel = new JLabel("Online: " + onlineUsersCount);
        bottomPanel.add(onlineCountLabel);

        // Add flexible space
        bottomPanel.add(Box.createHorizontalGlue());

        // Create theme toggle button
        JButton themeButton = getThemeButton();

        // Use horizontal box to push buttons to the right
        JPanel rightAlignPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightAlignPanel.add(themeButton);

        // Create and add logout button on the right
        JButton logoutButton = getLogoutButton();
        rightAlignPanel.add(logoutButton);

        // Add right-aligned panel with appropriate spacing
        bottomPanel.add(Box.createHorizontalStrut(500));  // Adjusted spacing
        bottomPanel.add(rightAlignPanel);

        return bottomPanel;
    }

    private static JButton getThemeButton() {
        JButton themeButton = new JButton(isDarkTheme ? "Light Mode" : "Dark Mode");
        themeButton.addActionListener(e -> {
            isDarkTheme = !isDarkTheme;
            if (isDarkTheme) {
                try {
                    UIManager.setLookAndFeel(new FlatAtomOneDarkIJTheme());
                    themeButton.setText("Light Mode");
                } catch (UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    UIManager.setLookAndFeel(new FlatAtomOneLightIJTheme());
                    themeButton.setText("Dark Mode");
                } catch (UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }
            }
            SwingUtilities.updateComponentTreeUI(mainFrame);
            mainFrame.revalidate();
            mainFrame.repaint();
        });
        return themeButton;
    }

    private static JButton getLogoutButton() {
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            // Logout logic here
            int dialogResult = JOptionPane.showConfirmDialog(MainFrame.mainFrame, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    System.exit(0);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());;
                }
            }
        });
        return logoutButton;
    }

    public static void updateOnlineCount() {
        int onlineUsersCount = SocketClient.onlineUsersCount;
        onlineCountLabel.setText("Online: " + onlineUsersCount);

        // Refresh the frame
        mainFrame.revalidate();
    }
}