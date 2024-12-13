package photoshopclone.View;

import javax.swing.*;
import java.util.List;
import photoshopclone.Model.Layer;

public class LayerListView extends JPanel {
    private JList<Layer> layerList;
    private DefaultListModel<Layer> listModel;

    public LayerListView(List<Layer> layers) {
        listModel = new DefaultListModel<>();
        for (Layer layer : layers) {
            listModel.addElement(layer);
        }
        layerList = new JList<>(listModel);
        add(new JScrollPane(layerList));
    }

    // Methods to add/remove layers...
}