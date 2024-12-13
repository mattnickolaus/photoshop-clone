package photoshopclone.Model;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class Layer {
    private BufferedImage image;
    private float opacity;

    public Layer(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        opacity = 1.0f;
    }

    public Layer(int width, int height, int imageType) {
        image = new BufferedImage(width, height, imageType);
        opacity = 1.0f;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float getOpacity() {
        return opacity;
    }

    public Layer copy() {
        Layer copyLayer = new Layer(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = copyLayer.getImage().createGraphics();
        g.drawImage(this.image, 0, 0, null);
        g.dispose();
        copyLayer.setOpacity(this.opacity);
        return copyLayer;
    }
}