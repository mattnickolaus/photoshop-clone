package photoshopclone.Model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

public class Layer implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient BufferedImage image; // Mark as transient to handle manually
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

    @Override
    public String toString() {
        return (visible ? "[V] " : "[H] ") + name;
    }

    public Layer copy() {
        Layer newLayer = new Layer(this.image.getWidth(), this.image.getHeight(), this.image.getType());
        newLayer.setName(this.name); // Retain the original name
        newLayer.setVisible(this.visible);
        newLayer.setImage(deepCopy(this.image));
        return newLayer;
    }

    // Deep copy method for BufferedImage
    private BufferedImage deepCopy(BufferedImage bi) {
        if (bi == null) return null;
        BufferedImage copy = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();
        return copy;
    }

    // Custom serialization for BufferedImage
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        // Serialize the BufferedImage as a byte array in PNG format
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        oos.writeInt(imageBytes.length);
        oos.write(imageBytes);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        // Deserialize the BufferedImage from the byte array
        int length = ois.readInt();
        if (length > 0) {
            byte[] imageBytes = new byte[length];
            ois.readFully(imageBytes);
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            image = ImageIO.read(bais);
        }
    }
}