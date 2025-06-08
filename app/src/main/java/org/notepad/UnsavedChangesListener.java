package org.notepad;

import javax.swing.event.DocumentEvent;
import java.io.File;

public class UnsavedChangesListener implements javax.swing.event.DocumentListener{
    private TextEditor editor;

    public boolean isModified() {
        return isModified;
    }

    private boolean isModified = false;

    public UnsavedChangesListener(TextEditor editor){
        this.editor = editor;
    }

    public void updateTitle(){
        String title = editor.getTitle();
        if(!isModified && !title.startsWith("*")){
            editor.setTitle("*" + title);
            isModified = true;
        }
        editor.updateStatusBar();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateTitle();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateTitle();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateTitle();

    }

    public void resetModifiedFlag(File file){
        isModified = false;
        if (file != null)
            editor.setTitle("Notepad - " + file.getName());
        else
            editor.setTitle("Notepad - Untitled");
    }

}
