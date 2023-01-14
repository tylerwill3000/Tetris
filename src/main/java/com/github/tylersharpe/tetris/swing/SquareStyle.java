package com.github.tylersharpe.tetris.swing;

import java.awt.*;

enum SquareStyle {

    DEFAULT {
        @Override
        public void paintSquare(Graphics g, Color color, int x, int y, int width, int height) {
            g.setColor(color);
            g.fill3DRect(x, y, width, height, true);
        }

        @Override
        public void paintGhost(Graphics g, int x, int y, int width, int height) {
            // Light gray for left and top border
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(x, y, x + width, y);
            g.drawLine(x, y, x, y + height);

            // White for bottom and right border
            g.setColor(Color.WHITE);
            g.drawLine(x + width, y, x + width, y + height);
            g.drawLine(x, y + height, x + width, y + height);
        }

    };

    abstract void paintSquare(Graphics g, Color color, int x, int y, int width, int height);

    abstract void paintGhost(Graphics g, int x, int y, int width, int height);

}
