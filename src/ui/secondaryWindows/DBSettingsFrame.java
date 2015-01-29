package ui.secondaryWindows;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import ui.util.FrameUtils;
import model.Properties;

public class DBSettingsFrame extends SupplementarySettingsFrame {
	
	private final static int TEXT_FIELD_WIDTH = 15;
	
	private JLabel jlbHost = new JLabel("   Database host: ");
	private JLabel jlbName = new JLabel("   Database name:");
	private JLabel jlbUser = new JLabel("   Database user:");
	private JLabel jlbPass = new JLabel("   Database password:");
	
	private JTextField jtxHost = new JTextField(Properties.getDBHostProperty(), TEXT_FIELD_WIDTH);
	private JTextField jtxName = new JTextField(Properties.getDBNameProperty(), TEXT_FIELD_WIDTH);
	private JTextField jtxUser = new JTextField(Properties.getDBUserProperty(), TEXT_FIELD_WIDTH);
	private JPasswordField jpwPass = new JPasswordField(Properties.getDBPassProperty(), TEXT_FIELD_WIDTH);
	
	public DBSettingsFrame() {
		
		JPanel fieldLabels = new JPanel(new GridLayout(4,1));
		for (JLabel l : Arrays.asList(jlbHost, jlbName, jlbUser, jlbPass)) {
			fieldLabels.add(l);
		}
		
		JPanel textFields = new JPanel(new GridLayout(4,1));
		for (JTextField txt : Arrays.asList(jtxHost, jtxName, jtxUser, jpwPass)) {
			textFields.add(txt);
		}
		
		jtxHost.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				Properties.setDBHostProperty(jtxHost.getText());
			}
		});
		
		jtxName.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				Properties.setDBNameProperty(jtxName.getText());
			}
		});
		
		jtxUser.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				Properties.setDBUserProperty(jtxUser.getText());
			}
		});
		
		jpwPass.addKeyListener(new KeyAdapter() {
			@SuppressWarnings("deprecation")
			public void keyReleased(KeyEvent e) {
				Properties.setDBPassProperty(jpwPass.getText());
			}
		});
		
		JPanel fieldContainer = new JPanel(new BorderLayout());
		fieldContainer.add(fieldLabels, BorderLayout.WEST);
		fieldContainer.add(textFields, BorderLayout.EAST);
		
		setLayout(new BorderLayout());
		add(fieldContainer, BorderLayout.CENTER);
		add(FrameUtils.nestInPanel(saveAndClose), BorderLayout.SOUTH);
		
		setTitle("Database Connectivity");
		setSize(320,180);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
}
