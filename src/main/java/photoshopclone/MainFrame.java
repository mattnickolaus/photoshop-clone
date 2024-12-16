package photoshopclone;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import org.apache.commons.imaging.ImageReadException;
import photoshopclone.Model.Image;
import photoshopclone.Model.Layer;
import photoshopclone.Model.AdjustmentLayer; // Ensure you have this import
import photoshopclone.View.CanvasView;
import photoshopclone.View.ColorPaletteView;
import photoshopclone.Controller.ToolController;
import photoshopclone.View.LayersPanel;
import photoshopclone.View.AdjustmentsPanel;

import java.awt.*;
import java.io.*;
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

    // Adjustments Panel
    private AdjustmentsPanel adjustmentsPanel;

    public MainFrame() {
        setTitle("Photoshop Clone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);

        // Setup menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        // Open Image Menu Item
        JMenuItem openImageMenuItem = new JMenuItem("Open Image");
        openImageMenuItem.addActionListener(e -> openImage());
        fileMenu.add(openImageMenuItem);

        // Export Image Menu Item
        JMenuItem exportImageMenuItem = new JMenuItem("Export Image");
        exportImageMenuItem.addActionListener(e -> exportImage());
        fileMenu.add(exportImageMenuItem);

        // Save As Menu Item
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        saveAsMenuItem.addActionListener(e -> saveAs());
        fileMenu.add(saveAsMenuItem);

        // Open File Menu Item
        JMenuItem openFileMenuItem = new JMenuItem("Open File");
        openFileMenuItem.addActionListener(e -> openFile());
        fileMenu.add(openFileMenuItem);

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
        // Pass lambdas to AdjustmentsPanel so it can get current layer and request repaint
        adjustmentsPanel = new AdjustmentsPanel(
                () -> (toolController != null) ? toolController.getCurrentLayer() : null,
                () -> {
                    // Repaint the canvas
                    System.out.println("Canvas View at Adjustments: " + canvasView.hashCode());
                    canvasView.repaint();
                    System.out.println("Canvas View at Adjustments: " + canvasView.hashCode());
                }
        );

        // Initially, add a placeholder for layers
        JPanel layersPlaceholder = new JPanel();
        layersPlaceholder.setLayout(new GridLayout(0, 1));
        layersPlaceholder.add(new JLabel("<html><h1>Load an image to begin.</h1><h3>'File > Open Image' then select a .png file</h3><p>(.jpg not supported)<p></html>"), SwingConstants.CENTER);

        // Vertical split: adjustments on top, layers on bottom
        adjustmentsLayersSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, adjustmentsPanel, layersPlaceholder);
        adjustmentsLayersSplit.setDividerLocation(700); // Allocate more space to adjustments
        adjustmentsLayersSplit.setOneTouchExpandable(true);
        adjustmentsLayersSplit.setResizeWeight(0.8); // 70% to adjustments

        // innerSplitPane: canvas on left, adjustments+layers on right
        innerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvasView, adjustmentsLayersSplit);
        innerSplitPane.setDividerLocation(1400); // Adjust based on window size
        innerSplitPane.setOneTouchExpandable(true);
        innerSplitPane.setResizeWeight(1.0); // Canvas takes available space

        // outerSplitPane: toolbar on left, and innerSplitPane on right
        outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, toolBarPanel, innerSplitPane);
        outerSplitPane.setDividerLocation(120); // Narrow toolbar on the left
        outerSplitPane.setOneTouchExpandable(true);
        outerSplitPane.setResizeWeight(0.0); // Toolbar maintains its size

        add(outerSplitPane);

        // Add window listener for saving state on close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // Automatically save the project on close
                saveOnExit();
            }
        });

        setVisible(true);
    }

    private void openImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                imageModel.loadImage(selectedFile);
                BufferedImage loadedImage = imageModel.getLoadedImage();

                if (loadedImage == null) {
                    System.err.println("Failed to load the image.");
                    JOptionPane.showMessageDialog(this, "Failed to load the image.", "Error", JOptionPane.ERROR_MESSAGE);
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

                // Add an AdjustmentLayer on top for demonstration
                AdjustmentLayer adjustmentLayer = new AdjustmentLayer(loadedImage.getWidth(), loadedImage.getHeight());
                adjustmentLayer.setName("Adjustment Layer");
                adjustmentLayer.setVisible(true);
                imageModel.addLayer(adjustmentLayer);

                // Update canvas view size
                canvasView.setPreferredSize(new Dimension(loadedImage.getWidth(), loadedImage.getHeight()));
                canvasView.revalidate();
                canvasView.repaint();
                System.out.println("Canvas View in openImage: " + canvasView.hashCode());

                // Recreate tool controller with the brush layer initially
                toolController = new ToolController(canvasView, brushLayer);
                System.out.println("Toolcontroller created: " + toolController.hashCode());
                brushToggle.setEnabled(true);
                panToggle.setEnabled(true);
                brushToggle.setSelected(true);
                toolController.setToolMode(ToolController.ToolMode.BRUSH);

                // Create the LayersPanel now that we have toolController and layers
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
                JOptionPane.showMessageDialog(this, "An error occurred while loading the image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportImage() {
        // Get the combined image from the model
        BufferedImage combinedImage = imageModel.getCombinedImage();

        if (combinedImage == null) {
            JOptionPane.showMessageDialog(this, "No image to export.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Open a file chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Image");
        fileChooser.setSelectedFile(new File("exported_image.png")); // Default filename

        // Set file filter to PNG
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Ensure the file has a .png extension
            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".png")) {
                fileToSave = new File(path + ".png");
            }

            try {
                // Write the image as PNG
                boolean result = javax.imageio.ImageIO.write(combinedImage, "png", fileToSave);
                if (result) {
                    JOptionPane.showMessageDialog(this, "Image exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to export image.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while exporting the image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveAs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Project");
        fileChooser.setSelectedFile(new File("project_save.ser")); // Default filename

        // Set file filter to serialized files
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Serialized Files", "ser"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Ensure the file has a .ser extension
            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".ser")) {
                fileToSave = new File(path + ".ser");
            }

            // Check if file exists and confirm overwrite
            if (fileToSave.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(this,
                        "File already exists. Do you want to overwrite it?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (overwrite != JOptionPane.YES_OPTION) {
                    return; // User chose not to overwrite
                }
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
                oos.writeObject(imageModel);
                JOptionPane.showMessageDialog(this, "Project saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while saving the project.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Project");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Serialized Files", "ser"));

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToOpen))) {
                Image loadedImageModel = (Image) ois.readObject();
                this.imageModel = loadedImageModel;

                // Update canvas view
                canvasView.setImageModel(imageModel);
                if (imageModel.getLoadedImage() != null) {
                    canvasView.setPreferredSize(new Dimension(imageModel.getLoadedImage().getWidth(), imageModel.getLoadedImage().getHeight()));
                }
                canvasView.revalidate();
                canvasView.repaint();
                System.out.println("Canvas View after load: " + canvasView.hashCode());

                // Recreate ToolController or set current layer
                if (toolController != null) {
                    // Find the "brush" layer by name
                    Layer brushLayer = findLayerByName("brush");
                    if (brushLayer != null) {
                        toolController.setCurrentLayer(brushLayer);
                        System.out.println("ToolController set to brush layer: " + brushLayer.getName() + ", Hash: " + brushLayer.hashCode());
                    }
                } else {
                    // If ToolController is null, create a new one with the "brush" layer
                    Layer brushLayer = findLayerByName("brush");
                    if (brushLayer != null) {
                        toolController = new ToolController(canvasView, brushLayer);
                        System.out.println("ToolController created with brush layer: " + brushLayer.getName() + ", Hash: " + brushLayer.hashCode());
                        brushToggle.setEnabled(true);
                        panToggle.setEnabled(true);
                        brushToggle.setSelected(true);
                        toolController.setToolMode(ToolController.ToolMode.BRUSH);
                    }
                }

                // Recreate LayersPanel
                LayersPanel layersPanel = new LayersPanel(imageModel, toolController);
                adjustmentsLayersSplit.setBottomComponent(layersPanel);
                adjustmentsLayersSplit.setDividerLocation(700); // Reset divider if necessary
                adjustmentsLayersSplit.revalidate();
                adjustmentsLayersSplit.repaint();

                revalidate();
                repaint();

                // Programmatically select the "brush" layer in LayersPanel to trigger the selection listener
                layersPanel.setSelectedLayerByName("brush");

                // Debugging Output
                Layer currentLayer = toolController.getCurrentLayer();
                if (currentLayer != null) {
                    System.out.println("Current layer after load: " + currentLayer.getName() + ", Hash: " + currentLayer.hashCode());
                }

                JOptionPane.showMessageDialog(this, "Project loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while loading the project.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper method to find a layer by name
    private Layer findLayerByName(String layerName) {
        for (Layer layer : imageModel.getLayers()) {
            if (layer.getName().equalsIgnoreCase(layerName)) {
                return layer;
            }
        }
        return null;
    }

    private void saveOnExit() {
        // Automatically save the project on close to "last_project.ser"
        File fileToSave = new File("last_project.ser");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
            oos.writeObject(imageModel);
            System.out.println("Project automatically saved to " + fileToSave.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
            // Optionally, notify the user, but avoid blocking the shutdown
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}