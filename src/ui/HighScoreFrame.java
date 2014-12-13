package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import model.DBComm;

public class HighScoreFrame extends JFrame {
	
	private static Integer cachedSelectedRecordCount = null;
	private static Integer cachedSelectedDifficulty = null;
	
	private final static Object[] COLUMN_HEADERS = {
		"Name", "Score", "Lines", "Level", "Difficulty"
	};
	
	private final static Integer[] RECORD_COUNTS = {10,25,50,100};
	
	private JComboBox<Integer> jcbxNumRecords = new JComboBox<>(RECORD_COUNTS);
	
	private JComboBox<String> jcbxDiff = new JComboBox<>(new String[]{
			"Easy","Medium","Hard","All"
	});
	
	private JButton jbtReturn = new JButton("Return");
	private JTable table = new JTable();
	
	private JLabel dbErrors = new JLabel();
	
	HighScoreFrame() {
		
		// Set default selected options if they are cached
		if (cachedSelectedRecordCount != null) jcbxNumRecords.setSelectedIndex(cachedSelectedRecordCount);
		if (cachedSelectedDifficulty != null) jcbxDiff.setSelectedIndex(cachedSelectedDifficulty);
		
		dbErrors.setHorizontalAlignment(SwingConstants.CENTER);
		dbErrors.setForeground(Color.RED);
		
		populateTable();
		
		table.setFillsViewportHeight(true);
		table.setEnabled(false);;
		
		setLayout(new BorderLayout());
		
		// Table added to center so it auto-expands with window size
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		// Holds record selector list and labels
		JPanel recordSelectorPanel = new JPanel();
		recordSelectorPanel.add(new JLabel("Show "));
		recordSelectorPanel.add(jcbxNumRecords);
		recordSelectorPanel.add(new JLabel(" records for difficulty "));
		recordSelectorPanel.add(jcbxDiff);
		
		// Container for button so it doesn't expand to full pane size
		JPanel buttonContainer = new JPanel();
		buttonContainer.add(jbtReturn);
		
		// Add all menu components to a master panel
		JPanel menuPanel = new JPanel(new GridLayout(3,1));
		menuPanel.add(recordSelectorPanel);
		menuPanel.add(dbErrors);
		menuPanel.add(buttonContainer);
		
		add(menuPanel, BorderLayout.SOUTH);
		
		jbtReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		// Both comboboxes simply re-populate the table upon selection change
		jcbxNumRecords.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				cachedSelectedRecordCount = jcbxNumRecords.getSelectedIndex();
				populateTable();				
			}
		});
		
		jcbxDiff.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				cachedSelectedDifficulty = jcbxDiff.getSelectedIndex();
				populateTable();				
			}
		});
		
		FrameUtils.setIcon(this, "trophy.png");
		setTitle("High Scores");
		setVisible(true);
		pack();
		setLocationRelativeTo(null);

	}
	
	// Populates table with appropriate data depending on selected row count
	private void populateTable() {
		
		int numRecords = RECORD_COUNTS[jcbxNumRecords.getSelectedIndex()];
		int difficulty = jcbxDiff.getSelectedIndex();
		
		Object[][] data = null;
		try {
			data = DBComm.getHighScoresData(numRecords, difficulty);
		}
		catch (ClassNotFoundException | SQLException e1) {
			dbErrors.setText("  Error reaching database: " + e1.getMessage() + "  ");
			return;
		}
			
		// If this statement is reached there were no errors reaching the
		// database, so clear error text in case it is still displaying
		// an old error and then re-center and pack the frame (since the
		// error text can stretch the frame)
		dbErrors.setText(null);
		pack();
		setLocationRelativeTo(null);
		
		table.setModel(new DefaultTableModel(data, COLUMN_HEADERS));
		
		// Center alignment renderer
		DefaultTableCellRenderer centerer = new DefaultTableCellRenderer();
		centerer.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Center all columns
		for (int i = 0; i < data[0].length; i++)
			table.getColumnModel().getColumn(i).setCellRenderer(centerer);
		
	}
	
}
