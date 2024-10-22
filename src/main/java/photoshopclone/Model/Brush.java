package photoshopclone.Model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Brush {
    private int size;
    private Color color;

    public Brush(int size, Color color) {
        this.size = size;
        this.color = color;
    }

    public void draw(Layer layer, int x1, int y1, int x2, int y2) {
        Graphics2D g = layer.getImage().createGraphics();
        g.setColor(color);
        g.setStroke(new BasicStroke(size));
        g.drawLine(x1, y1, x2, y2);
        g.dispose();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
