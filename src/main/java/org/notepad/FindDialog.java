package org.notepad;

import javax.swing.*;
import java.awt.*;

public class FindDialog extends JDialog {
    private JTextField searchField;
    private JButton btFindNext, btFindPrev;
    private JTextArea txtArea;
    private int lastMatchIndex = -1; // posizione dell’ultima occorrenza trovata

    public FindDialog(JFrame parent){
        super(parent, "Trova", false);
        setLayout(new FlowLayout());

        txtArea = ((TextEditor) parent).getTxtArea(); // usa getter

        searchField = new JTextField(20);
        btFindNext = new JButton("Find Next");
        btFindPrev = new JButton("Find Prev");

        add(new JLabel("Find:"));
        add(searchField);
        add(btFindNext);
        add(btFindPrev);

        btFindNext.addActionListener(e -> {
            String text = txtArea.getText();
            String query = searchField.getText();

            if (query.isBlank()) return;

            int startPos = txtArea.getCaretPosition();

            // Cerca la prossima occorrenza partendo da startPos
            int index = text.indexOf(query, startPos);

            if (index != -1) {
                txtArea.requestFocus();
                txtArea.select(index, index + query.length());
            } else {
                JOptionPane.showMessageDialog(this, "Next occurrence not found");
            }
        });

        btFindPrev.addActionListener(e -> {
            String text = txtArea.getText();
            String query = searchField.getText();

            if (query.isBlank()) return;

            int startPos = txtArea.getCaretPosition() - query.length() - 1;

            if (startPos < 0) startPos = 0;

            // Cerca l'occorrenza precedente partendo da startPos
            int index = text.lastIndexOf(query, startPos);

            if (index != -1) {
                txtArea.requestFocus();
                txtArea.select(index, index + query.length());
            } else {
                JOptionPane.showMessageDialog(this, "Previous occurrence not found");
            }
        });


        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
