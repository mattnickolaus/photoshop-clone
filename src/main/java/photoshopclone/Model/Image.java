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
    private BufferedImage loadedImage;
    private List<Layer> layers;

    public Image() {
        layers = new LinkedList<>();
    }

    /**
     * Loads the image from a file and stores it as loadedImage.
     * Does not create any layers by default.
     * You can clear existing layers after loading if desired.
     */
    public void loadImage(File file) throws IOException, ImageReadException {
        loadedImage = Imaging.getBufferedImage(file);
        // Note: We do NOT create a base layer here.
        // The caller (e.g., MainFrame) can decide how to use loadedImage
        // and what layers to create.
    }

    /**
     * Returns the image that was last loaded.
     */
    public BufferedImage getLoadedImage() {
        return loadedImage;
    }

    /**
     * Combines all visible layers into a single image.
     * If no visible layers exist, returns null.
     */
    public BufferedImage getCombinedImage() {
        if (layers.isEmpty()) return null;

        // Start with the first visible non-adjustment layer as the base
        BufferedImage base = null;
        for (Layer layer : layers) {
            if (layer.isVisible() && !(layer instanceof AdjustmentLayer)) {
                base = deepCopy(layer.getImage());
                break;
            }
        }
        if (base == null) return null; // No visible normal layers

        // Draw all other visible normal layers on top
        Graphics2D g = base.createGraphics();
        boolean foundBase = false;
        for (Layer layer : layers) {
            if (layer.isVisible() && !(layer instanceof AdjustmentLayer)) {
                if (!foundBase) {
                    foundBase = true; // The first visible normal layer is already the base
                    continue;
                }
                g.drawImage(layer.getImage(), 0, 0, null);
            }
        }
        g.dispose();

        // Apply adjustment layers in order
        for (Layer layer : layers) {
            if (layer.isVisible() && layer instanceof AdjustmentLayer) {
                AdjustmentLayer adj = (AdjustmentLayer) layer;
                base = adj.applyAdjustments(base);
            }
        }

        return base;
    }

    public void addLayer(Layer layer) {
        layers.add(layer);
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void clearLayers() {
        layers.clear();
    }

    /**
     * Creates a deep copy of this Image object, including all layers.
     */
    public Image copy() {
        Image copy = new Image();
        // Copy loadedImage if needed
        if (loadedImage != null) {
            copy.loadedImage = deepCopy(loadedImage);
        }
        for (Layer layer : this.layers) {
            copy.addLayer(layer.copy());
        }
        return copy;
    }

    private BufferedImage deepCopy(BufferedImage bi) {
        if (bi == null) return null;
        BufferedImage copy = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();
        return copy;
    }
}
