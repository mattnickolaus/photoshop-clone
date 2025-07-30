package photoshopclone.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class ColorPaletteView extends JPanel {
    private Consumer<Color> colorCallback;

    public ColorPaletteView(Consumer<Color> colorCallback) {
        this.colorCallback = colorCallback;
        JButton colorButton = new JButton("Select Color");
        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(null, "Choose a color", Color.BLACK);
                if (color != null && colorCallback != null) {
                    colorCallback.accept(color);
                }
            }
        });
        add(colorButton);
    }
}