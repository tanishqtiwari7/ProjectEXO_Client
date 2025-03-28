package dev.uday.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static dev.uday.Main.About;

public class FeaturePanel {
    public static JPanel featurePanel = new JPanel();
    private static JList<String> featuresList;
    private static JTextArea featureInfoArea;
    private static JButton openButton;

    public static void setFeaturePanel() {

        MainPanel.contentPanel.removeAll(); // Clear existing components

        // Clear any existing components
        featurePanel.removeAll();

        // Set layout for the feature panel
        featurePanel.setLayout(new BorderLayout());

        // Create a split pane to divide left and right sections
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200); // Width of left panel

        // Left panel - Features list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Features"));

        // Create and populate the features list
        String[] features = {"Chat", "Feature 2", "Feature 3", "About"};
        featuresList = new JList<>(features);
        featuresList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(featuresList);

        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        // Right panel - Feature info and open button
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Feature Details"));

        // Feature info area
        featureInfoArea = new JTextArea("Select a feature to view details");
        featureInfoArea.setEditable(false);
        featureInfoArea.setLineWrap(true);
        featureInfoArea.setWrapStyleWord(true);
        JScrollPane infoScrollPane = new JScrollPane(featureInfoArea);
        rightPanel.add(infoScrollPane, BorderLayout.CENTER);

        // Button panel at bottom right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        openButton = new JButton("Open");
        openButton.setEnabled(false); // Initially disabled until feature is selected
        buttonPanel.add(openButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add list selection listener
        featuresList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = featuresList.getSelectedValue();
                if (Objects.equals(selected, "Chat")) {
                    featureInfoArea.setText("Information about " + selected + "\n\n" +
                            "This is a chat feature that allows you to communicate with other users.\n");
                    openButton.setEnabled(true);
                } else if (Objects.equals(selected, "About")) {
                    featureInfoArea.setText(About);
                    openButton.setEnabled(false);
                } else if (selected != null) {
                    featureInfoArea.setText("Information about " + selected + "\n\n" +
                            "This is a detailed description of " + selected + ".");
                    openButton.setEnabled(true);
                } else {
                    featureInfoArea.setText("Select a feature to view details");
                    openButton.setEnabled(false);
                }
            }
        });

        // Add open button action listener
        openButton.addActionListener(e -> {
            String selected = featuresList.getSelectedValue();
            if (Objects.equals(selected, "Chat")) {
                // Open chat feature and remove feature panel
                ChatPanel.setChatPanel();
            } else {
                // Open other features
                JOptionPane.showMessageDialog(MainPanel.mainFrame,
                        "Opening " + selected, "Open Feature",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Add panels to split pane
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        // Add split pane to feature panel
        featurePanel.add(splitPane, BorderLayout.CENTER);

        // Add feature panel to content panel
        MainPanel.contentPanel.setLayout(new BorderLayout());
        MainPanel.contentPanel.add(featurePanel, BorderLayout.CENTER);

        MainPanel.mainFrame.revalidate();
    }
}