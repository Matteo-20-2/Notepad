package org.notepad;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * TextEditor.java
 *
 * Un semplice editor di testo
 *
 * @author Matteo Marangoni
 * @date 04/06/2025
 */

/*
    ChatGPT: https://chatgpt.com/share/683def73-52b4-8007-b347-79cded5959c8
    TODO: Contatore di parole/caratteri
    TODO: Implementare salvataggio automatico
 */

public class TextEditor extends JFrame {
    private JMenuBar menuBar;
    private JMenu fileMenu, helpMenu, appereanceMenu;
    private JMenuItem newItem, openItem, saveItem, saveWithNameItem, creditsItem, themeModeItem, findItem;
    private JTextArea txtArea;
    private JScrollPane scrollPane;
    private File currentFile;
    private String currentTheme;
    private UnsavedChangesListener listener;

    public JTextArea getTxtArea() {
        return txtArea;
    }

    public String getText(){
        return txtArea.getText();
    }

    public TextEditor() {
        super("Notepad - Untitled");
        setSize(600, 480);
        setLocationRelativeTo(null);

        // Menu
        menuBar = new JMenuBar();
        helpMenu = new JMenu("Help");
        creditsItem = new JMenuItem("Credits");
        fileMenu = new JMenu("File");
        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        findItem = new JMenuItem("Find");
        saveWithNameItem = new JMenuItem("Save With Name");
        appereanceMenu = new JMenu("Appearance");
        themeModeItem = new JMenuItem("☀️ Light Mode");



        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveWithNameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));


        helpMenu.add(creditsItem);

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveWithNameItem);
        fileMenu.add(findItem);

        appereanceMenu.add(themeModeItem);

        menuBar.add(helpMenu);
        menuBar.add(fileMenu);
        menuBar.add(appereanceMenu);
        setJMenuBar(menuBar);

        // Text area
        txtArea = new JTextArea(19, 50);
        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);
        txtArea.setMargin(new Insets(5,5,5,5));
        scrollPane = new JScrollPane(txtArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listener = new UnsavedChangesListener(this);
        txtArea.getDocument().addDocumentListener(listener);
        txtArea.addMouseWheelListener(e -> {
            if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0){
                Font currentFont = txtArea.getFont();
                int fontSize = currentFont.getSize();
                int notches = e.getWheelRotation();

                int newSize = fontSize - notches;
                if(newSize >= 8 && newSize <= 72){
                    txtArea.setFont(new Font(currentFont.getName(), currentFont.getStyle(), newSize));
                }
                e.consume();
            }
        });

        // Layout
        JPanel pMain = new JPanel(new BorderLayout());
        pMain.add(scrollPane, BorderLayout.CENTER);
        Container c = getContentPane();
        c.add(pMain);

        // Azioni menu
        currentFile = null;
        currentTheme = "dark";

        creditsItem.addActionListener(new Credits());
        newItem.addActionListener(new New());
        openItem.addActionListener(new Open());
        saveItem.addActionListener(new Save());
        saveWithNameItem.addActionListener(new SaveWithName());
        findItem.addActionListener(e -> new FindDialog(this));
        themeModeItem.addActionListener(new ChangeTheme());

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    //Classe per fare un nuovo file
    public class New implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentFile != null && !txtArea.getText().isEmpty() && listener.isModified()) {
                int scelta2 = JOptionPane.showConfirmDialog(null, "Do you want to save your current file?", "", JOptionPane.YES_NO_CANCEL_OPTION);
                if (scelta2 == JOptionPane.YES_OPTION) {
                    Save save = new Save();
                    save.actionPerformed(e);
                } else if (scelta2 == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }

            txtArea.setText("");
            currentFile = null;
            listener.resetModifiedFlag(null); // <-- per aggiornare titolo e flag

        }
    }

    public class Credits implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            String credits = """
                    TextEditor v0.6
                    
                    Created by Matteo Marangoni
                    
                    Date of creation: 02/05/2025
                    """;
            JOptionPane.showMessageDialog(null, credits);
        }
    }

    public class Open implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Se c'è un file corrente, chiedi se salvarlo
            if (currentFile != null && !txtArea.getText().isEmpty() && listener.isModified()) {
                int scelta2 = JOptionPane.showConfirmDialog(null, "Do you want to save your current file?", "", JOptionPane.YES_NO_CANCEL_OPTION);
                if (scelta2 == JOptionPane.YES_OPTION) {
                    Save save = new Save();
                    save.actionPerformed(e);
                } else if (scelta2 == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }


            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text File (.txt)", "txt"));
            int scelta = fileChooser.showOpenDialog(null);

            if (scelta == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                StringBuilder text = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        text.append(line).append("\n");
                    }
                    txtArea.setText(text.toString());
                    currentFile = file;
                    //setTitle("Notepad - "+currentFile.getName());
                    listener.resetModifiedFlag(currentFile);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Errore apertura file: " + ex.getMessage());
                }
            }
        }
    }


    // Classe per salvare un file
    public class Save implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            String text = txtArea.getText();

            if (currentFile != null){
                File file = currentFile;
                setTitle("Notepad - "+currentFile.getName());
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(text);
                    JOptionPane.showMessageDialog(null, "File salvato!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Errore salvataggio: " + ex.getMessage());
                }
            }else {
                SaveWithName saveWithName = new SaveWithName();
                saveWithName.actionPerformed(e);
            }


        }
    }

    public class SaveWithName implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            String text = txtArea.getText();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text File (.txt)", "txt"));
            int scelta = fileChooser.showSaveDialog(null);


            if (scelta == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                if (!file.getName().toLowerCase().endsWith(".txt")) {
                    file = new File(file.getAbsolutePath() + ".txt");
                    currentFile = file;
                    setTitle("Notepad - "+currentFile.getName());
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(text);
                    JOptionPane.showMessageDialog(null, "File salvato!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Errore salvataggio: " + ex.getMessage());

                }
            }
        }

    }

    public class ChangeTheme implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {


            try {
                if (currentTheme.equals("dark")) {
                    UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
                    currentTheme = "light";
                    themeModeItem.setText("🌙 Dark Mode");
                } else {
                    UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
                    currentTheme = "dark";
                    themeModeItem.setText("☀️ Light Mode");
                }

                // Aggiorna la UI dell'intera finestra
                SwingUtilities.updateComponentTreeUI(TextEditor.this);

            } catch (UnsupportedLookAndFeelException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


}
