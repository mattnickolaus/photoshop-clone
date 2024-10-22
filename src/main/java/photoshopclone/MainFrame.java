package photoshopclone;

import javax.swing.*;
import photoshopclone.Model.Image;
import photoshopclone.Model.Layer;
import photoshopclone.View.CanvasView;
import photoshopclone.Controller.ToolController;

public class MainFrame extends JFrame {
    private Image imageModel;
    private CanvasView canvasView;
    // Add view once built out
    private Layer currentLayer;

    public MainFrame() {
        setTitle("Photoshop Clone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        imageModel = new Image();
        currentLayer = new Layer(800, 600);
        imageModel.addLayer(currentLayer);

        // Later Add View Class once built out

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}

