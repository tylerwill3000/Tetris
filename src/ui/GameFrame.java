package ui;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import model.AudioManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameFrame extends JFrame {
	
	// Handles all thread execution for the game
	static final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	
	// Game style constants
	static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);
	static final Border BEVEL_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 15);
	
	// Dimension constants
	private static final int GAME_BOARD_PANEL_WIDTH = 300;
	private static final int INFO_PANEL_WIDTH = 150;
	
	GameFrame() {
		
		// Configure the game board panel before placing
		UIBox.gameBoardPanel.setFocusable(true);
		UIBox.gameBoardPanel.setPreferredSize(new Dimension(GameFrame.GAME_BOARD_PANEL_WIDTH, 750));
		
		add(UIBox.gameBoardPanel, BorderLayout.WEST);
		add(createInfoPanel(), BorderLayout.CENTER);
		add(UIBox.menuPanel, BorderLayout.SOUTH);
		
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
		UIBox.nextPiecePanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 130));
		
		
		// Create a container panel to add the controls and
		// settings panels to
		JPanel controlsAndSettingsContainer = new JPanel(new BorderLayout());
		JPanel controls = createControlsPanel();
		controls.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 140));
		UIBox.settingsPanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 100));
		controlsAndSettingsContainer.add(controls, BorderLayout.NORTH);
		controlsAndSettingsContainer.add(UIBox.settingsPanel, BorderLayout.SOUTH);
		
		// Add all components to the info panel
		infoPanel.add(UIBox.nextPiecePanel, BorderLayout.NORTH);
		infoPanel.add(UIBox.scorePanel, BorderLayout.CENTER);
		infoPanel.add(controlsAndSettingsContainer, BorderLayout.SOUTH);
		
		return infoPanel;
		
	}
	
	private JPanel createControlsPanel() {
		
		JPanel controls = new JPanel(new GridLayout(6,1));
		controls.setBorder(new TitledBorder("Controls"));
		controls.add(new JLabel("Up: rotate CW"));
		controls.add(new JLabel("'F': rotate CCW"));
		controls.add(new JLabel("Down: shift down"));
		controls.add(new JLabel("Left: shift left"));
		controls.add(new JLabel("Right: shift right"));
		controls.add(new JLabel("Space: instant drop"));
		return controls;
		
	}
	
}
