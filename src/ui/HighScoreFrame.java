package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
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

import model.DBComm;

public class HighScoreFrame extends JFrame {
	
	static Integer cachedSelectedRecordCount = null;
	static Integer cachedSelectedDifficulty = null;
	
	private final static Object[] COLUMN_HEADERS = {
		"Rank", "Name", "Score", "Lines", "Level", "Difficulty"
	};
	
	private final static Integer[] RECORD_COUNTS = {10,25,50,100};
	
	private JComboBox<Integer> jcbxNumRecords = new JComboBox<>(RECORD_COUNTS);
	
	private JComboBox<String> jcbxDiff = new JComboBox<>(new String[]{
			"Easy","Medium","Hard","All"
	});
	
	// Used when accessing this frame from save score frame to make new score row stand out
	private int highlightRow = -1;
	
	private JButton jbtReturn = new JButton("Return");
	private JTable table = new JTable();
	
	HighScoreFrame(int rowToHighlight) {
		
		this.highlightRow = rowToHighlight;
		
		// Set default selected options if they are cached
		if (cachedSelectedRecordCount != null) jcbxNumRecords.setSelectedIndex(cachedSelectedRecordCount);
		if (cachedSelectedDifficulty != null) jcbxDiff.setSelectedIndex(cachedSelectedDifficulty);
		
		table.setFillsViewportHeight(true);
		table.setEnabled(false);;
		
		setLayout(new BorderLayout());
		
		// Table added to center so it auto-expands with window size
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		// Holds record selector list and labels
		JPanel recordSelectorPanel = new JPanel();
		recordSelectorPanel.add(new JLabel("Show top "));
		recordSelectorPanel.add(jcbxNumRecords);
		recordSelectorPanel.add(new JLabel(" scores for difficulty "));
		recordSelectorPanel.add(jcbxDiff);
		
		// Container for button so it doesn't expand to full pane size
		JPanel buttonContainer = new JPanel();
		buttonContainer.add(jbtReturn);
		
		// Add all menu components to a master panel
		JPanel menuPanel = new JPanel(new GridLayout(2,1));
		menuPanel.add(recordSelectorPanel);
		menuPanel.add(buttonContainer);
		
		add(menuPanel, BorderLayout.SOUTH);
		
		jbtReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		// Both comboboxes simply re-populate the table upon selection change
		jcbxNumRecords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cachedSelectedRecordCount = jcbxNumRecords.getSelectedIndex();
				populateTable(false, highlightRow);				
			}
		});
		
		jcbxDiff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cachedSelectedDifficulty = jcbxDiff.getSelectedIndex();
				populateTable(false, highlightRow);				
			}
		});
		
		FrameUtils.setIcon(this, "trophy.png");
		setTitle("High Scores");
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		populateTable(true, highlightRow);

	}
	
	// Populates table with appropriate data depending on selected row count. Boolean
	// controls whether to destroy the window upon failed DB connection
	private void populateTable(boolean disposeOnFail, int rowToHighlight) {

		int numRecords = RECORD_COUNTS[jcbxNumRecords.getSelectedIndex()];
		int difficulty = jcbxDiff.getSelectedIndex();
		
		Object[][] data = null;
		try {
			data = DBComm.getHighScoresData(numRecords, difficulty);
		}
		catch (ClassNotFoundException | SQLException e1) {
			JOptionPane.showMessageDialog(null, "Database error: " + e1.getMessage());
			if (disposeOnFail) dispose();
			return;
		}
		
		table.setModel(new DefaultTableModel(data, COLUMN_HEADERS));
		
		TableCellRenderer renderer = new HighScoreCellRenderer(rowToHighlight);
		
		// Apply the renderer to all table columns
		for (int i = 0; i < data[0].length; i++)
			table.getColumnModel().getColumn(i).setCellRenderer(renderer);
		
	}
	
	// Returns a rendered component that represents a cell value in the data table
	private class HighScoreCellRenderer implements TableCellRenderer {
		
		private int rowToHighlight;
		
		private HighScoreCellRenderer(int rowToHighlight) {
			this.rowToHighlight = rowToHighlight;
		}
		
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			// A simple JLabel is used to render the cell data
			JLabel cell = new JLabel(value.toString());
			cell.setHorizontalAlignment(SwingConstants.CENTER);
			cell.setOpaque(true); // Allows background to show through
			cell.setEnabled(false);
			cell.setBackground(row == rowToHighlight ? Color.YELLOW : table.getBackground());
			
			return cell;
			
		}
		
	}
	
	
}
