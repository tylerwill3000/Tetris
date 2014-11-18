package ui;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameFrame extends JFrame {
	
	// Handles all thread execution for the game
	static final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	
	// GGUI style constants
	static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);
	static final Border BEVEL_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 17);
	
	// Dimension constants
	private static final int GAME_BOARD_PANEL_WIDTH = 300;
	private static final int INFO_PANEL_WIDTH = 150;
	
	GameFrame() {
		
		// Configure the game board panel before placing
		GUI.gameBoardPanel.setFocusable(true);
		GUI.gameBoardPanel.setPreferredSize(new Dimension(GameFrame.GAME_BOARD_PANEL_WIDTH, 750));
		
		add(GUI.gameBoardPanel, BorderLayout.WEST);
		add(createInfoPanel(), BorderLayout.CENTER);
		add(GUI.menuPanel, BorderLayout.SOUTH);
		
		setIconImage(new ImageIcon(getClass().getResource("images/icon.png")).getImage());
		setTitle("Tetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(GAME_BOARD_PANEL_WIDTH + INFO_PANEL_WIDTH, 600);
		setResizable(false); // I don't want to mess with trying to make this work right
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	// Creates the info panel (holds next piece / scoring info / settings
	private JPanel createInfoPanel() {
		
		JPanel infoPanel = new JPanel(new BorderLayout());
		
		// Configure next piece panel size
		GUI.nextPiecePanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 130));
		
		// Create a container panel to add the controls and settings panels to
		JPanel controlsAndSettingsContainer = new JPanel(new BorderLayout());
		
		// Create and configure the controls panel
		JPanel controls = createControlsPanel();
		controls.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 130));
		
		// Set size for the settings panel
		GUI.settingsPanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 100));
		
		controlsAndSettingsContainer.add(controls, BorderLayout.NORTH);
		controlsAndSettingsContainer.add(GUI.settingsPanel, BorderLayout.SOUTH);
		
		// Add all components to the info panel
		infoPanel.add(GUI.nextPiecePanel, BorderLayout.NORTH);
		infoPanel.add(GUI.scorePanel, BorderLayout.CENTER);
		infoPanel.add(controlsAndSettingsContainer, BorderLayout.SOUTH);
		
		return infoPanel;
		
	}
	
	// Creates the controls panel. Basically just a bunch of JLabels
	private JPanel createControlsPanel() {
		
		JPanel controls = new JPanel();
		controls.setBorder(new TitledBorder("Controls"));
		
		JPanel keyContainer = new JPanel(new GridLayout(6,1));
		keyContainer.add(new JLabel("Up:"));
		keyContainer.add(new JLabel("'F:"));
		keyContainer.add(new JLabel("Down:"));
		keyContainer.add(new JLabel("Left:"));
		keyContainer.add(new JLabel("Right:"));
		keyContainer.add(new JLabel("Space:"));
		
		JPanel actionContainer = new JPanel(new GridLayout(6,1));
		actionContainer.add(new JLabel("rotate CW"));
		actionContainer.add(new JLabel("rotate CCW"));
		actionContainer.add(new JLabel("shift down"));
		actionContainer.add(new JLabel("shift left"));
		actionContainer.add(new JLabel("shift right"));
		actionContainer.add(new JLabel("instant drop"));
		
		controls.add(keyContainer, BorderLayout.WEST);
		controls.add(actionContainer, BorderLayout.EAST);		
		
		return controls;
		
	}
	
}
