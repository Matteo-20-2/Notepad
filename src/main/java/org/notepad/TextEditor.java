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

public class TextEditor extends JFrame {
    // UI components and core variables
    private JMenuBar menuBar;
    private JMenu fileMenu, helpMenu, appereanceMenu;
    private JMenuItem newItem, openItem, saveItem, saveWithNameItem, creditsItem, themeModeItem, findItem;
    private RSyntaxTextArea txtArea;
    private RTextScrollPane scrollPane;
    private File currentFile;
    private String currentTheme;
    private UnsavedChangesListener listener;
    private JLabel statusBar;

    // Accessor for the text area
    public JTextArea getTxtArea() {
        return txtArea;
    }

    // Returns the full text content
    public String getText(){
        return txtArea.getText();
    }

    public TextEditor() {
        super("Notepad - Untitled");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/app-icon.png")));

        setSize(600, 480);
        setLocationRelativeTo(null); // Center window

        // Initialize menus and menu items
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

        // Add keyboard shortcuts
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveWithNameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));

        // Add items to menus
        helpMenu.add(creditsItem);
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveWithNameItem);
        fileMenu.add(findItem);
        appereanceMenu.add(themeModeItem);

        // Add menus to the menu bar
        menuBar.add(helpMenu);
        menuBar.add(fileMenu);
        menuBar.add(appereanceMenu);
        setJMenuBar(menuBar);

        // Text editor setup with syntax highlighting
        txtArea = new RSyntaxTextArea(19, 50);
        txtArea.setCurrentLineHighlightColor(Color.DARK_GRAY);
        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtArea.setForeground(Color.WHITE);
        txtArea.setBackground(Color.DARK_GRAY);
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);
        txtArea.setMargin(new Insets(5,5,5,5));

        scrollPane = new RTextScrollPane(txtArea);
        scrollPane.setWheelScrollingEnabled(true);

        listener = new UnsavedChangesListener(this);
        txtArea.getDocument().addDocumentListener(listener);

        // Enable zoom with Ctrl + mouse wheel
        txtArea.addMouseWheelListener(e -> {
            if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                // Zooming in/out
                Font currentFont = txtArea.getFont();
                int fontSize = currentFont.getSize();
                int notches = e.getWheelRotation();

                int newSize = fontSize - notches;
                if (newSize >= 8 && newSize <= 72) {
                    txtArea.setFont(new Font(currentFont.getName(), currentFont.getStyle(), newSize));
                }
            } else {
                // Normal scrolling
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                int notches = e.getWheelRotation();
                int scrollSpeed = 15;
                int newValue = verticalScrollBar.getValue() + (notches * verticalScrollBar.getUnitIncrement() * scrollSpeed);
                verticalScrollBar.setValue(newValue);
            }
            e.consume();
        });

        // Status bar for word and character count
        statusBar = new JLabel("Words: 0    |   Chars: 0");
        statusBar.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        // Layout configuration
        JPanel pMain = new JPanel(new BorderLayout());
        pMain.add(scrollPane, BorderLayout.CENTER);
        pMain.add(statusBar, BorderLayout.SOUTH);
        Container c = getContentPane();
        c.add(pMain);

        // Initial setup
        currentFile = null;
        currentTheme = "dark";

        // Add action listeners to menu items
        creditsItem.addActionListener(new Credits());
        newItem.addActionListener(new New());
        openItem.addActionListener(new Open());
        saveItem.addActionListener(new Save());
        saveWithNameItem.addActionListener(new SaveWithName());
        findItem.addActionListener(e -> new FindDialog(this));
        themeModeItem.addActionListener(new ChangeTheme());

        // Show window
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // Detects file extension and applies syntax highlighting accordingly
    public void setSintax(){
        System.out.println("sono nella funzione");
        String fileName = currentFile.toString().toLowerCase();
        StringTokenizer tokenizer = new StringTokenizer(fileName, ".");

        String nome = tokenizer.nextToken();
        String extension = tokenizer.nextToken();

        switch (extension){
            case "py":
                txtArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
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

            case "html":
                txtArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
                break;

            case "css":
                txtArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);
                break;

            case "js":
                txtArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
                break;

            case "json":
                txtArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
                break;
        }
    }

    // Updates word and character count in status bar
    public void updateStatusBar(){
        String text = txtArea.getText().trim();
        int charCount = text.length();
        int wordCount = text.isEmpty() ? 0 : text.split("\\s+").length;
        statusBar.setText("Words: "+wordCount + "   |   Chars: "+charCount);
    }

    // Creates a new file (asks to save if current is modified)
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

    // Displays credits and version info
    public class Credits implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String credits = """
                TextEditor v1.0

                Created by Matteo Marangoni

                Date of creation: 02/06/2025
                
                Last Update Date: 02/11/2025
                
                Features:
                • Syntax highlighting for Java, Python, C, and C++, HTML, CSS, JavaScript, JSON
                • Light/Dark theme support
                • Find text functionality
                • Line numbers
                • Zoom in/out with Ctrl+Mouse wheel
                """;
            JOptionPane.showMessageDialog(null, credits);
        }
    }

    // Opens a file and sets syntax highlighting based on extension
    public class Open implements ActionListener {
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

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter(
                    "Source Files (*.java, *.py, *.c, *.cpp, *html, *css, *js, *json) & Text Files (*.txt)",
                    "java", "py", "c", "cpp", "txt", "html", "js", "css", "json"
            ));

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
                    listener.resetModifiedFlag(currentFile);

                    setSintax();

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Errore apertura file: " + ex.getMessage());
                }
            }
        }
    }

    // Saves to current file if available, otherwise asks for file name
    public class Save implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = txtArea.getText();

            if (currentFile != null){
                File file = currentFile;
                setTitle("Notepad - "+currentFile.getName());
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(text);
                    JOptionPane.showMessageDialog(null, "File Saved!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error Saving: " + ex.getMessage());
                }
            } else {
                SaveWithName saveWithName = new SaveWithName();
                saveWithName.actionPerformed(e);
            }
        }
    }

    // Asks user for file name and saves it
    public class SaveWithName implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = txtArea.getText();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter(
                    "Source Files (*.java, *.py, *.c, *.cpp, *html, *css, *js, *json) & Text Files (*.txt)",
                    "java", "py", "c", "cpp", "txt", "html", "js", "css", "json"
            ));

            int scelta = fileChooser.showSaveDialog(null);

            if (scelta == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                // Adds .txt if no extension is given
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

    // Switches between light and dark themes
    public class ChangeTheme implements ActionListener {
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

                    // Adjust line number gutter colors for light theme
                    scrollPane.getGutter().setBackground(new Color(240, 240, 240));
                    scrollPane.getGutter().setLineNumberColor(Color.GRAY);
                    scrollPane.getGutter().setBorderColor(new Color(200, 200, 200));
                } else {
                    UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
                    currentTheme = "dark";
                    themeModeItem.setText("☀️ Light Mode");
                    txtArea.setBackground(Color.DARK_GRAY);
                    txtArea.setCurrentLineHighlightColor(Color.DARK_GRAY);
                    txtArea.setForeground(Color.WHITE);

                    // Adjust line number gutter colors for dark theme
                    scrollPane.getGutter().setBackground(new Color(60, 60, 60));
                    scrollPane.getGutter().setLineNumberColor(Color.LIGHT_GRAY);
                    scrollPane.getGutter().setBorderColor(new Color(80, 80, 80));
                }

                SwingUtilities.updateComponentTreeUI(TextEditor.this);
            } catch (UnsupportedLookAndFeelException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
