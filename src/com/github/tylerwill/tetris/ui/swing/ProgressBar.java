package com.github.tylerwill.tetris.ui.swing;

import com.github.tylerwill.tetris.Block;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Displays a progress bar using JPanels
 * @author Tyler
 */
abstract class ProgressBar extends PixelGrid {
	
	private static final int PROGRESS_BAR_DIMENSION = 13;
	
	Color barColor;
	private double percentagePerPanel;
	
	ProgressBar(int cols, Color barColor) {
		super(1, cols, PROGRESS_BAR_DIMENSION);
		this.barColor = barColor;
		this.percentagePerPanel = 100.0 / cols;
	}
	
	@Override
	public Collection<Block.ColoredSquare> getCurrentColors() {
		
		double percentageComplete = getCurrentPercentage();
		
		// Fill in first panel if it's above zero but below percentage per panel value
		if (percentageComplete > 0 && percentageComplete < percentagePerPanel) {
			percentageComplete = percentagePerPanel;
		}
		
		List<Block.ColoredSquare> squares = new ArrayList<>();
		for (int panel = 1; panel <= columns ; panel++) {
			double panelPerc = Math.min(100.0, panel * percentagePerPanel);
			Color squareColor = panelPerc <= percentageComplete ? barColor : null;
			Block.ColoredSquare square = new Block.ColoredSquare(squareColor, 0, panel - 1);
			squares.add(square);
		}
		
		return squares;
	}

	abstract double getCurrentPercentage();
	
}
