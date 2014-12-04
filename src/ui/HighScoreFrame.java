package ui;

import java.awt.BorderLayout;
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
	
	private final static Object[] COLUMN_HEADERS = {
		"Name",
		"Score",
		"Lines",
		"Level",
		"Difficulty"
	};
	
	private JComboBox<String> jcbxNumRecords = new JComboBox<>(new String[]{
			"10","25","50","100"
	});
	
	private JButton jbtReturn = new JButton("Return");
	private JTable table = new JTable();
	
	private JLabel dbErrors = new JLabel();
	
	HighScoreFrame() {
		
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
		recordSelectorPanel.add(new JLabel(" records"));
		recordSelectorPanel.add(dbErrors);
		
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
		
		jcbxNumRecords.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				populateTable();				
			}
		});
		
		pack();
		setTitle("High Scores");
		setLocationRelativeTo(null);
		setVisible(true);

	}
	
	// Populates table with appropriate data depending on selected row count
	private void populateTable() {
		
		try {
			
			int numRecords = Integer.parseInt((String)(jcbxNumRecords.getSelectedItem()));
			
			Object[][] data = DBComm.getHighScoresData(numRecords);
			
			table.setModel(new DefaultTableModel(data, COLUMN_HEADERS));
			
			// Center alignment renderer
			DefaultTableCellRenderer centerer = new DefaultTableCellRenderer();
			centerer.setHorizontalAlignment(SwingConstants.CENTER);
			
			// Center all columns
			for (int i = 0; i < data[0].length; i++) {
				table.getColumnModel().getColumn(i).setCellRenderer(centerer);
			}

		}
		catch (ClassNotFoundException | SQLException e) {
			dbErrors.setText("There were errors reaching the database: " + e.getMessage());
		}
		
	}
	
}
