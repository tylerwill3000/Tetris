package com.github.tylersharpe.tetris.swing;

import javax.swing.*;

public final class SwingUtility {

    /**
     * @return JPanel with the specified component nested inside it
     */
    public static JPanel nestInPanel(JComponent... toNest) {
        JPanel container = new JPanel();
        for (JComponent comp : toNest) {
            container.add(comp);
        }
        return container;
    }

}
