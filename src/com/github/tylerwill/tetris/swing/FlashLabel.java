package com.github.tylerwill.tetris.swing;

import javax.swing.*;
import java.awt.*;

class FlashLabel extends JLabel {

  FlashLabel(String text, int center) {
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
