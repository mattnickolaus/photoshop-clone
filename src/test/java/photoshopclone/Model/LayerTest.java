package photoshopclone.Model;

import org.junit.jupiter.api.*;
import photoshopclone.Model.Layer;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

public class LayerTest {

    private Layer layer;

    @BeforeEach
    void setUp() {
        layer = new Layer(200, 200);
    }

    @Test
    void testLayerInitialization() {
        BufferedImage image = layer.getImage();
        assertNotNull(image, "Layer image should not be null.");
        assertEquals(200, image.getWidth(), "Layer width should be 200.");
        assertEquals(200, image.getHeight(), "Layer height should be 200.");
    }

    @Test
    void testOpacity() {
        layer.setOpacity(0.5f);
        assertEquals(0.5f, layer.getOpacity(), "Layer opacity should be 0.5.");
    }

    @Test
    void testDrawingOnLayer() {
        Graphics2D g = layer.getImage().createGraphics();
        g.setColor(Color.RED);
        g.fillRect(50, 50, 100, 100);
        g.dispose();

        // Check pixel color at (100, 100)
        int rgb = layer.getImage().getRGB(100, 100);
        Color color = new Color(rgb, true);
        assertEquals(Color.RED.getRed(), color.getRed(), "Red component should match.");
        assertEquals(Color.RED.getGreen(), color.getGreen(), "Green component should match.");
        assertEquals(Color.RED.getBlue(), color.getBlue(), "Blue component should match.");
    }
}
