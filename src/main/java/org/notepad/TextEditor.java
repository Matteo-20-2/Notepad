package org.notepad;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.StringTokenizer;

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
    TODO: Fare in modo che quando si cambia il tema si cambi anche il background e il foreground
    TODO: Inserire il resto dei linguaggi
 */

public class TextEditor extends JFrame {
    private JMenuBar menuBar;
    private JMenu fileMenu, helpMenu, appereanceMenu;
    private JMenuItem newItem, openItem, saveItem, saveWithNameItem, creditsItem, themeModeItem, findItem;
    private RSyntaxTextArea txtArea;
    private RTextScrollPane scrollPane;
    private File currentFile;
    private String currentTheme;
    private UnsavedChangesListener listener;
    private JLabel statusBar;

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
        txtArea = new RSyntaxTextArea(19, 50);
        txtArea.setCurrentLineHighlightColor(Color.DARK_GRAY);
        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtArea.setForeground(Color.WHITE);
        txtArea.setBackground(Color.DARK_GRAY);
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);
        txtArea.setMargin(new Insets(5,5,5,5));
        scrollPane = new RTextScrollPane(txtArea);
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

        statusBar = new JLabel("Words: 0    |   Chars: 0");
        statusBar.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        // Layout
        JPanel pMain = new JPanel(new BorderLayout());
        pMain.add(scrollPane, BorderLayout.CENTER);
        pMain.add(statusBar, BorderLayout.SOUTH);
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

    public void setSintax(){
        System.out.println("sono nella funzione");
        String fileName = currentFile.toString().toLowerCase();
        StringTokenizer tokenizer = new StringTokenizer(fileName, ".");

        String nome = tokenizer.nextToken();
        String extension = tokenizer.nextToken();
        System.out.println(extension);

        switch (extension){
            case "py":
                txtArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);;
                break;

            case "java":
                txtArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                break;

            case "c":
                txtArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
                break;

            case "cpp":
                txtArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
                break;

            default:
                System.out.println("BOH");
                break;
        }
    }

    public void updateStatusBar(){
        String text = txtArea.getText().trim();
        int charCount = text.length();
        int wordCount = text.isEmpty() ? 0 : text.split("\\s+").length;
        statusBar.setText("Words: "+wordCount + "   |   Chars: "+charCount);
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
            listener.resetModifiedFlag(null);

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
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text File (.txt)", "txt", "py", "c", "cpp", "java"));
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

                    setSintax();

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

    public class SaveWithName implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String text = txtArea.getText();
            JFileChooser fileChooser = new JFileChooser();

            // Mostra tutti i file (non forza .txt)
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
            int scelta = fileChooser.showSaveDialog(null);

            if (scelta == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                // Aggiunge .txt solo se NON è presente nessuna estensione
                if (!file.getName().contains(".")) {
                    file = new File(file.getAbsolutePath() + ".txt");
                }

                currentFile = file;
                setTitle("Notepad - " + currentFile.getName());

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(text);
                    JOptionPane.showMessageDialog(null, "File salvato!");
                    setSintax();
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
                    txtArea.setBackground(Color.WHITE);
                    txtArea.setCurrentLineHighlightColor(Color.WHITE);
                    txtArea.setForeground(Color.BLACK);




                } else {
                    UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
                    currentTheme = "dark";
                    themeModeItem.setText("☀️ Light Mode");
                    txtArea.setBackground(Color.DARK_GRAY);
                    txtArea.setCurrentLineHighlightColor(Color.DARK_GRAY);
                    txtArea.setForeground(Color.WHITE);

                }

                // Aggiorna la UI dell'intera finestra
                SwingUtilities.updateComponentTreeUI(TextEditor.this);

            } catch (UnsupportedLookAndFeelException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


}
