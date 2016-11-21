package tetris.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import tetris.Difficulty;
import tetris.ScoreDao;
import tetris.TetrisGame;
import tetris.Utility;

public class LeaderboardFrame extends JFrame {
	
	private final static String[] COLUMN_HEADERS = { "Rank", "Name", "Score", "Lines", "Level", "Difficulty", "Game Time", "Date"};
	
	private JComboBox<String> lstDiff = new JComboBox<>(new String[]{ "Easy", "Medium", "Hard", "All" });
	private int highlightRank = -1;
	private TetrisButton btnClose = new TetrisButton("Close");
	private JTable tblScores = new JTable();
	private ScoreDao scoresDao;
	
	public LeaderboardFrame(ScoreDao scoresDao) {
		this(scoresDao, -1);
	}
	
	public LeaderboardFrame(ScoreDao scoresDao, int highlightRank) {
		
		this.scoresDao = scoresDao;
		this.highlightRank = highlightRank;
		
		tblScores.setFillsViewportHeight(true);
		tblScores.setEnabled(false);;
		
		lstDiff.setSelectedItem("All");
		lstDiff.addActionListener(e -> refreshTable());
		btnClose.addActionListener(e -> dispose());
		
		// Holds record selector list and labels
		JPanel recordSelectorPanel = new JPanel();
		recordSelectorPanel.add(new JLabel("Difficulty: "));
		recordSelectorPanel.add(lstDiff);
		
		// Add all menu components to a master panel
		JPanel menuPanel = new JPanel(new GridLayout(2,1));
		menuPanel.add(recordSelectorPanel);
		menuPanel.add(SwingUtility.nestInPanel(btnClose));
		
		setLayout(new BorderLayout());
		add(new JScrollPane(tblScores), BorderLayout.CENTER);
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

		int difficultyIndex = lstDiff.getSelectedIndex();
		Difficulty difficulty = difficultyIndex == 3 ? null : Difficulty.values()[difficultyIndex];
		
		Object[][] data = null;
		try {
			data = scoresDao.getScores(Optional.ofNullable(difficulty), Optional.empty())
			                .stream()
			                .map(s -> new Object[]{ s.rank, s.name, s.points, s.linesCleared, s.maxLevel == TetrisGame.MAX_LEVEL ? "Complete" : s.maxLevel,
			                                s.difficulty, Utility.formatSeconds(s.gameTime), s.dateAchieved })
			                .toArray(Object[][]::new);
		}
		catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "Error reading scores: " + e1.getMessage());
			return;
		}
		
		tblScores.setModel(new DefaultTableModel(data, COLUMN_HEADERS));
		TableCellRenderer renderer = new HighScoreCellRenderer(highlightRank);
		IntStream.range(0, tblScores.getColumnCount())
		         .mapToObj(tblScores.getColumnModel()::getColumn)
		         .forEach(c -> c.setCellRenderer(renderer));
	}
	
	private static class HighScoreCellRenderer implements TableCellRenderer {
		
		private int rankToHighlight;
		private int rowToHighlight = -1;
		
		private HighScoreCellRenderer(int rankToHighlight) {
			this.rankToHighlight = rankToHighlight;
		}
		
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
