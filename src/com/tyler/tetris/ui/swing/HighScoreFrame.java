package com.tyler.tetris.ui.swing;

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

import com.tyler.tetris.Difficulty;
import com.tyler.tetris.Utility;
import com.tyler.tetris.score.HighScoreDao;
import com.tyler.tetris.ui.swing.widget.TetrisButton;

public class HighScoreFrame extends JFrame {
	
	private final static String[] COLUMN_HEADERS = { "Rank", "Name", "Score", "Lines", "Level", "Difficulty", "Game Time", "Date"};
	
	private JComboBox<Integer> lstRecords = new JComboBox<Integer>(new Integer[]{ 10, 25, 50, 100 });
	private JComboBox<String> lstDiff = new JComboBox<>(new String[]{ "Easy", "Medium", "Hard", "All" });
	private int highlightRank = -1;
	private TetrisButton btnClose = new TetrisButton("Close");
	private JTable tblScores = new JTable();
	private HighScoreDao scoresDao;
	
	public HighScoreFrame(HighScoreDao scoresDao) {
		this(scoresDao, -1);
	}
	
	public HighScoreFrame(HighScoreDao scoresDao, int highlightRank) {
		
		this.scoresDao = scoresDao;
		this.highlightRank = highlightRank;
		
		tblScores.setFillsViewportHeight(true);
		tblScores.setEnabled(false);;
		
		lstRecords.setSelectedItem(50);
		lstDiff.setSelectedItem("All");
		lstRecords.addActionListener(e -> refreshTable());
		lstDiff.addActionListener(e -> refreshTable());
		btnClose.addActionListener(e -> dispose());
		
		// Holds record selector list and labels
		JPanel recordSelectorPanel = new JPanel();
		recordSelectorPanel.add(new JLabel("Show top "));
		recordSelectorPanel.add(lstRecords);
		recordSelectorPanel.add(new JLabel(" scores for difficulty "));
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
		setTitle("High Scores");
		setLocationRelativeTo(null);
		setVisible(true);
		refreshTable();
	}
	
	// Populates table with appropriate data depending on selected row count. Boolean
	// controls whether to destroy the window upon failed DB connection
	private void refreshTable() {

		int numRecords = (int) lstRecords.getSelectedItem();
		int difficultyIndex = lstDiff.getSelectedIndex();
		Difficulty difficulty = difficultyIndex == 3 ? null : Difficulty.values()[difficultyIndex];
		
		Object[][] data = null;
		try {
			data = scoresDao.getHighScores(Optional.ofNullable(difficulty), Optional.of(numRecords))
			                .stream()
			                .map(s -> new Object[]{ s.rank, s.name, s.score, s.linesCleared, s.maxLevel,
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
