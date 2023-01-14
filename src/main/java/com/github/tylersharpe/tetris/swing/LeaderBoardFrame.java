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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class LeaderBoardFrame extends JFrame {

    private final static String[] COLUMN_HEADERS = {"Rank", "Name", "Score", "Lines", "Level", "Difficulty", "Game Time", "Date"};

    private static final String[] DIFFICULTY_OPTIONS = Stream.concat(
            Stream.of(Difficulty.values()).map(Difficulty::getDisplay),
            Stream.of("All")
    ).toArray(String[]::new);

    private static final int LEADERBOARD_FRAME_WIDTH = 600;
    private static final int LEADERBOARD_FRAME_HEIGHT = 400;

    private final JComboBox<String> difficultyComboBox = new JComboBox<>(DIFFICULTY_OPTIONS);
    private final int highlightRank;
    private final JTable scoresTable = new JTable();
    private final ScoreRepository scoreRepository;

    LeaderBoardFrame(ScoreRepository scoreRepository) {
        this(scoreRepository, -1);
    }

    LeaderBoardFrame(ScoreRepository scoreRepository, int highlightRank) {
        this.scoreRepository = scoreRepository;
        this.highlightRank = highlightRank;

        scoresTable.setFillsViewportHeight(true);
        scoresTable.setEnabled(false);

        difficultyComboBox.setSelectedItem("All");
        difficultyComboBox.addActionListener(e -> refreshTable());

        TetrisButton closeButton = new TetrisButton("Close");
        closeButton.setMnemonic('l');
        closeButton.addActionListener(e -> dispose());

        JPanel difficultySelectorPanel = new JPanel();
        difficultySelectorPanel.add(new JLabel("Difficulty: "));
        difficultySelectorPanel.add(difficultyComboBox);

        JPanel menuPanel = new JPanel(new GridLayout(2, 1));
        menuPanel.add(difficultySelectorPanel);

        setLayout(new BorderLayout());
        add(new JScrollPane(scoresTable), BorderLayout.CENTER);
        add(menuPanel, BorderLayout.SOUTH);
        SwingUtility.setIcon(this, "/images/trophy.png");
        setSize(LEADERBOARD_FRAME_WIDTH, LEADERBOARD_FRAME_HEIGHT);
        setTitle("Leaderboard");
        setLocationRelativeTo(null);
        setVisible(true);
        refreshTable();
    }

    // Populates table with appropriate data depending on selected row count
    private void refreshTable() {
        String selectedDifficultyDisplay = (String) difficultyComboBox.getSelectedItem();
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

        AtomicInteger rank = new AtomicInteger(0);
        Object[][] formattedScoreData = scores.stream()
                .map(score -> new Object[]{
                        rank.incrementAndGet(),
                        score.name(),
                        score.points(),
                        score.linesCleared(),
                        score.completedGame() ? "Complete" : score.maxLevel(),
                        score.difficulty(),
                        Utility.formatSeconds(score.gameTime().getSeconds()),
                        score.date()
                })
                .toArray(Object[][]::new);

        scoresTable.setModel(new DefaultTableModel(formattedScoreData, COLUMN_HEADERS));

        TableCellRenderer renderer = new HighScoreCellRenderer(highlightRank);
        IntStream.range(0, scoresTable.getColumnCount())
                .mapToObj(scoresTable.getColumnModel()::getColumn)
                .forEach(column -> column.setCellRenderer(renderer));
    }

    private static class HighScoreCellRenderer implements TableCellRenderer {
        private final int rankToHighlight;
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
