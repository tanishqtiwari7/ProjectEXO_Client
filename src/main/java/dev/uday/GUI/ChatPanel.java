package dev.uday.GUI;

import dev.uday.Main;
import dev.uday.NET.SocketClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatPanel {
    private static JPanel chatPanel;
    private static JList<String> userList;
    private static DefaultListModel<String> userListModel;
    private static JTextArea chatArea;
    private static JTextField messageField;
    private static JButton sendButton;
    private static JButton backButton;
    private static JLabel chatTitleLabel;
    private static String selectedUser = "General";
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    // Map to store chat history for each user and general chat
    private static Map<String, StringBuilder> chatHistories = new HashMap<>();

    public static void setChatPanel() {
        chatPanel = new JPanel(new BorderLayout(5, 5));
        chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Initialize general chat history if not already created
        if (!chatHistories.containsKey("General")) {
            chatHistories.put("General", new StringBuilder());
        }

        // Create user list panel (left side)
        JPanel userListPanel = createUserListPanel();

        // Create chat view panel (right side)
        JPanel chatViewPanel = createChatViewPanel();

        // Create split pane with user list on left and chat on right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, userListPanel, chatViewPanel);
        splitPane.setDividerLocation(200);

        // Add split pane to main chat panel
        chatPanel.add(splitPane, BorderLayout.CENTER);

        // Update the main panel with the chat panel
        MainPanel.contentPanel.removeAll();
        MainPanel.contentPanel.add(chatPanel);
        MainPanel.mainFrame.revalidate();
        MainPanel.mainFrame.repaint();

        // Select General chat by default
        userList.setSelectedValue("General", true);
        updateChatTitle("General");
        updateChatArea("General");

        // Update user list with online users
        updateUserList();
    }

    private static JPanel createUserListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Online Users"));

        // Create list model and JList for users
        userListModel = new DefaultListModel<>();
        userListModel.addElement("General"); // Add general chat option

        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if ("General".equals(value)) {
                    setIcon(UIManager.getIcon("FileView.computerIcon"));
                } else {
                    setIcon(UIManager.getIcon("FileView.personIcon"));
                }
                return c;
            }
        });

        // Add selection listener to handle chat switching
        userList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selected = userList.getSelectedValue();
                    if (selected != null) {
                        selectedUser = selected;
                        updateChatTitle(selected);
                        updateChatArea(selected);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userList);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Back button to return to feature panel
        backButton = new JButton("Back to Menu");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FeaturePanel.setFeaturePanel();
            }
        });
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    private static JPanel createChatViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Chat title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        chatTitleLabel = new JLabel("General Chat");
        chatTitleLabel.setFont(new Font(chatTitleLabel.getFont().getName(), Font.BOLD, 14));
        chatTitleLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        titlePanel.add(chatTitleLabel, BorderLayout.CENTER);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        panel.add(chatScrollPane, BorderLayout.CENTER);

        // Message input panel
        JPanel messagePanel = new JPanel(new BorderLayout(5, 0));
        messageField = new JTextField();
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        panel.add(messagePanel, BorderLayout.SOUTH);

        return panel;
    }

    private static void updateChatTitle(String user) {
        if ("General".equals(user)) {
            chatTitleLabel.setText("General Chat");
        } else {
            chatTitleLabel.setText("Chat with " + user);
        }
    }

    private static void updateChatArea(String user) {
        // Initialize chat history for this user if it doesn't exist
        if (!chatHistories.containsKey(user)) {
            chatHistories.put(user, new StringBuilder());
        }

        // Update chat area with the appropriate history
        chatArea.setText(chatHistories.get(user).toString());

        // Scroll to bottom
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private static void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            // Add message to chat area
            String timestamp = timeFormat.format(new Date());
            String formattedMessage;

            if ("General".equals(selectedUser)) {
                // Send general message
                System.out.println("Sending general message");
                byte[] packetData = new byte[message.getBytes().length + 2];
                packetData[0] = 1; // PacketType type
                packetData[1] = 0; // General message type
                System.arraycopy(message.getBytes(), 0, packetData, 2, message.getBytes().length);
                SocketClient.sendPacket(packetData);

                // Display message locally
                formattedMessage = "[" + timestamp + "] You: " + message + "\n";
                chatHistories.get("General").append(formattedMessage);
            } else {
                // Send private message
                System.out.println("Sending private message to " + selectedUser);
                String messageData = selectedUser + "x1W1x" + message;
                byte[] packetData = new byte[messageData.getBytes().length + 2];
                packetData[0] = 1; // PacketType type
                packetData[1] = 1; // Private message type
                System.arraycopy(messageData.getBytes(), 0, packetData, 2, messageData.getBytes().length);
                SocketClient.sendPacket(packetData);

                // Display message locally
                formattedMessage = "[" + timestamp + "] You → " + selectedUser + ": " + message + "\n";
                chatHistories.get(selectedUser).append(formattedMessage);
            }

            // Update chat area
            chatArea.setText(chatHistories.get(selectedUser).toString());

            // Clear message field and set focus
            messageField.setText("");
            messageField.requestFocus();

            // Auto-scroll to bottom
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        }
    }

    public static void receiveMessage(String sender, String message, boolean isPrivate) {
        String timestamp = timeFormat.format(new Date());
        String formattedMessage;

        if (isPrivate) {
            // Add to private chat history
            formattedMessage = "[" + timestamp + "] " + sender + " → You: " + message + "\n";

            // Initialize chat history for this user if it doesn't exist
            if (!chatHistories.containsKey(sender)) {
                chatHistories.put(sender, new StringBuilder());
            }
            chatHistories.get(sender).append(formattedMessage);

            // If we are currently viewing this chat, update the chat area
            if (sender.equals(selectedUser)) {
                chatArea.setText(chatHistories.get(sender).toString());
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            } else {
                sendSystemNotification(message ,sender);
            }
        } else {
            // Cheak if the sender is the current user
            if (sender.equals(SocketClient.username)) {
                return; // Ignore messages sent by the current user
            }
            // Add to general chat history
            formattedMessage = "[" + timestamp + "] " + sender + ": " + message + "\n";
            chatHistories.get("General").append(formattedMessage);

            // If we are currently viewing the general chat, update the chat area
            if ("General".equals(selectedUser)) {
                chatArea.setText(chatHistories.get("General").toString());
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            }
        }
    }

    public static void updateUserList() {
        userListModel.removeAllElements();
        userListModel.addElement("General"); // Always keep General chat

        // Add all online users except the current user
        for (String user : SocketClient.onlineUsers) {
            if (!user.equals(SocketClient.username)) {
                userListModel.addElement(user);
            }
        }

        // Ensure the selected user is still in the list or reset to General
        if (!userListModel.contains(selectedUser)) {
            selectedUser = "General";
            updateChatTitle("General");
            updateChatArea("General");
        }

        // Restore selection
        userList.setSelectedValue(selectedUser, true);
    }

    private static TrayIcon trayIcon;

    private static void sendSystemNotification(String message , String sender) {
        if (Main.OS_Name.equals("windows")) {
            windowsSystem(message, sender);
        } else if (Main.OS_Name.equals("linux")) {
            linuxSystem(message, sender);
        } else if (Main.OS_Name.equals("mac")) {
            macSystem(message, sender);
        }
    }

    private static void macSystem(String message, String sender) {
        // for mac
        try {
            String[] cmd = {
                    "osascript",
                    "-e",
                    "display notification \"" + message + "\" with title \"New message from " + sender + "\""
            };
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            System.err.println("Error displaying notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void linuxSystem(String message, String sender) {
        // for linux
        try {
            String[] cmd = {
                    "/bin/sh",
                    "-c",
                    "notify-send -a ProjectEXO 'New message from " + sender + "' '" + message + "'"
            };
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            System.err.println("Error displaying notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void windowsSystem(String message, String sender) {
        try {
            // Check if system tray is supported
            if (!SystemTray.isSupported()) {
                System.out.println("System tray is not supported");
                return;
            }

            SystemTray tray = SystemTray.getSystemTray();

            // Create tray icon only once
            if (trayIcon == null) {
                // Get a default image if custom one not available
                Image image;
                try {
                    // Try to load from resources or use a default system icon
                    image = Toolkit.getDefaultToolkit().createImage(
                            ChatPanel.class.getResource("/icon.png"));
                    if (image == null) {
                        // Fallback to system icon
                        // Convert Icon to Image properly
                        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
                        if (icon instanceof ImageIcon) {
                            image = ((ImageIcon) icon).getImage();
                        } else {
                            // Create a default image if conversion fails
                            image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                        }
                    }
                } catch (Exception e) {
                    // Fallback to system icon
                    Icon icon = UIManager.getIcon("OptionPane.informationIcon");
                    if (icon instanceof ImageIcon) {
                        image = ((ImageIcon) icon).getImage();
                    } else {
                        // Create a default image if conversion fails
                        image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                    }
                }

                trayIcon = new TrayIcon(image, "Chat Application");
                trayIcon.setImageAutoSize(true);

                try {
                    tray.add(trayIcon);
                } catch (AWTException e) {
                    System.err.println("TrayIcon could not be added: " + e.getMessage());
                    return;
                }
            }

            // Select the user on notification click
            trayIcon.addActionListener(e -> {
                userList.setSelectedValue(sender, true);
                updateChatTitle(sender);
                updateChatArea(sender);
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            });

            // Display notification
            trayIcon.displayMessage("New message",
                    "New message from " + selectedUser + ": " + message,
                    TrayIcon.MessageType.INFO);
        } catch (Exception e) {
            System.err.println("Error displaying notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}