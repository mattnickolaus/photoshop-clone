package Model;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class Layer {
    private BufferedImage image;
    private float opacity;

    public Layer(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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
}
