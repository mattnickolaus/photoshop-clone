package photoshopclone.Model;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Image {
    private List<Layer> layers;

    public Image() {
        layers = new LinkedList<>();
    }

    public void loadImage(File file) throws IOException, ImageReadException {
        BufferedImage loaded = Imaging.getBufferedImage(file);
        // Create a base layer from loaded image
        Layer baseLayer = new Layer(loaded.getWidth(), loaded.getHeight());
        Graphics2D g = baseLayer.getImage().createGraphics();
        g.drawImage(loaded, 0, 0, null);
        g.dispose();
        layers.clear(); // Clear any existing layers
        layers.add(baseLayer);
    }

    public BufferedImage getCombinedImage() {
        if (layers.isEmpty()) return null;

        BufferedImage combined = deepCopy(layers.get(0).getImage());
        Graphics2D g = combined.createGraphics();
        for (int i = 1; i < layers.size(); i++) {
            g.drawImage(layers.get(i).getImage(), 0, 0, null);
        }
        g.dispose();
        return combined;
    }

    public void addLayer(Layer layer) {
        layers.add(layer);
    }

    public List<Layer> getLayers() {
        return layers;
    }

    /**
     * Creates a deep copy of the current Image, including all layers.
     */
    public Image copy() {
        Image copy = new Image();
        for (Layer layer : this.layers) {
            copy.addLayer(layer.copy());
        }
        return copy;
    }

    private BufferedImage deepCopy(BufferedImage bi) {
        BufferedImage copy = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();
        return copy;
    }
}