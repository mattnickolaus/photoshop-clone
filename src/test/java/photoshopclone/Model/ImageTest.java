
package photoshopclone.Model;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.junit.jupiter.api.*;
import photoshopclone.Model.Image;
import photoshopclone.Model.Layer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ImageTest {

    private Image imageModel;

    @BeforeEach
    void setUp() {
        imageModel = new Image();
    }

    @Test
    void testLoadImage() throws IOException, ImageReadException {
        File testImageFile = new File("src/test/resources/testImage.png");
        assertTrue(testImageFile.exists(), "Test image file should exist.");

        imageModel.loadImage(testImageFile);
        assertNotNull(imageModel.getImage(), "Image should be loaded.");
    }

    @Test
    void testAddLayer() {
        Layer layer = new Layer(100, 100);
        imageModel.addLayer(layer);
        assertEquals(1, imageModel.getLayers().size(), "Layer list should contain one layer.");
        assertEquals(layer, imageModel.getLayers().get(0), "The added layer should be in the list.");
    }

    @Test
    void testGetCombinedImage() throws IOException, ImageReadException {
        // Load base image
        File testImageFile = new File("src/test/resources/testImage.png");
        imageModel.loadImage(testImageFile);

        // Add a layer
        Layer layer = new Layer(imageModel.getImage().getWidth(), imageModel.getImage().getHeight());
        imageModel.addLayer(layer);

        BufferedImage combinedImage = imageModel.getCombinedImage();
        assertNotNull(combinedImage, "Combined image should not be null.");
        assertEquals(imageModel.getImage().getWidth(), combinedImage.getWidth(), "Width should match.");
        assertEquals(imageModel.getImage().getHeight(), combinedImage.getHeight(), "Height should match.");
    }

    @Test
    void testSaveImage() throws IOException, ImageReadException, ImageWriteException {
        // Load base image
        File testImageFile = new File("src/test/resources/testImage.png");
        imageModel.loadImage(testImageFile);

        // Save the image
        File outputFile = new File("src/test/resources/outputImage.png");
        imageModel.saveImage(outputFile);

        assertTrue(outputFile.exists(), "Output file should exist after saving.");

        // Clean up
        assertTrue(outputFile.delete(), "Output file should be deleted after test.");
    }
}



