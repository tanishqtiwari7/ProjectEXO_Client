package dev.uday.GUI;

import dev.uday.NET.ServerBroadcastReceiver;
import dev.uday.NET.SocketClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

public class LoginPanel {
    public static JPanel loginPanel;
    static JTextField usernameField;
    static JPasswordField passwordField;
    static JTextField serverIpField;
    static JTextField portField;
    static JButton loginButton;
    static JList<String> serverList;
    static DefaultListModel<String> serverListModel;
    static Map<String, ServerBroadcastReceiver.ServerInfo> currentServers;

    public static void setLoginPanel() {
        loginPanel = new JPanel(new BorderLayout(10, 0));
        loginPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left panel for server list
        JPanel leftPanel = createServerListPanel();

        // Right panel for login fields
        JPanel rightPanel = createLoginFieldsPanel();

        // Add panels to main login panel
        loginPanel.add(leftPanel, BorderLayout.WEST);
        loginPanel.add(rightPanel, BorderLayout.CENTER);
    }

    private static JPanel createServerListPanel() {
        JPanel serverListPanel = new JPanel(new BorderLayout());
        serverListPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Available Servers(Visible in LAN)",
                TitledBorder.LEFT, TitledBorder.TOP));

        serverListModel = new DefaultListModel<>();
        serverList = new JList<>(serverListModel);
        serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serverList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && serverList.getSelectedIndex() != -1) {
                String serverName = serverList.getSelectedValue();
                // Find the server info by server name
                for (ServerBroadcastReceiver.ServerInfo info : currentServers.values()) {
                    if (info.getServerName().equals(serverName)) {
                        serverIpField.setText(info.getIpAddress());
                        portField.setText(info.getPort());
                        break;
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(serverList);
        scrollPane.setPreferredSize(new Dimension(250, 200));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            // Refresh the list with current servers
            updateServerList(ServerBroadcastReceiver.availableServers);
        });

        serverListPanel.add(scrollPane, BorderLayout.CENTER);
        serverListPanel.add(refreshButton, BorderLayout.SOUTH);

        return serverListPanel;
    }

    public static void updateServerList(Map<String, ServerBroadcastReceiver.ServerInfo> availableServers) {
        // Store the reference to the current servers
        currentServers = availableServers;

        // Update must happen on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            serverListModel.clear();
            for (ServerBroadcastReceiver.ServerInfo info : availableServers.values()) {
                // Use server name as the display value
                serverListModel.addElement(info.getServerName());
            }
        });
    }

    private static JPanel createLoginFieldsPanel() {
        JPanel loginFieldsPanel = new JPanel();
        loginFieldsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Login",
                TitledBorder.LEFT, TitledBorder.TOP));

        // Set Layout with padding
        GridBagLayout layout = new GridBagLayout();
        loginFieldsPanel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add Server IP Components and localhost as default
        JLabel serverIpLabel = new JLabel("Server IP");
        serverIpField = new JTextField("localhost", 20);
        serverIpField.setEditable(true);
        serverIpField.addActionListener(e -> usernameField.requestFocus());

        // Add Port Components
        JLabel portLabel = new JLabel("Port");
        portField = new JTextField("2005", 20);
        portField.setEditable(true);
        portField.addActionListener(e -> usernameField.requestFocus());

        // Add Login Components
        JLabel usernameLabel = new JLabel("Username");
        usernameField = new JTextField(20);
        usernameField.addActionListener(e -> passwordField.requestFocus());

        JLabel passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField(20);
        passwordField.addActionListener((ActionEvent e) -> login());

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());

        // Add Components to Panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginFieldsPanel.add(serverIpLabel, gbc);
        gbc.gridx = 1;
        loginFieldsPanel.add(serverIpField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginFieldsPanel.add(portLabel, gbc);
        gbc.gridx = 1;
        loginFieldsPanel.add(portField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginFieldsPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        loginFieldsPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        loginFieldsPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        loginFieldsPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginFieldsPanel.add(loginButton, gbc);

        return loginFieldsPanel;
    }

    private static void login() {
        // Login Logic
        Thread loginThread = new Thread(() -> {
            SocketClient.init(serverIpField.getText(), portField.getText(), usernameField.getText(), passwordField.getText());
        });
        loginThread.start();
    }
}