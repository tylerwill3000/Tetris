package com.github.tylerwilliams.tetris.swing;

import javax.swing.*;
import java.awt.*;

class FlashableLabel extends JLabel {

    FlashableLabel(String text, int center) {
        super(text, center);
    }

    void flash(Color flashColor) {
        Color currentColor = getForeground();
        try {
            for (int i = 1; i <= 60; i++) {
                setForeground(i % 2 == 0 ? currentColor : flashColor);
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            setForeground(currentColor);
        }
    }

}
