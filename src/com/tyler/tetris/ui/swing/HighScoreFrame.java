package com.tyler.tetris.ui.swing;

import javax.swing.JFrame;

public class HighScoreFrame extends JFrame {
	/*
	private final static Object[] COLUMN_HEADERS = {"Rank", "Name", "Score", "Lines", "Level", "Difficulty", "Game Time"};
	
	private final static Integer[] RECORD_COUNTS = {10,25,50,100};
	
	private JComboBox<Integer> _jcbxRecords = new JComboBox<>(RECORD_COUNTS);
	
	private JComboBox<String> _jcbxDiff = new JComboBox<>(new String[]{"Easy","Medium","Hard","All"});
	
	// Used when accessing this frame from save score frame to make new score row stand out
	private int _highlightRank = -1;
	
	private CloseFrameButton _jbtClose = new CloseFrameButton(this);
	private JTable _table = new JTable();
	
	public HighScoreFrame(int rankToHighlight) {
		
		this._highlightRank = rankToHighlight;
		
		// Set default selected options if they are cached
		_jcbxRecords.setSelectedIndex(TetrisProperties.getHighScoreRecordCount());
		_jcbxDiff.setSelectedIndex(TetrisProperties.getHighScoreDifficulty());
		
		_table.setFillsViewportHeight(true);
		_table.setEnabled(false);;
		
		setLayout(new BorderLayout());
		
		// Table added to center so it auto-expands with window size
		add(new JScrollPane(_table), BorderLayout.CENTER);
		
		// Holds record selector list and labels
		JPanel recordSelectorPanel = new JPanel();
		recordSelectorPanel.add(new JLabel("Show top "));
		recordSelectorPanel.add(_jcbxRecords);
		recordSelectorPanel.add(new JLabel(" scores for difficulty "));
		recordSelectorPanel.add(_jcbxDiff);
		
		// Add all menu components to a master panel
		JPanel menuPanel = new JPanel(new GridLayout(2,1));
		menuPanel.add(recordSelectorPanel);
		menuPanel.add(FrameUtils.nestInPanel(_jbtClose));
		
		add(menuPanel, BorderLayout.SOUTH);
		
		// Both comboboxes simply re-populate the table upon selection change
		_jcbxRecords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TetrisProperties.setHighScoreRecordCount(_jcbxRecords.getSelectedIndex());
				populateTable(false, _highlightRank);				
			}
		});
		
		_jcbxDiff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TetrisProperties.setHighScoresDifficulty(_jcbxDiff.getSelectedIndex());
				populateTable(false, _highlightRank);				
			}
		});
		
		FrameUtils.setIcon(this, "trophy.png");
		setTitle("High Scores");
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		populateTable(true, _highlightRank);

	}
	
	// Populates table with appropriate data depending on selected row count. Boolean
	// controls whether to destroy the window upon failed DB connection
	private void populateTable(boolean disposeOnFail, int rowToHighlight) {

		int numRecords = RECORD_COUNTS[_jcbxRecords.getSelectedIndex()];
		int difficulty = _jcbxDiff.getSelectedIndex();
		
		Object[][] data = null;
		try {
			data = TetrisDB.getHighScores(numRecords, difficulty);
		}
		catch (ClassNotFoundException | SQLException e1) {
			JOptionPane.showMessageDialog(null, "Database error: " + e1.getMessage());
			if (disposeOnFail) dispose();
			return;
		}
		
		_table.setModel(new DefaultTableModel(data, COLUMN_HEADERS));
		
		TableCellRenderer renderer = new HighScoreCellRenderer(rowToHighlight);
		
		// Apply the renderer to all table columns
		for (int i = 0; i < data[0].length; i++)
			_table.getColumnModel().getColumn(i).setCellRenderer(renderer);
		
	}
	
	// Returns a rendered component that represents a cell value in the data table
	private static class HighScoreCellRenderer implements TableCellRenderer {
		
		private int rankToHighlight;
		
		// This is not set until the rank row is detected, since the rank and the
		// row it appears on are not always the same
		private int rowToHighlight = -1;
		
		private HighScoreCellRenderer(int rankToHighlight) {
			this.rankToHighlight = rankToHighlight;
		}
		
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			// A simple JLabel is used to render the cell data
			JLabel cell = new JLabel(value.toString());
			cell.setHorizontalAlignment(SwingConstants.CENTER);
			cell.setOpaque(true); // Allows background to show through
			cell.setEnabled(false);
			
			// If the cell contains the rank to highlight, the row to highlight has been
			// found, so the value can now be set
			if (value.toString().equals(String.valueOf(rankToHighlight)))
				rowToHighlight = row;
			
			cell.setBackground(row == rowToHighlight ? Color.YELLOW : table.getBackground());
			
			return cell;
			
		}
		
	}
*/	
	
}