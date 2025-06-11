package org.notepad;

import javax.swing.event.DocumentEvent;
import java.io.File;

// Listener that detects unsaved changes in the document
public class UnsavedChangesListener implements javax.swing.event.DocumentListener {

    // Reference to the main text editor
    private TextEditor editor;

    // Flag indicating whether the document has been modified
    private boolean isModified = false;

    // Constructor: stores a reference to the editor
    public UnsavedChangesListener(TextEditor editor) {
        this.editor = editor;
    }

    // Returns whether the document is currently modified
    public boolean isModified() {
        return isModified;
    }

    // Updates the window title to indicate unsaved changes
    public void updateTitle() {
        String title = editor.getTitle();

        // If not already marked as modified, add an asterisk (*) to the title
        if (!isModified && !title.startsWith("*")) {
            editor.setTitle("*" + title);
            isModified = true;
        }

        // Update the status bar (e.g., showing "Modified" or other info)
        editor.updateStatusBar();
    }

    // Called when text is inserted into the document
    @Override
    public void insertUpdate(DocumentEvent e) {
        updateTitle();
    }

    // Called when text is removed from the document
    @Override
    public void removeUpdate(DocumentEvent e) {
        updateTitle();
    }

    // Called when an attribute or style change occurs (rarely used with plain text)
    @Override
    public void changedUpdate(DocumentEvent e) {
        updateTitle();
    }

    // Resets the modification flag and updates the title after saving
    public void resetModifiedFlag(File file) {
        isModified = false;

        // Set the window title based on the file name or show "Untitled" if null
        if (file != null)
            editor.setTitle("Notepad - " + file.getName());
        else
            editor.setTitle("Notepad - Untitled");
    }
}

