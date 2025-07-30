package photoshopclone.Model;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Image implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient BufferedImage loadedImage; // Handle manually
    private List<Layer> layers;

    public Image() {
        layers = new LinkedList<>();
    }

    public void loadImage(File file) throws IOException, ImageReadException {
        loadedImage = Imaging.getBufferedImage(file);
        // Note: The caller manages layers
    }

    public BufferedImage getLoadedImage() {
        return loadedImage;
    }

    public BufferedImage getCombinedImage() {
        if (layers.isEmpty()) return null;

        // Start with the first visible normal layer as base
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
                    foundBase = true; // First visible normal layer is already the base
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

    public Image copy() {
        Image copy = new Image();
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

    // Method to set the state from another Image instance
    public void setState(Image other) {
        this.loadedImage = other.getLoadedImage() != null ? deepCopy(other.getLoadedImage()) : null;
        this.layers = new LinkedList<>();
        for (Layer layer : other.getLayers()) {
            this.layers.add(layer.copy());
        }
    }


    // Custom serialization for loadedImage
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        // Serialize loadedImage
        if (loadedImage != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(loadedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            oos.writeInt(imageBytes.length);
            oos.write(imageBytes);
        } else {
            oos.writeInt(0);
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        // Deserialize loadedImage
        int length = ois.readInt();
        if (length > 0) {
            byte[] imageBytes = new byte[length];
            ois.readFully(imageBytes);
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            loadedImage = ImageIO.read(bais);
        } else {
            loadedImage = null;
        }
    }
}