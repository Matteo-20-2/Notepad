package org.example;

import javax.swing.JTextPane;
import javax.swing.text.*;


public class TextDecoration {

    public static void applyBold(JTextPane textPane){
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        if (start == end) return;

        Style style = textPane.addStyle("BoldStyle", null);
        StyleConstants.setBold(style, true);
        doc.setCharacterAttributes(start, end - start, style, false);
    }
}
