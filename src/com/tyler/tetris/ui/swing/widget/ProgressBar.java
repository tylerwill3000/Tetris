package com.tyler.tetris.ui.swing.widget;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tyler.tetris.Block.ColoredSquare;
import com.tyler.tetris.ui.swing.PixelGrid;

/**
 * Displays a progress bar using JPanels
 * @author Tyler
 */
public class ProgressBar extends PixelGrid {
	
	public static final int PROGRESS_BAR_DIMENSION = 13;
	
	private Color barColor;
	private int percentagePerPanel;
	private double percentageComplete;
	
	public ProgressBar(int cols, Color barColor) {
		super(1, cols, PROGRESS_BAR_DIMENSION);
		this.barColor = barColor;
		this.percentagePerPanel = (int)(100.0 / cols);
		this.percentageComplete = 0;
	}
	
	public void setPercentageComplete(double complete) {
		this.percentageComplete = complete;
		repaint();
	}
	
	@Override
	public Collection<ColoredSquare> getCurrentColors() {
		
		// Fill in first panel if it's above zero but below percentage per panel value
		if (percentageComplete > 0 && percentageComplete < percentagePerPanel) {
			percentageComplete = percentagePerPanel;
		}
		
		List<ColoredSquare> squares = new ArrayList<>();
		for (int panel = 1; panel <= columns ; panel++) {
			Color squareColor = (panel * percentagePerPanel) <= percentageComplete ? barColor : null;
			ColoredSquare square = new ColoredSquare(squareColor, 0, panel - 1);
			squares.add(square);
		}
		
		return squares;
	}

}
