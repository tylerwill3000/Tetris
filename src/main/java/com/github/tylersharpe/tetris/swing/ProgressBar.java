package com.github.tylersharpe.tetris.swing;

import com.github.tylersharpe.tetris.ColoredSquare;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract class ProgressBar extends ColorGrid {

  private static final int PROGRESS_BAR_DIMENSION = 13;

  protected Color barColor;
  private final double percentagePerPanel;

  ProgressBar(int cols, Color barColor) {
    super(1, cols, PROGRESS_BAR_DIMENSION);
    this.barColor = barColor;
    this.percentagePerPanel = 100.0 / cols;
  }

  @Override
  public Collection<ColoredSquare> getCurrentColors() {
    double percentageComplete = getCurrentPercentage();

    // This is necessary so that at least 1 panel gets filled in if we are > 0 percentage complete but not within percentage per panel window
    if (percentageComplete > 0 && percentageComplete < percentagePerPanel) {
      percentageComplete = percentagePerPanel;
    }

    List<ColoredSquare> squares = new ArrayList<>();
    for (int panel = 1; panel <= getColumns() ; panel++) {
      double panelPercentage = Math.min(100.0, panel * percentagePerPanel);
      Color squareColor = panelPercentage <= percentageComplete ? barColor : null;
      squares.add(new ColoredSquare(squareColor, 0, panel - 1));
    }

    return squares;
  }

  abstract double getCurrentPercentage();

}
