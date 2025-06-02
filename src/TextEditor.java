import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;

public class TextEditor extends JFrame {
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem openItem, saveItem;
    JTextArea txtArea;
    JScrollPane scrollPane;

    public TextEditor() {
        super("Notepad");
        setSize(600, 480);
        setLocationRelativeTo(null);

        // Menu
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");

        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Text area
        txtArea = new JTextArea(19, 50);
        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);
        scrollPane = new JScrollPane(txtArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Layout
        JPanel pMain = new JPanel(new BorderLayout());
        pMain.add(scrollPane, BorderLayout.CENTER);
        Container c = getContentPane();
        c.add(pMain);

        // Azioni menu
        openItem.addActionListener(new Open());
        saveItem.addActionListener(new Save());

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // Classe per aprire un file
    public class Open implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder text = new StringBuilder();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text File (.txt)", "txt"));
            int scelta = fileChooser.showOpenDialog(null);

            if (scelta == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
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
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text File (.txt)", "txt"));
            int scelta = fileChooser.showSaveDialog(null);

            if (scelta == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".txt")) {
                    file = new File(file.getAbsolutePath() + ".txt");
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
