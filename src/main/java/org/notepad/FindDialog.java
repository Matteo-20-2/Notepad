package org.notepad;

import javax.swing.*;
import java.awt.*;

// Dialog window for finding text in the editor
public class FindDialog extends JDialog {
    // Input field for the search query
    private JTextField searchField;

    // Buttons to find next and previous occurrences
    private JButton btFindNext, btFindPrev;

    // Reference to the main text area from the editor
    private JTextArea txtArea;

    // Position of the last match found (not used directly in this version)
    private int lastMatchIndex = -1;

    // Constructor that sets up the dialog
    public FindDialog(JFrame parent) {
        super(parent, "Trova", false); // Non-modal dialog titled "Trova" (Italian for "Find")
        setLayout(new FlowLayout());  // Simple horizontal layout

        // Get the text area from the parent editor using a getter
        txtArea = ((TextEditor) parent).getTxtArea();

        // Initialize UI components
        searchField = new JTextField(20);
        btFindNext = new JButton("Find Next");
        btFindPrev = new JButton("Find Prev");

        // Add components to the dialog
        add(new JLabel("Find:"));
        add(searchField);
        add(btFindNext);
        add(btFindPrev);

        // Action listener for "Find Next" button
        btFindNext.addActionListener(e -> {
            String text = txtArea.getText();               // Full text in the editor
            String query = searchField.getText();          // Search query

            if (query.isBlank()) return;                   // Ignore empty searches

            int startPos = txtArea.getCaretPosition();     // Start from current cursor position

            // Search for next occurrence of query after startPos
            int index = text.indexOf(query, startPos);

            if (index != -1) {
                txtArea.requestFocus();                    // Focus back to the text area
                txtArea.select(index, index + query.length()); // Highlight the found text
            } else {
                JOptionPane.showMessageDialog(this, "Next occurrence not found"); // Show message if not found
            }
        });

        // Action listener for "Find Prev" button
        btFindPrev.addActionListener(e -> {
            String text = txtArea.getText();               // Full text in the editor
            String query = searchField.getText();          // Search query

            if (query.isBlank()) return;                   // Ignore empty searches

            int startPos = txtArea.getCaretPosition() - query.length() - 1; // Start before current position

            if (startPos < 0) startPos = 0;                // Ensure valid index

            // Search for previous occurrence of query before startPos
            int index = text.lastIndexOf(query, startPos);

            if (index != -1) {
                txtArea.requestFocus();                    // Focus back to the text area
                txtArea.select(index, index + query.length()); // Highlight the found text
            } else {
                JOptionPane.showMessageDialog(this, "Previous occurrence not found"); // Show message if not found
            }
        });

        // Fit the dialog to its components, center it, and show it
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
