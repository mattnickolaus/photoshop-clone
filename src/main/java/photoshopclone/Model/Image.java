package photoshopclone.Model;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Image {
    private BufferedImage image;
    private List<Layer> layers;

    public Image() {
        layers = new LinkedList<>();
    }

    public void loadImage(File file) throws IOException, ImageReadException {
        image = Imaging.getBufferedImage(file);
    }

    public void saveImage(File file) throws IOException, ImageWriteException {
        BufferedImage combinedImage = getCombinedImage();
        Imaging.writeImage(combinedImage, file, ImageFormats.PNG, null);
    }

    public void addLayer(Layer layer) {
        layers.add(layer);
    }

    public BufferedImage getCombinedImage() {
        //Combine baseImage with layers
        BufferedImage combined = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = combined.createGraphics();
        g.drawImage(image, 0, 0, null);
        for (Layer layer : layers) {
            g.drawImage(layer.getImage(), 0, 0, null);
        }
        g.dispose();
        return combined;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }
}
