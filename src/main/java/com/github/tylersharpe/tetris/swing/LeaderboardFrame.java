package com.github.tylersharpe.tetris.swing;

import com.github.tylersharpe.tetris.Difficulty;
import com.github.tylersharpe.tetris.TetrisGame;
import com.github.tylersharpe.tetris.Utility;
import com.github.tylersharpe.tetris.score.ScoreDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.stream.IntStream;

class LeaderboardFrame extends JFrame {

  private final static String[] COLUMN_HEADERS = { "Rank", "Name", "Score", "Lines", "Level", "Difficulty", "Game Time", "Date"};

  private static final String[] DIFFICULTY_OPTIONS = new String[Difficulty.values().length + 1];
  static {
    Difficulty[] diffs = Difficulty.values();
    for (int i = 0; i < diffs.length; i++) {
      DIFFICULTY_OPTIONS[i] = diffs[i].toString();
    }
    DIFFICULTY_OPTIONS[diffs.length] = "All";
  }

  private JComboBox<String> difficulties = new JComboBox<>(DIFFICULTY_OPTIONS);
  private int highlightRank = -1;
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

    TetrisButton clearButton = new TetrisButton("Clear Scores");
    clearButton.setMnemonic('c');
    clearButton.addActionListener(e -> {
      int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete all saved scores? This cannot be undone", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (result == JOptionPane.YES_OPTION) {
          try {
          scoresDao.clearAll();
          refreshTable();
        } catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(null, "Error clearing scores: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    TetrisButton closeButton = new TetrisButton("Close");
    closeButton.setMnemonic('l');
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

  // Populates table with appropriate data depending on selected row count
  private void refreshTable() {

    int selectedIndex = difficulties.getSelectedIndex();
    String selectedItem = (String) difficulties.getSelectedItem();
    Difficulty selectedDifficulty = "All".equals(selectedItem) ? null : Difficulty.values()[selectedIndex];

    Object[][] scoreData;
    try {
      scoreData = scoresDao.getScores(selectedDifficulty, null)
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
      JOptionPane.showMessageDialog(null, "Error reading scores: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      dispose();
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
