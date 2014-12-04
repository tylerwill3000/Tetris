package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import model.DBComm;

public class HighScoreFrame extends JFrame {
	
	private int numScores = 10;
	
	private JButton jbtReturn = new JButton("Return");
	
	HighScoreFrame() {
		
		setLayout(new BorderLayout());
		
		add(new JScrollPane(buildTable()), BorderLayout.CENTER);
		JPanel btnContainer = new JPanel();
		btnContainer.add(jbtReturn);
		add(btnContainer, BorderLayout.SOUTH);
		
		jbtReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		pack();
		setTitle("High Scores");
		setLocationRelativeTo(null);
		setVisible(true);

	}
	
	// Initializes and configures the table
	private JTable buildTable() {
		
		try {
			
			JTable t = DBComm.getHighScoresTable(numScores);
			
			// Center alignment renderer
			DefaultTableCellRenderer centerer = new DefaultTableCellRenderer();
			centerer.setHorizontalAlignment(SwingConstants.CENTER);
			
			// Center all columns
			for (int i = 0; i < t.getColumnCount(); i++) {
				t.getColumnModel().getColumn(i).setCellRenderer(centerer);
			}
			
			return t;
			
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
}
