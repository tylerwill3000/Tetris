package tetris.ui.swing;

import java.awt.Color;
import java.awt.Graphics;

public enum SquareStyle {
	
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
		
	}
	
;
	public abstract void paintSquare(Graphics g, Color color, int x, int y, int width, int height);
	
	public abstract void paintGhost(Graphics g, int x, int y, int width, int height);
	
}
