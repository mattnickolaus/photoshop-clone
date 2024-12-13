package photoshopclone.View;

import photoshopclone.Model.Image;
import photoshopclone.Model.Layer;
import photoshopclone.Controller.ToolController;

import javax.swing.*;
import java.awt.*;

public class LayersPanel extends JPanel {
    private Image imageModel;
    private ToolController toolController;
    private DefaultListModel<Layer> layerListModel;
    private JList<Layer> layerList;

    public LayersPanel(Image imageModel, ToolController toolController) {
        this.imageModel = imageModel;
        this.toolController = toolController;
        setLayout(new BorderLayout());

        layerListModel = new DefaultListModel<>();
        for (Layer layer : imageModel.getLayers()) {
            layerListModel.addElement(layer);
        }

        layerList = new JList<>(layerListModel);
        layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        layerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Layer selected = layerList.getSelectedValue();
                if (selected != null && toolController != null) {
                    toolController.setCurrentLayer(selected);
                }
            }
        });

        add(new JScrollPane(layerList), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2));

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
    }

    private void addLayer() {
        if (imageModel.getLayers().isEmpty()) return;
        Layer base = imageModel.getLayers().get(0);
        Layer newLayer = new Layer(base.getImage().getWidth(), base.getImage().getHeight());
        newLayer.setName("Layer " + (layerListModel.size() + 1));
        imageModel.addLayer(newLayer);
        layerListModel.addElement(newLayer);
        layerList.setSelectedValue(newLayer, true);
        repaintCanvas();
    }

    private void deleteLayer() {
        Layer selected = layerList.getSelectedValue();
        if (selected != null && imageModel.getLayers().size() > 1) {
            imageModel.getLayers().remove(selected);
            layerListModel.removeElement(selected);
            if (!imageModel.getLayers().isEmpty()) {
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
        SwingUtilities.getWindowAncestor(this).repaint();
    }
}
