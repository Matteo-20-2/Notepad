package org.example;
import com.formdev.flatlaf.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
            ex.printStackTrace();
        }

        UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());

        TextEditor textEditor = new TextEditor();

    }
}
