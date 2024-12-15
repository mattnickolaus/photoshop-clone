package photoshopclone.View;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class AdjustmentsPanel extends JPanel {
    private JSlider brightnessSlider;
    private JSlider contrastSlider;
    private JSlider saturationSlider;
    private JSlider vibranceSlider;
    private JSlider warmthSlider;
    private JSlider tintSlider;
    private JSlider sharpnessSlider;

    public AdjustmentsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Adjustments",
                TitledBorder.CENTER,
                TitledBorder.TOP
        ));

        add(Box.createVerticalStrut(10)); // spacing at top

        // Create and add each slider with a label
        brightnessSlider = createLabeledSlider("Brightness", -100, 100, 0);
        contrastSlider = createLabeledSlider("Contrast", -100, 100, 0);
        saturationSlider = createLabeledSlider("Saturation", -100, 100, 0);
        vibranceSlider = createLabeledSlider("Vibrance", -100, 100, 0);
        warmthSlider = createLabeledSlider("Warmth", -100, 100, 0);
        tintSlider = createLabeledSlider("Tint", -100, 100, 0);
        sharpnessSlider = createLabeledSlider("Sharpness", 0, 100, 50);

        add(Box.createVerticalGlue()); // push sliders up
    }

    /**
     * Helper method to create a label and a slider pair and add them to the panel.
     */
    private JSlider createLabeledSlider(String labelText, int min, int max, int value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        JLabel label = new JLabel(labelText + ":");
        panel.add(label, BorderLayout.WEST);

        JSlider slider = new JSlider(min, max, value);
        slider.setPaintTicks(true);
        slider.setPaintLabels(false);
        slider.setMajorTickSpacing((max - min) / 4);
        slider.setPreferredSize(new Dimension(150, 40));
        panel.add(slider, BorderLayout.CENTER);

        add(panel);
        return slider;
    }
}