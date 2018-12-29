package com.github.tylersharpe.tetris.swing;

import javax.swing.*;

public final class SwingUtility {

  private SwingUtility() {}

  /** Sets the icon image for a frame. The image must exist in the same directory* as the frame class' .class file */
  public static void setIcon(JFrame f, String image) {
    f.setIconImage(new ImageIcon(f.getClass().getResource(image)).getImage());
  }

  /** @return JPanel with the specified component nested inside it */
  public static JPanel nestInPanel(JComponent... toNest) {
    JPanel container = new JPanel();
    for (JComponent comp : toNest) {
      container.add(comp);
    }
    return container;
  }

}