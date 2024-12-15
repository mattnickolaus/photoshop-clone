package photoshopclone;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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
    private Layer imageLayer;
    private Layer brushLayer;

    private JToggleButton brushToggle;
    private JToggleButton panToggle;

    // Split pane references
    private JSplitPane outerSplitPane;
    private JSplitPane innerSplitPane;
    private JSplitPane adjustmentsLayersSplit;

    // Color indicator panel
    private JPanel colorIndicatorPanel;

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
        toolBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

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

        // Add color indicator square
        colorIndicatorPanel = new JPanel();
        colorIndicatorPanel.setPreferredSize(new Dimension(30, 30));
        colorIndicatorPanel.setMaximumSize(new Dimension(30, 30));
        colorIndicatorPanel.setBackground(Color.BLACK); // Initial color
        colorIndicatorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        colorIndicatorPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolBarPanel.add(colorIndicatorPanel);
        toolBarPanel.add(Box.createVerticalStrut(10));

        // Add color palette below the color indicator
        ColorPaletteView colorPaletteView = new ColorPaletteView(color -> {
            if (toolController != null) {
                toolController.setBrushColor(color);
                colorIndicatorPanel.setBackground(color);
            }
        });
        toolBarPanel.add(colorPaletteView);

        // Use a ButtonGroup for exclusive selection of tools
        ButtonGroup toolGroup = new ButtonGroup();
        toolGroup.add(brushToggle);
        toolGroup.add(panToggle);

        // Create the adjustments panel (top) and layers panel (bottom)
        adjustmentsPanel = new AdjustmentsPanel();

        // Initially, add a placeholder for layers
        JPanel layersPlaceholder = new JPanel();
        layersPlaceholder.setLayout(new BorderLayout());
        layersPlaceholder.add(new JLabel("Load an image to see layers"), BorderLayout.CENTER);

        // Vertical split: adjustments on top, layers on bottom
        adjustmentsLayersSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, adjustmentsPanel, layersPlaceholder);
        adjustmentsLayersSplit.setDividerLocation(700); // Allocate more space to adjustments
        adjustmentsLayersSplit.setOneTouchExpandable(true);
        adjustmentsLayersSplit.setResizeWeight(0.7); // 70% to adjustments

        // innerSplitPane: canvas on left, adjustments+layers on right
        innerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvasView, adjustmentsLayersSplit);
        innerSplitPane.setDividerLocation(1400); // Adjust based on window size
        innerSplitPane.setOneTouchExpandable(true);
        innerSplitPane.setResizeWeight(1.0); // Canvas takes available space

        // outerSplitPane: toolbar on left, and innerSplitPane on right
        outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, toolBarPanel, innerSplitPane);
        outerSplitPane.setDividerLocation(100); // Narrow toolbar on the left
        outerSplitPane.setOneTouchExpandable(true);
        outerSplitPane.setResizeWeight(0.0); // Toolbar maintains its size

        add(outerSplitPane);

        setVisible(true);
    }

    private AdjustmentsPanel adjustmentsPanel;

    private void openImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                imageModel.loadImage(selectedFile);
                BufferedImage loadedImage = imageModel.getLoadedImage(); // Do you mean getCombinedImage

                if (loadedImage == null) {
                    System.err.println("Failed to load the image.");
                    return;
                }

                // Clear existing layers if any
                imageModel.clearLayers();

                // Create "image" layer
                imageLayer = new Layer(loadedImage.getWidth(), loadedImage.getHeight());
                Graphics2D gImage = imageLayer.getImage().createGraphics();
                gImage.drawImage(loadedImage, 0, 0, null);
                gImage.dispose();
                imageLayer.setName("image");
                imageLayer.setVisible(true);
                imageModel.addLayer(imageLayer);

                // Create "brush" layer
                brushLayer = new Layer(loadedImage.getWidth(), loadedImage.getHeight());
                Graphics2D gBrush = brushLayer.getImage().createGraphics();
                gBrush.setComposite(AlphaComposite.Clear);
                gBrush.fillRect(0, 0, loadedImage.getWidth(), loadedImage.getHeight());
                gBrush.dispose();
                brushLayer.setName("brush");
                brushLayer.setVisible(true);
                imageModel.addLayer(brushLayer);

                // Test drawing on the brush layer
                Graphics2D testG = brushLayer.getImage().createGraphics();
                testG.setColor(Color.RED);
                testG.fillRect(10, 10, 50, 50);
                testG.dispose();

                // Update canvas view size
                canvasView.setPreferredSize(new Dimension(loadedImage.getWidth(), loadedImage.getHeight()));
                canvasView.revalidate();
                canvasView.repaint();

                // Recreate tool controller
                toolController = new ToolController(canvasView, brushLayer);
                brushToggle.setEnabled(true);
                panToggle.setEnabled(true);
                brushToggle.setSelected(true);
                toolController.setToolMode(ToolController.ToolMode.BRUSH);

                // Create the LayersPanel since we have toolController and layers
                LayersPanel layersPanel = new LayersPanel(imageModel, toolController);

                // Replace the placeholder with the actual layersPanel
                adjustmentsLayersSplit.setBottomComponent(layersPanel);
                adjustmentsLayersSplit.setDividerLocation(700); // Reset divider if necessary
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
