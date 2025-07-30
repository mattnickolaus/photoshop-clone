package photoshopclone.Controller;

import java.util.Stack;
import photoshopclone.Model.Image;

public class UndoManager {
    public interface UndoRedoListener {
        void onUndoRedoStackChanged();
    }

    private Stack<Image> undoStack;
    private Stack<Image> redoStack;
    private Image currentImage;
    private UndoRedoListener listener;

    public UndoManager(Image initialImage) {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        // Make a copy in case initialImage changes externally
        currentImage = initialImage.copy();
        System.out.println("UndoManager initialized. Current stack sizes - Undo: " + undoStack.size() + ", Redo: " + redoStack.size());
    }

    public void setUndoRedoListener(UndoRedoListener listener) {
        this.listener = listener;
    }

    public void saveState(Image newImage) {
        undoStack.push(currentImage.copy());
        currentImage = newImage.copy();
        redoStack.clear();
        System.out.println("State saved. Current stack sizes - Undo: " + undoStack.size() + ", Redo: " + redoStack.size());
        notifyListener();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(currentImage.copy());
            currentImage = undoStack.pop();
            System.out.println("Undo performed. Current stack sizes - Undo: " + undoStack.size() + ", Redo: " + redoStack.size());
            notifyListener();
        } else {
            System.out.println("Undo attempted but undo stack is empty.");
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(currentImage.copy());
            currentImage = redoStack.pop();
            System.out.println("Redo performed. Current stack sizes - Undo: " + undoStack.size() + ", Redo: " + redoStack.size());
            notifyListener();
        } else {
            System.out.println("Redo attempted but redo stack is empty.");
        }
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    private void notifyListener() {
        if (listener != null) {
            listener.onUndoRedoStackChanged();
        }
    }
}