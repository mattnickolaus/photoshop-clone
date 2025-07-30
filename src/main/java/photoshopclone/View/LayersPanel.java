package photoshopclone.View;

import photoshopclone.Controller.UndoManager;
import photoshopclone.Model.Image;
import photoshopclone.Model.Layer;
import photoshopclone.Controller.ToolController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LayersPanel extends JPanel {
    private Image imageModel;
    private ToolController toolController;
    private DefaultListModel<Layer> layerListModel;
    private JList<Layer> layerList;
    private UndoManager undoManager;

    public LayersPanel(Image imageModel, ToolController toolController, UndoManager undoManager) {
        this.imageModel = imageModel;
        this.toolController = toolController;
        this.undoManager = undoManager;
        setLayout(new BorderLayout());

        // Add a titled border with the word "Layers"
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Layers",
                TitledBorder.CENTER,
                TitledBorder.TOP
        ));

        layerListModel = new DefaultListModel<>();

        for (Layer layer : imageModel.getLayers()) {
            layerListModel.addElement(layer);
        }

        layerList = new JList<>(layerListModel);
        layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        layerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Layer selected = layerList.getSelectedValue();
                if (selected != null) {
                    toolController.setCurrentLayer(selected);
                    System.out.println("Layer selected: " + selected);
                    System.out.println("toolController = " + toolController.hashCode());
                    System.out.println("Current layer set to: " + selected.getName());
                }
            }
        });

        add(new JScrollPane(layerList), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 5, 5)); // Added padding between buttons

        JButton addLayerBtn = new JButton("Add");
        addLayerBtn.addActionListener(e -> addLayer());
        buttonsPanel.add(addLayerBtn);

        JButton deleteLayerBtn = new JButton("Delete");
        deleteLayerBtn.addActionListener(e -> deleteLayer());
        buttonsPanel.add(deleteLayerBtn);

        JButton renameLayerBtn = new JButton("Rename");
        renameLayerBtn.addActionListener(e -> renameLayer());
        buttonsPanel.add(renameLayerBtn);

        JButton toggleVisibleBtn = new JButton("Toggle Vis");
        toggleVisibleBtn.addActionListener(e -> toggleVisibility());
        buttonsPanel.add(toggleVisibleBtn);

        add(buttonsPanel, BorderLayout.SOUTH);

        // Select the topmost layer by default
        if (!layerListModel.isEmpty()) {
            layerList.setSelectedIndex(0);
        }
    }

    public void addLayer() {
        // Generate a unique layer name
        int count = layerListModel.getSize() + 1;
        String baseName = "Layer " + count;
        String uniqueName = baseName;
        boolean nameExists = true;
        while (nameExists) {
            nameExists = false;
            for (int i = 0; i < layerListModel.getSize(); i++) {
                if (layerListModel.get(i).getName().equalsIgnoreCase(uniqueName)) {
                    nameExists = true;
                    uniqueName = baseName + " (" + count + ")";
                    count++;
                    break;
                }
            }
        }

        // Create and add the new layer
        Layer newLayer = new Layer(toolController.getCurrentLayer().getImage().getWidth(),
                toolController.getCurrentLayer().getImage().getHeight());
        newLayer.setName(uniqueName);
        newLayer.setVisible(true);
        imageModel.addLayer(newLayer); // This is throwing an error
        layerListModel.addElement(newLayer);

        // Select the new layer
        layerList.setSelectedIndex(layerListModel.size() - 1);
        repaintCanvas();
    }


    private void deleteLayer() {
        Layer selected = layerList.getSelectedValue();
        if (selected != null && imageModel.getLayers().size() > 1) {
            imageModel.getLayers().remove(selected);
            layerListModel.removeElement(selected);
            if (!layerListModel.isEmpty()) {
                layerList.setSelectedIndex(0);
            }
            repaintCanvas();
        } else {
            JOptionPane.showMessageDialog(this, "Cannot delete the last layer.");
        }
    }

    private void renameLayer() {
        Layer selected = layerList.getSelectedValue();
        if (selected != null) {
            String newName = JOptionPane.showInputDialog(this, "Enter new layer name:", selected.getName());
            if (newName != null && !newName.trim().isEmpty()) {
                selected.setName(newName);
                layerList.repaint();
                repaintCanvas();
            }
        }
    }

    private void toggleVisibility() {
        Layer selected = layerList.getSelectedValue();
        if (selected != null) {
            selected.setVisible(!selected.isVisible());
            layerList.repaint();
            repaintCanvas();
        }
    }

    private void repaintCanvas() {
        // Repaint the entire window to reflect layer changes
        SwingUtilities.getWindowAncestor(this).repaint();
    }

    // Custom cell renderer to display visibility and layer name
    private class LayerCellRenderer extends JLabel implements ListCellRenderer<Layer> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Layer> list, Layer value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setText((value.isVisible() ? "[V] " : "[H] ") + value.getName());
            setOpaque(true);
            setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
            setForeground(Color.BLACK);
            return this;
        }
    }

    // Method to programmatically select a layer by name
    public void setSelectedLayerByName(String layerName) {
        for (int i = 0; i < layerListModel.getSize(); i++) {
            Layer layer = layerListModel.get(i);
            if (layer.getName().equalsIgnoreCase(layerName)) {
                layerList.setSelectedIndex(i);
                break;
            }
        }
    }

}
