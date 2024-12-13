package photoshopclone;

import javax.swing.*;
import org.apache.commons.imaging.ImageReadException;
import photoshopclone.Model.Image;
import photoshopclone.Model.Layer;
import photoshopclone.View.CanvasView;
import photoshopclone.View.ColorPaletteView;
import photoshopclone.Controller.ToolController;

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
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BoxLayout(toolBarPanel, BoxLayout.Y_AXIS));

        // Add a vertical strut at the top for spacing
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
        // Add vertical spacing between buttons
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
        // Center and add Pan button
        panToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolBarPanel.add(panToggle);
        // Add vertical spacing
        toolBarPanel.add(Box.createVerticalStrut(10));

        // Zoom buttons
        JButton zoomInButton = new JButton("+");
        zoomInButton.addActionListener(e -> canvasView.zoomIn());
        zoomInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolBarPanel.add(zoomInButton);
        // Add vertical spacing
        toolBarPanel.add(Box.createVerticalStrut(10));

        JButton zoomOutButton = new JButton("-");
        zoomOutButton.addActionListener(e -> canvasView.zoomOut());
        zoomOutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolBarPanel.add(zoomOutButton);
        // Add vertical spacing
        toolBarPanel.add(Box.createVerticalStrut(10));

        JButton resetViewButton = new JButton("Reset View");
        resetViewButton.addActionListener(e -> canvasView.resetView());
        resetViewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolBarPanel.add(resetViewButton);
        // Add a final vertical strut at the bottom
        toolBarPanel.add(Box.createVerticalStrut(20));

        // Use a ButtonGroup if you want exclusive selection of tools:
        ButtonGroup toolGroup = new ButtonGroup();
        toolGroup.add(brushToggle);
        toolGroup.add(panToggle);

        // Create the right-side panel (for layers and adjustments)
        JPanel rightSidePanel = new JPanel(new BorderLayout());
        rightSidePanel.add(new JLabel("Layers/Adjustments Placeholder"), BorderLayout.CENTER);

        // Add color palette below or above adjustments
        ColorPaletteView colorPaletteView = new ColorPaletteView(color -> {
            if (toolController != null) {
                toolController.setBrushColor(color);
            }
        });
        rightSidePanel.add(colorPaletteView, BorderLayout.SOUTH);

        // Create the inner split pane: center canvas and right side panel
        JSplitPane innerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvasView, rightSidePanel);
        innerSplitPane.setDividerLocation(1400); // Adjust as needed
        innerSplitPane.setOneTouchExpandable(true);
        innerSplitPane.setResizeWeight(1.0);

        // Create the outer split pane: left toolbar, and the inner split pane
        JSplitPane outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, toolBarPanel, innerSplitPane);
        outerSplitPane.setDividerLocation(100); // Narrow toolbar on the left
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
                testG.fillRect(10, 10, 50, 50); // Draw a red square
                testG.dispose();

                canvasView.setPreferredSize(new Dimension(combinedImage.getWidth(), combinedImage.getHeight()));
                canvasView.revalidate();
                canvasView.repaint();

                // Recreate tool controller
                toolController = new ToolController(canvasView, drawingLayer);
                brushToggle.setEnabled(true);
                panToggle.setEnabled(true);

                // Default to brush mode
                brushToggle.setSelected(true);
                toolController.setToolMode(ToolController.ToolMode.BRUSH);

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