package ui.components;

import java.awt.Color;
import java.awt.Graphics;

import ui.GridPainter;

/**
 * Displays a progress bar using JPanels
 * @author Tyler
 */
public abstract class ProgressBar extends GridPainter {
	
	private Color _barColor;
	private int _percentagePerPanel;
	
	protected ProgressBar(int cols, Color barColor) {
		super(1, cols);
		_barColor = barColor;
		_percentagePerPanel = (int)(100.0 / cols);
	}
	
	public abstract double getCurrentValue();
	public abstract double getMaxValue();
	
	protected void paintComponent(Graphics g) {
		
		// Keep everything in terms of whole numbers - easier
		int percentageComplete = (int)(getCurrentValue() / getMaxValue() * 100);
		
		// Fill in first panel if it's above zero but below percentage per panel value
		if (percentageComplete > 0 && percentageComplete < _percentagePerPanel)
			percentageComplete = _percentagePerPanel;
		
		for (int panel = 1; panel <= _JPanelGrid[0].length ; panel++) {
			Color squareColor = panel * _percentagePerPanel <= percentageComplete ? _barColor : null;
			paintSquare(0, panel-1, squareColor);
		}
		
	}
	
}
