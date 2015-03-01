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

import util.FrameUtils;
import model.Properties;

public class DBSettingsFrame extends SupplementarySettingsFrame {
	
	private final static int TEXT_FIELD_WIDTH = 15;
	
	private JLabel _jlbHost = new JLabel("   Database host: ");
	private JLabel _jlbName = new JLabel("   Database name:");
	private JLabel _jlbUser = new JLabel("   Database user:");
	private JLabel _jlbPass = new JLabel("   Database password:");
	
	private JTextField _jtxHost = new JTextField(Properties.getDBHostProperty(), TEXT_FIELD_WIDTH);
	private JTextField _jtxName = new JTextField(Properties.getDBNameProperty(), TEXT_FIELD_WIDTH);
	private JTextField _jtxUser = new JTextField(Properties.getDBUserProperty(), TEXT_FIELD_WIDTH);
	private JPasswordField _jpqPass = new JPasswordField(Properties.getDBPassProperty(), TEXT_FIELD_WIDTH);
	
	private KeyAdapter _keyListener = new KeyAdapter() {
		public void keyReleased(KeyEvent e) {

			Object source = e.getSource();
			if (source == _jtxHost)
				Properties.setDBHostProperty(_jtxHost.getText());
			else if (source == _jtxName)
				Properties.setDBNameProperty(_jtxName.getText());
			else if (source == _jtxUser)
				Properties.setDBUserProperty(_jtxUser.getText());
			else if (source == _jpqPass) {
				String.valueOf(_jpqPass.getPassword());
			}
			
		}
	};
	
	public DBSettingsFrame() {
		
		JPanel fieldLabels = new JPanel(new GridLayout(4,1));
		for (JLabel l : Arrays.asList(_jlbHost, _jlbName, _jlbUser, _jlbPass)) {
			fieldLabels.add(l);
		}
		
		JPanel textFields = new JPanel(new GridLayout(4,1));
		for (JTextField txt : Arrays.asList(_jtxHost, _jtxName, _jtxUser, _jpqPass)) {
			textFields.add(txt);
			txt.addKeyListener(_keyListener);
		}
		
		JPanel fieldContainer = new JPanel(new BorderLayout());
		fieldContainer.add(fieldLabels, BorderLayout.WEST);
		fieldContainer.add(textFields, BorderLayout.EAST);
		
		setLayout(new BorderLayout());
		add(fieldContainer, BorderLayout.CENTER);
		add(FrameUtils.nestInPanel(_btnSaveAndClose), BorderLayout.SOUTH);
		
		setTitle("Database Connectivity");
		setSize(320,180);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
}
