import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
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
    TODO: Aggiungere asterisco nel titolo quando il testo è modificato
    TODO: Aggiungere una funzione "Trova" (CTRL + F) con campo di ricerca
    TODO: Contatore di parole/caratteri
    TODO: Aggiungere una modalità scura attivabile da menu
    TODO: Implementare salvataggio automatico
 */

public class TextEditor extends JFrame {
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem newItem, openItem, saveItem, saveWithNameItem;
    JTextArea txtArea;
    JScrollPane scrollPane;
    File currentFile;

    public TextEditor() {
        super("Notepad - Untitled");
        setSize(600, 480);
        setLocationRelativeTo(null);

        // Menu
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        saveWithNameItem = new JMenuItem("Save With Name");

        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveWithNameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveWithNameItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Text area
        txtArea = new JTextArea(19, 50);
        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);
        txtArea.setMargin(new Insets(5,5,5,5));
        scrollPane = new JScrollPane(txtArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        // Layout
        JPanel pMain = new JPanel(new BorderLayout());
        pMain.add(scrollPane, BorderLayout.CENTER);
        Container c = getContentPane();
        c.add(pMain);

        // Azioni menu
        currentFile = null;

        newItem.addActionListener(new New());
        openItem.addActionListener(new Open());
        saveItem.addActionListener(new Save());
        saveWithNameItem.addActionListener(new SaveWithName());

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    //Classe per fare un nuovo file
    public class New implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentFile != null && !txtArea.getText().isEmpty()) {
                int scelta = JOptionPane.showConfirmDialog(null, "Do you want to save your current file?", "", JOptionPane.YES_NO_CANCEL_OPTION);
                if (scelta == JOptionPane.YES_OPTION) {
                    Save save = new Save();
                    save.actionPerformed(e);
                } else if (scelta == JOptionPane.CANCEL_OPTION) {
                    return; // Annulla il "New"
                }
            }
            txtArea.setText("");
            currentFile = null;
            setTitle("Notepad - Untitled");
        }
    }


    public class Open implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Se c'è un file corrente, chiedi se salvarlo
            if (currentFile != null && !txtArea.getText().isEmpty()) {
                int scelta2 = JOptionPane.showConfirmDialog(null, "Do you want to save your current file?", "", JOptionPane.YES_NO_CANCEL_OPTION);
                if (scelta2 == JOptionPane.YES_OPTION) {
                    Save save = new Save();
                    save.actionPerformed(e);
                    setTitle("Notepad - "+currentFile.getName());
                } else if (scelta2 == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text File (.txt)", "txt"));
            int scelta = fileChooser.showOpenDialog(null);

            if (scelta == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                currentFile = file;
                setTitle("Notepad - "+currentFile.getName());

                StringBuilder text = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        text.append(line).append("\n");
                    }
                    txtArea.setText(text.toString());
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
}
