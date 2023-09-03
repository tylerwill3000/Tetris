package com.github.tylerwilliams.tetris.swing;

import com.github.tylerwilliams.tetris.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class LeaderBoardFrame extends JFrame {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    private static final int LEADERBOARD_FRAME_WIDTH = 800;
    private static final int LEADERBOARD_FRAME_HEIGHT = 400;

    private record Column(String name, BiFunction<Score, Integer, String> renderer) {}

    private static final Column[] COLUMNS = {
        new Column("Rank",      (s, r) -> String.valueOf(r + 1)),
        new Column("Name",      (s, r) -> s.name()),
        new Column("Score",     (s, r) -> String.valueOf(s.points())),
        new Column("Lines",     (s, r) -> String.valueOf(s.linesCleared())),
        new Column("Level",     (s, r) -> s.completedGame() == null ? "N/A" : (s.completedGame() ? "Complete" : String.valueOf(s.maxLevel()))),
        new Column("Game Time", (s, r) -> Utility.formatSeconds(s.gameTime().getSeconds())),
        new Column("Date",      (s, r) -> DATE_FORMATTER.format(s.date()))
    };

    private final JComboBox<GameMode> gameModeComboBox = new JComboBox<>(GameMode.values());
    private final JComboBox<Difficulty> difficultyComboBox = new JComboBox<>(Difficulty.values());
    private final TableCellRenderer renderer = new HighScoreCellRenderer();
    private final Score scoreToHighlight;
    private final JTable scoresTable = new JTable();

    LeaderBoardFrame(Score scoreToHighlight) {
        this.scoreToHighlight = scoreToHighlight;

        scoresTable.setFillsViewportHeight(true);
        scoresTable.setEnabled(false);

        gameModeComboBox.setSelectedItem(scoreToHighlight != null ? scoreToHighlight.gameMode() : GameMode.CAMPAIGN);
        gameModeComboBox.addActionListener(e -> refreshTable());

        difficultyComboBox.setSelectedItem(scoreToHighlight != null ? scoreToHighlight.difficulty() : Difficulty.EASY);
        difficultyComboBox.addActionListener(e -> refreshTable());

        TetrisButton closeButton = new TetrisButton("Close");
        closeButton.setMnemonic('l');
        closeButton.addActionListener(e -> dispose());

        JPanel menuPanel = new JPanel();
        menuPanel.add(new JLabel("Game Mode:"));
        menuPanel.add(gameModeComboBox);
        menuPanel.add(new JLabel("            ")); // poor man's spacer (TODO clean this up when I get around to general UI polishing)
        menuPanel.add(new JLabel("Difficulty:"));
        menuPanel.add(difficultyComboBox);

        setLayout(new BorderLayout());
        add(new JScrollPane(scoresTable), BorderLayout.CENTER);
        add(menuPanel, BorderLayout.SOUTH);
        setIconImage(new ImageIcon(ImageFile.TROPHY_ICON.getUrl()).getImage());
        setSize(LEADERBOARD_FRAME_WIDTH, LEADERBOARD_FRAME_HEIGHT);
        setTitle("Leaderboard");
        setLocationRelativeTo(null);
        setVisible(true);
        refreshTable();
    }

    private void refreshTable() {
        Difficulty difficulty = (Difficulty) difficultyComboBox.getSelectedItem();
        GameMode gameMode = (GameMode) gameModeComboBox.getSelectedItem();

        List<Score> scores;
        try {
            scores = ScoreRepository.getScores(difficulty, gameMode);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Could not read high scores", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        Object[][] scoresTableData = scores.stream()
                .map(score -> Stream.of(COLUMNS).map(__ -> score).toArray(Object[]::new))
                .toArray(Object[][]::new);

        String[] columnHeaders = Stream.of(COLUMNS).map(Column::name).toArray(String[]::new);
        scoresTable.setModel(new DefaultTableModel(scoresTableData, columnHeaders));

        IntStream.range(0, scoresTable.getColumnCount())
                .mapToObj(scoresTable.getColumnModel()::getColumn)
                .forEach(column -> column.setCellRenderer(renderer));
    }

    private class HighScoreCellRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Score score = (Score) value;

            JLabel cell = new JLabel();
            cell.setHorizontalAlignment(SwingConstants.CENTER);
            cell.setOpaque(true); // Allows background to show through
            cell.setForeground(Color.BLACK);

            String text = COLUMNS[column].renderer().apply(score, row);
            cell.setText(text);

            cell.setBackground(score.equals(scoreToHighlight) ? Color.YELLOW : table.getBackground());

            return cell;
        }
    }
}
