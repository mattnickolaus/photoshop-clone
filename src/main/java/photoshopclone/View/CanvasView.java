package photoshopclone.View;

import photoshopclone.Model.Image;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CanvasView extends JPanel {
    private Image imageModel;
    private double scaleFactor = 1.0;
    private double offsetX = 0.0;
    private double offsetY = 0.0;

    public CanvasView(Image imageModel) {
        this.imageModel = imageModel;
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage displayImage = imageModel.getCombinedImage();
        if (displayImage == null) return;

        Graphics2D g2d = (Graphics2D) g.create();
        // Apply scale and translation
        g2d.translate(offsetX, offsetY);
        g2d.scale(scaleFactor, scaleFactor);
        g2d.drawImage(displayImage, 0, 0, this);
        g2d.dispose();
    }

    public void zoomIn() {
        scaleFactor *= 1.1;
        revalidate();
        repaint();
    }

    public void zoomOut() {
        scaleFactor /= 1.1;
        revalidate();
        repaint();
    }

    public void resetView() {
        scaleFactor = 1.0;
        offsetX = 0.0;
        offsetY = 0.0;
        revalidate();
        repaint();
    }

    public void pan(double dx, double dy) {
        offsetX += dx;
        offsetY += dy;
        repaint();
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }
}