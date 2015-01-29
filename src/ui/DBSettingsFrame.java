package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.Properties;

public class DBSettingsFrame extends JFrame {
	
	private final static int TEXT_FIELD_WIDTH = 15;
	
	private JLabel jlbHost = new JLabel("  Database host:   ");
	private JLabel jlbName = new JLabel("  Database name:  ");
	private JLabel jlbUser = new JLabel("  Database user:  ");
	private JLabel jlbPass = new JLabel("  Database password:  ");
	
	private JTextField jtxHost = new JTextField(TEXT_FIELD_WIDTH);
	private JTextField jtxName = new JTextField(TEXT_FIELD_WIDTH);
	private JTextField jtxUser = new JTextField(TEXT_FIELD_WIDTH);
	private JPasswordField jpwPass = new JPasswordField(TEXT_FIELD_WIDTH);
	
	private GameFrame.TetrisButton jbtSave = new GameFrame.TetrisButton("Save");
	private GameFrame.TetrisButton jbtClose = new GameFrame.TetrisButton("Close");
	
	DBSettingsFrame() {
		
		jtxHost.setText(Properties.GAME_PROPERTIES.getProperty("db.host"));
		jtxName.setText(Properties.GAME_PROPERTIES.getProperty("db.name"));
		jtxUser.setText(Properties.GAME_PROPERTIES.getProperty("db.user"));
		jpwPass.setText(Properties.GAME_PROPERTIES.getProperty("db.pass"));
		
		JPanel fieldLabels = new JPanel(new GridLayout(4,1));
		for (JLabel l : Arrays.asList(jlbHost, jlbName, jlbUser, jlbPass)) {
			fieldLabels.add(l);
		}
		
		JPanel textFields = new JPanel(new GridLayout(4,1));
		for (JTextField txt : Arrays.asList(jtxHost, jtxName, jtxUser, jpwPass)) {
			textFields.add(txt);
		}
		
		JPanel fieldContainer = new JPanel(new BorderLayout());
		fieldContainer.add(fieldLabels, BorderLayout.WEST);
		fieldContainer.add(textFields, BorderLayout.EAST);
		
		JPanel buttons = new JPanel();
		buttons.add(jbtSave);
		buttons.add(jbtClose);
		
		setLayout(new BorderLayout());
		add(fieldContainer, BorderLayout.CENTER);
		add(buttons, BorderLayout.SOUTH);
		
		jbtSave.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				
				Properties.GAME_PROPERTIES.setProperty("db.host", jtxHost.getText());
				Properties.GAME_PROPERTIES.setProperty("db.name", jtxName.getText());
				Properties.GAME_PROPERTIES.setProperty("db.user", jtxUser.getText());
				Properties.GAME_PROPERTIES.setProperty("db.pass", jpwPass.getText());
				
				try {
					Properties.saveCurrentProperties();
				}
				catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Error writing to settings file: " + e1.getMessage());
					return;
				}
				
				JOptionPane.showMessageDialog(null, "Settings saved.");
				dispose();
				
			}
		});
		
		jbtClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dispose(); }
		});
		
		setTitle("Database Connectivity");
		setSize(320,200);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
}
