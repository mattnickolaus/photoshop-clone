package photoshopclone.View;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.function.Supplier;

import photoshopclone.Model.AdjustmentLayer;
import photoshopclone.Model.Layer;

public class AdjustmentsPanel extends JPanel {
    private JSlider brightnessSlider;
    private JSlider contrastSlider;
    private JSlider saturationSlider;
    private JSlider vibranceSlider;
    private JSlider warmthSlider;
    private JSlider tintSlider;
    private JSlider sharpnessSlider;

    // We need a way to get the current layer. We can pass a supplier or callback.
    private Supplier<Layer> currentLayerSupplier;
    private Runnable repaintCallback;

    public AdjustmentsPanel(Supplier<Layer> currentLayerSupplier, Runnable repaintCallback) {
        this.currentLayerSupplier = currentLayerSupplier;
        this.repaintCallback = repaintCallback;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Adjustments",
                TitledBorder.CENTER,
                TitledBorder.TOP
        ));

        add(Box.createVerticalStrut(10)); // spacing at top

        brightnessSlider = createLabeledSlider("Brightness", -100, 100, 0);
        contrastSlider = createLabeledSlider("Contrast", -100, 100, 0);
        saturationSlider = createLabeledSlider("Saturation", -100, 100, 0);
        vibranceSlider = createLabeledSlider("Vibrance", -100, 100, 0);
        warmthSlider = createLabeledSlider("Warmth", -100, 100, 0);
        tintSlider = createLabeledSlider("Tint", -100, 100, 0);
        sharpnessSlider = createLabeledSlider("Sharpness", 0, 100, 50);

        addSliderListeners();

        add(Box.createVerticalGlue()); // push sliders up
    }

    private JSlider createLabeledSlider(String labelText, int min, int max, int value) {
        JPanel panel = new JPanel(new BorderLayout());
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

    private void addSliderListeners() {
        ChangeListener listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Layer current = currentLayerSupplier.get();
                if (current instanceof AdjustmentLayer) {
                    AdjustmentLayer adj = (AdjustmentLayer) currentLayerSupplier.get();
                    adj.setBrightness(brightnessSlider.getValue());
                    adj.setContrast(contrastSlider.getValue());
                    adj.setSaturation(saturationSlider.getValue());
                    adj.setVibrance(vibranceSlider.getValue());
                    adj.setWarmth(warmthSlider.getValue());
                    adj.setTint(tintSlider.getValue());
                    adj.setSharpness(sharpnessSlider.getValue());
                    repaintCallback.run();
                }
            }
        };

        brightnessSlider.addChangeListener(listener);
        contrastSlider.addChangeListener(listener);
        saturationSlider.addChangeListener(listener);
        vibranceSlider.addChangeListener(listener);
        warmthSlider.addChangeListener(listener);
        tintSlider.addChangeListener(listener);
        sharpnessSlider.addChangeListener(listener);
    }
}