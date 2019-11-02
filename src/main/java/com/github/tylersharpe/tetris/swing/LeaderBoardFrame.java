package com.github.tylersharpe.tetris.swing;

import com.github.tylersharpe.tetris.Difficulty;
import com.github.tylersharpe.tetris.Score;
import com.github.tylersharpe.tetris.ScoreRepository;
import com.github.tylersharpe.tetris.Utility;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class LeaderBoardFrame extends JFrame {

  private final static String[] COLUMN_HEADERS = { "Rank", "Name", "Score", "Lines", "Level", "Difficulty", "Game Time", "Date"};

  private static final String[] DIFFICULTY_OPTIONS = Stream.concat(
    Stream.of(Difficulty.values()).map(Difficulty::getDisplay),
    Stream.of("All")
  ).toArray(String[]::new);

  private JComboBox<String> difficulties = new JComboBox<>(DIFFICULTY_OPTIONS);
  private int highlightRank;
  private JTable scoresTable = new JTable();
  private ScoreRepository scoreRepository;

  LeaderBoardFrame(ScoreRepository scoreRepository) {
    this(scoreRepository, -1);
  }

  LeaderBoardFrame(ScoreRepository scoreRepository, int highlightRank) {
    this.scoreRepository = scoreRepository;
    this.highlightRank = highlightRank;

    scoresTable.setFillsViewportHeight(true);
    scoresTable.setEnabled(false);

    difficulties.setSelectedItem("All");
    difficulties.addActionListener(e -> refreshTable());

    TetrisButton closeButton = new TetrisButton("Close");
    closeButton.setMnemonic('l');
    closeButton.addActionListener(e -> dispose());

    JPanel recordSelectorPanel = new JPanel();
    recordSelectorPanel.add(new JLabel("Difficulty: "));
    recordSelectorPanel.add(difficulties);

    JPanel menuPanel = new JPanel(new GridLayout(2, 1));
    menuPanel.add(recordSelectorPanel);

    setLayout(new BorderLayout());
    add(new JScrollPane(scoresTable), BorderLayout.CENTER);
    add(menuPanel, BorderLayout.SOUTH);
    SwingUtility.setIcon(this, "/images/trophy.png");
    setSize(600, 400);
    setTitle("Leaderboard");
    setLocationRelativeTo(null);
    setVisible(true);
    refreshTable();
  }

  // Populates table with appropriate data depending on selected row count
  private void refreshTable() {
    String selectedDifficultyDisplay = (String) difficulties.getSelectedItem();
    Difficulty selectedDifficulty = "All".equals(selectedDifficultyDisplay) ? null : Difficulty.fromDisplay(selectedDifficultyDisplay);

    List<Score> scores;
    try {
      scores = scoreRepository.getScores(selectedDifficulty, null);
    } catch (IOException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Could not read high scores", "Error", JOptionPane.ERROR_MESSAGE);
      dispose();
      return;
    }

    Object[][] formattedScoreData = scores.stream()
            .map(score -> new Object[]{
              score.rank,
              score.name,
              score.points,
              score.linesCleared,
              score.completedGame() ? "Complete" : score.maxLevel,
              score.difficulty,
              Utility.formatSeconds(score.gameTime),
              score.dateAchieved
            })
            .toArray(Object[][]::new);

    scoresTable.setModel(new DefaultTableModel(formattedScoreData, COLUMN_HEADERS));

    TableCellRenderer renderer = new HighScoreCellRenderer(highlightRank);
    IntStream.range(0, scoresTable.getColumnCount())
             .mapToObj(scoresTable.getColumnModel()::getColumn)
             .forEach(column -> column.setCellRenderer(renderer));
  }

  private static class HighScoreCellRenderer implements TableCellRenderer {
    private int rankToHighlight;
    private int rowToHighlight = -1;

    private HighScoreCellRenderer(int rankToHighlight) {
      this.rankToHighlight = rankToHighlight;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

      if (column == 0 && Integer.parseInt(value.toString()) == rankToHighlight) {
        rowToHighlight = row;
      }

      JLabel cell = new JLabel(value.toString());
      cell.setHorizontalAlignment(SwingConstants.CENTER);
      cell.setOpaque(true); // Allows background to show through
      cell.setForeground(Color.BLACK);
      cell.setBackground(row == rowToHighlight ? Color.YELLOW : table.getBackground());
      return cell;
    }

  }

}
