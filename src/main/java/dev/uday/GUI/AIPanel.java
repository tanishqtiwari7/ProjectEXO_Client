package dev.uday.GUI;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import dev.uday.NET.SocketClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AIPanel {
    private static JPanel aiPanel;
    private static JTextPane chatArea;
    private static JTextArea messageArea;
    private static JButton sendButton;
    private static JButton backButton;
    private static StringBuilder chatHistory = new StringBuilder();
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    // Markdown parser and renderer
    private static final Parser parser;
    private static final HtmlRenderer renderer;

    static {
        MutableDataSet options = new MutableDataSet();
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }

    public static void setAIPanel() {
        aiPanel = new JPanel(new BorderLayout(5, 5));
        aiPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        aiPanel.add(headerPanel, BorderLayout.NORTH);

        // Create chat area with markdown support
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setContentType("text/html");
        chatArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        chatArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        aiPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Create input panel
        JPanel inputPanel = createInputPanel();
        aiPanel.add(inputPanel, BorderLayout.SOUTH);

        // Update the main panel with AI panel
        MainPanel.contentPanel.removeAll();
        MainPanel.contentPanel.add(aiPanel);
        MainPanel.mainFrame.revalidate();
        MainPanel.mainFrame.repaint();

        updateChatArea();
    }

    private static JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("AI Chat Assistant");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> FeaturePanel.setFeaturePanel());
        headerPanel.add(backButton, BorderLayout.EAST);

        return headerPanel;
    }

    private static JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Multi-line text area for input
        messageArea = new JTextArea(3, 20);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Send message on Ctrl+Enter
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    sendMessage();
                    e.consume();
                }
            }
        });
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        inputPanel.add(messageScrollPane, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        inputPanel.add(sendButton, BorderLayout.EAST);

        JLabel hintLabel = new JLabel("Press Ctrl+Enter to send");
        hintLabel.setFont(new Font(hintLabel.getFont().getName(), Font.ITALIC, 11));
        hintLabel.setForeground(Color.GRAY);
        inputPanel.add(hintLabel, BorderLayout.SOUTH);

        return inputPanel;
    }

    private static void sendMessage() {
        String message = messageArea.getText().trim();
        if (!message.isEmpty()) {
            // Display user message
            appendToChat("You", message, false);

            // Send message to server
            byte[] packetData = new byte[message.getBytes().length + 2];
            packetData[0] = 9; // PacketType for AI request
            packetData[1] = 1; // PacketType for text message
            System.arraycopy(message.getBytes(), 0, packetData, 2, message.getBytes().length);
            SocketClient.sendPacket(packetData);

            // Add "thinking" indicator
            String timestamp = timeFormat.format(new Date());
            chatHistory.append("<div style='margin-bottom: 10px;'><span style='color: gray;'>[")
                    .append(timestamp)
                    .append("]</span> <span style='font-weight: bold; color: #4a86e8;'>Assistant</span> is thinking...</div>");
            updateChatArea();

            // Clear message area and set focus
            messageArea.setText("");
            messageArea.requestFocus();
        }
    }

    public static void receiveAIResponse(String response) {
        // Remove thinking indicator (find last line and remove it)
        int lastNewlineIndex = chatHistory.lastIndexOf("Assistant</span> is thinking...");
        if (lastNewlineIndex != -1) {
            int startOfThinking = chatHistory.lastIndexOf("<div", lastNewlineIndex);
            if (startOfThinking != -1) {
                chatHistory.delete(startOfThinking, chatHistory.length());
            }
        }

        // Add AI response
        appendToChat("Assistant", response, true);
    }

    private static void appendToChat(String sender, String message, boolean isMarkdown) {
        String timestamp = timeFormat.format(new Date());

        chatHistory.append("<div style='margin-bottom: 10px;'><span style='color: gray;'>[")
                .append(timestamp)
                .append("]</span> <span style='font-weight: bold; color: ")
                .append("You".equals(sender) ? "#2ecc71" : "#4a86e8")
                .append(";'>")
                .append(sender)
                .append("</span>: ");

        if (isMarkdown) {
            // Convert markdown to HTML
            Node document = parser.parse(message);
            String html = renderer.render(document);
            chatHistory.append(html);
        } else {
            // Escape HTML special characters for plain text
            message = message.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\n", "<br>");
            chatHistory.append("<span style='white-space: pre-wrap;'>").append(message).append("</span>");
        }

        chatHistory.append("</div>");

        updateChatArea();
    }

    private static void updateChatArea() {
        // Create complete HTML document with styling
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head>");
        html.append("<style type=\"text/css\">");
// Dark theme CSS for markdown rendering
        html.append("body { font: 14px Helvetica, arial, sans-serif; line-height: 1.6; color: #e6edf3;}");
        html.append("* { box-sizing: border-box; }");
        html.append("a { color: #58a6ff; text-decoration: none; }");
        html.append("a:hover { text-decoration: underline; }");
        html.append("h1, h2, h3, h4, h5, h6 { margin-top: 24px; margin-bottom: 16px; font-weight: 600; line-height: 1.25; color: #c9d1d9; }");
        html.append("h1 { font-size: 2em; border-bottom: 1px solid #30363d; padding-bottom: .3em; }");
        html.append("h2 { font-size: 1.5em; border-bottom: 1px solid #30363d; padding-bottom: .3em; }");
        html.append("h3 { font-size: 1.25em; }");
        html.append("h4 { font-size: 1em; }");
        html.append("p, blockquote, ul, ol, dl, table, pre { margin: 16px 0; }");
        html.append("ul, ol { padding-left: 2em; }");
        html.append("blockquote { padding: 0 1em; color: #8b949e; border-left: 0.25em solid #3b434b; }");
        html.append("code { padding: .2em .4em; margin: 0; font-size: 85%; background-color: rgba(110,118,129,0.4); border-radius: 3px; font-family: Consolas, monospace; color: #c9d1d9; }");
        html.append("pre { background-color: #161b22; border-radius: 3px; font-size: 85%; line-height: 1.45; overflow: auto; padding: 16px; }");
        html.append("pre code { background-color: transparent; border: 0; display: inline; line-height: inherit; margin: 0; padding: 0; overflow: visible; }");
        html.append("table { border-collapse: collapse; width: 100%; overflow: auto; }");
        html.append("table th, table td { border: 1px solid #30363d; padding: 6px 13px; }");
        html.append("table tr { background-color: #0d1117; border-top: 1px solid #30363d; }");
        html.append("table tr:nth-child(2n) { background-color: #161b22; }");
        html.append("img { max-width: 100%; }");
        html.append(".highlight .c { color: #8b949e; font-style: italic; }");
        html.append(".highlight .err { color: #f85149; background-color: #2d0102; }");
        html.append(".highlight .k, .highlight .kc, .highlight .kd, .highlight .kn, .highlight .kp, .highlight .kr { font-weight: bold; color: #ff7b72; }");
        html.append(".highlight .o { font-weight: bold; }");
        html.append(".highlight .s, .highlight .sb, .highlight .sc, .highlight .sd, .highlight .s2, .highlight .se, .highlight .sh, .highlight .si, .highlight .sx, .highlight .s1 { color: #a5d6ff; }");
        html.append(".highlight .nb { color: #79c0ff; }");
        html.append(".highlight .nc { color: #f0883e; font-weight: bold; }");
        html.append(".highlight .nt { color: #7ee787; }");
        html.append(".highlight .na { color: #7ee787; }");
        html.append(".highlight .nf { color: #d2a8ff; font-weight: bold; }");
        html.append("</style>");
        html.append("</head><body style='font-family: sans-serif; margin: 0; padding: 5px;'>");
        html.append("<div class=\"markdown-body\">"); // Wrap content in markdown-body class
        //write a welcoming message
        html.append("<h1 style='color: #c9d1d9;'>Welcome to the AI Chat Assistant!</h1>");
        html.append(chatHistory);
        html.append("</div></body></html>");

        // Set the HTML content to the text pane
        chatArea.setText(html.toString());

        // Scroll to bottom after content update
        SwingUtilities.invokeLater(() -> {
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
}