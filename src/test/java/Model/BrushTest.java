package Model;

import org.junit.jupiter.api.*;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class BrushTest {

    private Brush brush;
    private Layer layer;

    @BeforeEach
    void setUp() {
        brush = new Brush(10, Color.BLUE);
        layer = new Layer(100, 100);
    }

    @Test
    void testBrushInitialization() {
        assertEquals(10, brush.getSize(), "Brush size should be 10.");
        assertEquals(Color.BLUE, brush.getColor(), "Brush color should be blue.");
    }

    @Test
    void testSetBrushSize() {
        brush.setSize(20);
        assertEquals(20, brush.getSize(), "Brush size should be updated to 20.");
    }

    @Test
    void testSetBrushColor() {
        brush.setColor(Color.GREEN);
        assertEquals(Color.GREEN, brush.getColor(), "Brush color should be updated to green.");
    }

    @Test
    void testBrushDrawing() {
        // Draw a line from (10,10) to (90,90)
        brush.draw(layer, 10, 10, 90, 90);

        // Check a pixel along the line
        int rgb = layer.getImage().getRGB(50, 50);
        Color color = new Color(rgb, true);
        assertEquals(Color.BLUE.getRed(), color.getRed(), "Red component should match.");
        assertEquals(Color.BLUE.getGreen(), color.getGreen(), "Green component should match.");
        assertEquals(Color.BLUE.getBlue(), color.getBlue(), "Blue component should match.");
    }
}
