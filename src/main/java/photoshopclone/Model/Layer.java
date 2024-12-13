package photoshopclone.Model;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class Layer {
    private BufferedImage image;
    private float opacity;
    private String name;
    private boolean visible;

    public Layer(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        opacity = 1.0f;
        name = "New Layer";
        visible = true;
    }

    public Layer(int width, int height, int imageType) {
        image = new BufferedImage(width, height, imageType);
        opacity = 1.0f;
        name = "New Layer";
        visible = true;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Layer copy() {
        Layer copyLayer = new Layer(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = copyLayer.getImage().createGraphics();
        g.drawImage(this.image, 0, 0, null);
        g.dispose();
        copyLayer.setOpacity(this.opacity);
        copyLayer.setName(this.name + " Copy");
        copyLayer.setVisible(this.visible);
        return copyLayer;
    }

    @Override
    public String toString() {
        return (visible ? "[V] " : "[H] ") + name; // For JList display: [V] for visible, [H] for hidden
    }
}
