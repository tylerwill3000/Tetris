package com.github.tylerwill.tetris.swing;

import com.github.tylerwill.tetris.Difficulty;
import com.github.tylerwill.tetris.score.ScoreDao;
import com.github.tylerwill.tetris.TetrisGame;
import com.github.tylerwill.tetris.Utility;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Optional;
import java.util.stream.IntStream;

class LeaderboardFrame extends JFrame {

  private final static String[] COLUMN_HEADERS = { "Rank", "Name", "Score", "Lines", "Level", "Difficulty", "Game Time", "Date"};

  private JComboBox<String> difficulties = new JComboBox<>(new String[]{ "Easy", "Medium", "Hard", "All" });
  private int highlightRank = -1;
  private TetrisButton clearButton = new TetrisButton("Clear Scores");
  private TetrisButton closeButton = new TetrisButton("Close");
  private JTable scoresTable = new JTable();
  private ScoreDao scoresDao;

  LeaderboardFrame(ScoreDao scoresDao) {
    this(scoresDao, -1);
  }

  LeaderboardFrame(ScoreDao scoresDao, int highlightRank) {

    this.scoresDao = scoresDao;
    this.highlightRank = highlightRank;

    scoresTable.setFillsViewportHeight(true);
    scoresTable.setEnabled(false);

    difficulties.setSelectedItem("All");
    difficulties.addActionListener(e -> refreshTable());

    clearButton.addActionListener(e -> {
      int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete all saved scores? This cannot be undone", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (result == JOptionPane.YES_OPTION) {
        try {
          scoresDao.clearAll();
          refreshTable();
        } catch (Exception e1) {
          JOptionPane.showMessageDialog(null, "Error clearing scores: " + e1.getMessage());
          return;
        }
      }
    });
    closeButton.addActionListener(e -> dispose());

    JPanel recordSelectorPanel = new JPanel();
    recordSelectorPanel.add(new JLabel("Difficulty: "));
    recordSelectorPanel.add(difficulties);

    JPanel menuPanel = new JPanel(new GridLayout(2, 1));
    menuPanel.add(recordSelectorPanel);
    menuPanel.add(SwingUtility.nestInPanel(clearButton, closeButton));

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

  // Populates table with appropriate data depending on selected row count. Boolean
  // controls whether to destroy the window upon failed DB connection
  private void refreshTable() {

    int difficultyIndex = difficulties.getSelectedIndex();
    Difficulty difficulty = difficultyIndex == 3 ? null : Difficulty.values()[difficultyIndex];

    Object[][] scoreData;
    try {
      scoreData = scoresDao.getScores(Optional.ofNullable(difficulty), Optional.empty())
                           .stream()
                           .map(score -> new Object[]{
                             score.rank,
                             score.name,
                             score.points,
                             score.linesCleared,
                             score.maxLevel == TetrisGame.MAX_LEVEL ? "Complete" : score.maxLevel,
                             score.difficulty,
                             Utility.formatSeconds(score.gameTime),
                             score.dateAchieved})
                           .toArray(Object[][]::new);
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null, "Error reading scores: " + ex.getMessage());
      return;
    }

    scoresTable.setModel(new DefaultTableModel(scoreData, COLUMN_HEADERS));

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
