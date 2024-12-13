package photoshopclone;

import javax.swing.*;
import org.apache.commons.imaging.ImageReadException;
import photoshopclone.Model.Image;
import photoshopclone.Model.Layer;
import photoshopclone.View.CanvasView;
import photoshopclone.View.ColorPaletteView;
import photoshopclone.Controller.ToolController;
import photoshopclone.View.LayersPanel;
import photoshopclone.View.AdjustmentsPanel;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class MainFrame extends JFrame {
    private Image imageModel;
    private CanvasView canvasView;
    private ToolController toolController;
    private Layer drawingLayer;

    private JToggleButton brushToggle;
    private JToggleButton panToggle;

    // Keep references to your split panes and panels as fields
    private JPanel toolBarPanel;
    private AdjustmentsPanel adjustmentsPanel;
    private JPanel layersPlaceholder;
    private JSplitPane adjustmentsLayersSplit;
    private JSplitPane innerSplitPane;
    private JSplitPane outerSplitPane;

    public MainFrame() {
        setTitle("Photoshop Clone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);

        // Setup menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openMenuItem = new JMenuItem("Open Image");
        openMenuItem.addActionListener(e -> openImage());
        fileMenu.add(openMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Initialize model and canvas
        imageModel = new Image();
        canvasView = new CanvasView(imageModel);

        // Create toolbar panel (on the left)
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BoxLayout(toolBarPanel, BoxLayout.Y_AXIS));
        toolBarPanel.add(Box.createVerticalStrut(20));

        // Brush toggle
        brushToggle = new JToggleButton("Brush");
        brushToggle.setEnabled(false);
        brushToggle.addActionListener(e -> {
            if (toolController != null && brushToggle.isSelected()) {
                toolController.setToolMode(ToolController.ToolMode.BRUSH);
                panToggle.setSelected(false);
            }
        });
        brushToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolBarPanel.add(brushToggle);
        toolBarPanel.add(Box.createVerticalStrut(10));

        // Pan toggle
        panToggle = new JToggleButton("Pan");
        panToggle.setEnabled(false);
        panToggle.addActionListener(e -> {
            if (toolController != null && panToggle.isSelected()) {
                toolController.setToolMode(ToolController.ToolMode.PAN);
                brushToggle.setSelected(false);
            }
        });
        panToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolBarPanel.add(panToggle);
        toolBarPanel.add(Box.createVerticalStrut(10));

        // Zoom in
        JButton zoomInButton = new JButton("+");
        zoomInButton.addActionListener(e -> canvasView.zoomIn());
        zoomInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolBarPanel.add(zoomInButton);
        toolBarPanel.add(Box.createVerticalStrut(10));

        // Zoom out
        JButton zoomOutButton = new JButton("-");
        zoomOutButton.addActionListener(e -> canvasView.zoomOut());
        zoomOutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolBarPanel.add(zoomOutButton);
        toolBarPanel.add(Box.createVerticalStrut(10));

        // Reset view
        JButton resetViewButton = new JButton("Reset View");
        resetViewButton.addActionListener(e -> canvasView.resetView());
        resetViewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolBarPanel.add(resetViewButton);
        toolBarPanel.add(Box.createVerticalStrut(20));

        // Add color palette below or above adjustments
        ColorPaletteView colorPaletteView = new ColorPaletteView(color -> {
            if (toolController != null) {
                toolController.setBrushColor(color);
            }
        });
        toolBarPanel.add(colorPaletteView, BorderLayout.SOUTH);

        ButtonGroup toolGroup = new ButtonGroup();
        toolGroup.add(brushToggle);
        toolGroup.add(panToggle);

        // Create the adjustments panel (top) and a placeholder for layers (bottom)
        adjustmentsPanel = new AdjustmentsPanel();
        layersPlaceholder = new JPanel();
        layersPlaceholder.add(new JLabel("Load an image to see layers"));

        // Vertical split: adjustments on top, layers on bottom
        adjustmentsLayersSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, adjustmentsPanel, layersPlaceholder);
        adjustmentsLayersSplit.setDividerLocation(200);

        // innerSplitPane: canvas on left, adjustments+layers on right
        innerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvasView, adjustmentsLayersSplit);
        innerSplitPane.setDividerLocation(1400);
        innerSplitPane.setOneTouchExpandable(true);
        innerSplitPane.setResizeWeight(1.0);

        // outerSplitPane: toolbar on left, main area on right
        outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, toolBarPanel, innerSplitPane);
        outerSplitPane.setDividerLocation(100);
        outerSplitPane.setOneTouchExpandable(true);

        add(outerSplitPane);

        setVisible(true);
    }

    private void openImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                imageModel.loadImage(selectedFile);
                BufferedImage combinedImage = imageModel.getCombinedImage();

                if (combinedImage == null) {
                    System.err.println("Failed to load or combine image.");
                    return;
                }

                // Add a transparent drawing layer
                drawingLayer = new Layer(combinedImage.getWidth(), combinedImage.getHeight());
                Graphics2D g2d = drawingLayer.getImage().createGraphics();
                g2d.setComposite(AlphaComposite.Clear);
                g2d.fillRect(0, 0, combinedImage.getWidth(), combinedImage.getHeight());
                g2d.dispose();
                imageModel.addLayer(drawingLayer);

                // Test drawing on the drawing layer
                Graphics2D testG = drawingLayer.getImage().createGraphics();
                testG.setColor(Color.RED);
                testG.fillRect(10, 10, 50, 50);
                testG.dispose();

                canvasView.setPreferredSize(new Dimension(combinedImage.getWidth(), combinedImage.getHeight()));
                canvasView.revalidate();
                canvasView.repaint();

                // Recreate tool controller
                toolController = new ToolController(canvasView, drawingLayer);
                brushToggle.setEnabled(true);
                panToggle.setEnabled(true);
                brushToggle.setSelected(true);
                toolController.setToolMode(ToolController.ToolMode.PAN);

                // Now create the LayersPanel since we have toolController and layers
                LayersPanel layersPanel = new LayersPanel(imageModel, toolController);

                // Directly replace the placeholder with the actual layersPanel
                adjustmentsLayersSplit.setBottomComponent(layersPanel);
                adjustmentsLayersSplit.revalidate();
                adjustmentsLayersSplit.repaint();

                revalidate();
                repaint();

            } catch (IOException | ImageReadException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}