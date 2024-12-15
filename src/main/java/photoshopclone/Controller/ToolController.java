package photoshopclone.Controller;

import photoshopclone.Model.Brush;
import photoshopclone.Model.Layer;
import photoshopclone.View.CanvasView;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ToolController {
    public enum ToolMode {
        BRUSH,
        PAN,
        // ZOOM could be handled separately via buttons
    }

    private Brush brush;
    private CanvasView canvasView;
    private Layer currentLayer;
    private ToolMode currentMode;

    // For pan mode: track the last mouse position
    private int lastX, lastY;

    public ToolController(CanvasView canvasView, Layer currentLayer) {
        this.canvasView = canvasView;
        this.currentLayer = currentLayer;
        this.brush = new Brush(10, Color.BLACK);
        this.currentMode = ToolMode.BRUSH; // default

        MouseAdapter mouseAdapter = new MouseAdapter() {
            boolean dragging = false;

            @Override
            public void mousePressed(MouseEvent e) {
                if (currentMode == ToolMode.BRUSH) {
                    int x = e.getX();
                    int y = e.getY();
                    brush.draw(currentLayer, screenToImageX(x), screenToImageY(y), screenToImageX(x), screenToImageY(y));
                    canvasView.repaint();
                    dragging = true;
                    lastX = x;
                    lastY = y;
                } else if (currentMode == ToolMode.PAN) {
                    // Record the starting point for panning
                    lastX = e.getX();
                    lastY = e.getY();
                    dragging = true;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentMode == ToolMode.BRUSH && dragging) {
                    int x = e.getX();
                    int y = e.getY();
                    brush.draw(currentLayer, screenToImageX(lastX), screenToImageY(lastY), screenToImageX(x), screenToImageY(y));
                    canvasView.repaint();
                    lastX = x;
                    lastY = y;
                } else if (currentMode == ToolMode.PAN && dragging) {
                    // Calculate delta
                    int x = e.getX();
                    int y = e.getY();
                    double dx = x - lastX;
                    double dy = y - lastY;
                    // Move the view
                    canvasView.pan(dx, dy);
                    lastX = x;
                    lastY = y;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
        };

        canvasView.addMouseListener(mouseAdapter);
        canvasView.addMouseMotionListener(mouseAdapter);
    }

    public void setBrushColor(Color color) {
        brush.setColor(color);
    }

    public void setToolMode(ToolMode mode) {
        this.currentMode = mode;
        System.out.println("Tool mode set to " + mode);
    }

    public void setCurrentLayer(Layer layer) {
        this.currentLayer = layer;
        System.out.println("Current layer set to: " + layer.getName());
    }

    // Convert screen coordinates to image coordinates (for brush)
    // Since we scale and pan, we need to invert that transform:
    private int screenToImageX(int x) {
        double scale = canvasView.getScaleFactor();
        double offsetX = canvasView.getOffsetX();
        return (int)((x - offsetX) / scale);
    }

    private int screenToImageY(int y) {
        double scale = canvasView.getScaleFactor();
        double offsetY = canvasView.getOffsetY();
        return (int)((y - offsetY) / scale);
    }
}
