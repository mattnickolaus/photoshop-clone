package photoshopclone.Controller;

import java.util.Stack;
import photoshopclone.Model.Image;

public class UndoManager {
    private Stack<Image> undoStack;
    private Stack<Image> redoStack;
    private Image currentImage;

    public UndoManager(Image initialImage) {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        // Make a copy in case initialImage changes externally
        currentImage = initialImage.copy();
    }

    public void saveState() {
        undoStack.push(currentImage.copy());
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(currentImage.copy());
            currentImage = undoStack.pop();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(currentImage.copy());
            currentImage = redoStack.pop();
        }
    }

    public Image getCurrentImage() {
        return currentImage;
    }
}