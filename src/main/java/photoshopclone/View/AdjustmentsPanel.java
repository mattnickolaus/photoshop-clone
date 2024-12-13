package photoshopclone.View;

import javax.swing.*;

public class AdjustmentsPanel extends JPanel {
    public AdjustmentsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("Adjustments Placeholder"));
        // Future: add JSliders for brightness/contrast etc.
    }
}
