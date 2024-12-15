package photoshopclone.View;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class AdjustmentsPanel extends JPanel {
    public AdjustmentsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Adjustments",
                TitledBorder.CENTER,
                TitledBorder.TOP
        ));
        add(new JLabel("Adjustments Placeholder"));
        // Future: add JSliders for brightness, contrast, etc.
    }
}
