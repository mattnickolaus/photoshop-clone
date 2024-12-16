package photoshopclone.Model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class AdjustmentLayer extends Layer implements Serializable {
    private static final long serialVersionUID = 1L;
    // Adjustment parameters
    private double brightness;  // -100 to 100
    private double contrast;    // -100 to 100
    private double saturation;  // -100 to 100
    private double vibrance;    // -100 to 100
    private double warmth;      // -100 to 100
    private double tint;        // -100 to 100
    private double sharpness;   // 0 to 100

    public AdjustmentLayer(int width, int height) {
        super(width, height);
        brightness = 0;
        contrast = 0;
        saturation = 0;
        vibrance = 0;
        warmth = 0;
        tint = 0;
        sharpness = 50; // Default sharpness
        setName("Adjustment Layer");
        setVisible(true);
    }

    // Getters and setters...

    public double getBrightness() { return brightness; }
    public void setBrightness(double brightness) { this.brightness = brightness; }

    public double getContrast() { return contrast; }
    public void setContrast(double contrast) { this.contrast = contrast; }

    public double getSaturation() { return saturation; }
    public void setSaturation(double saturation) { this.saturation = saturation; }

    public double getVibrance() { return vibrance; }
    public void setVibrance(double vibrance) { this.vibrance = vibrance; }

    public double getWarmth() { return warmth; }
    public void setWarmth(double warmth) { this.warmth = warmth; }

    public double getTint() { return tint; }
    public void setTint(double tint) { this.tint = tint; }

    public double getSharpness() { return sharpness; }
    public void setSharpness(double sharpness) { this.sharpness = sharpness; }

    @Override
    public Layer copy() {
        AdjustmentLayer copy = new AdjustmentLayer(getImage().getWidth(), getImage().getHeight());
        copy.setBrightness(this.brightness);
        copy.setContrast(this.contrast);
        copy.setSaturation(this.saturation);
        copy.setVibrance(this.vibrance);
        copy.setWarmth(this.warmth);
        copy.setTint(this.tint);
        copy.setSharpness(this.sharpness);
        copy.setOpacity(getOpacity());
        copy.setName(getName());
        copy.setVisible(isVisible());
        return copy;
    }

    /**
     * Apply adjustments to the given image and return the result.
     */
    public java.awt.image.BufferedImage applyAdjustments(java.awt.image.BufferedImage src) {
        java.awt.image.BufferedImage result = src;

        // Apply brightness/contrast using Filter
        if (brightness != 0 || contrast != 0) {
            // Convert contrast (-100 to 100) to alpha (1.0 ± something)
            double alpha = 1.0 + (contrast / 100.0);
            // Brightness (-100 to 100) to beta (range -100 to 100)
            int beta = (int)(brightness);
            result = Filter.applyBrightnessContrast(result, alpha, beta);
        }

        // Apply saturation
        if (saturation != 0) {
            result = Filter.applySaturation(result, saturation);
        }

        // Apply vibrance
        if (vibrance != 0) {
            result = Filter.applyVibrance(result, vibrance);
        }

        // Apply warmth/tint
        if (warmth != 0 || tint != 0) {
            result = Filter.applyWarmthTint(result, warmth, tint);
        }

        // Apply sharpness
        if (sharpness != 50) { // 50 = no change, higher = sharper, lower = blur maybe
            double amount = (sharpness - 50) / 50.0; // from -1.0 to +1.0
            result = Filter.applySharpness(result, amount);
        }

        return result;
    }
}