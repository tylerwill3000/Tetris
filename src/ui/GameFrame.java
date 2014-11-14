package ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
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

@SuppressWarnings("serial")
public class GameFrame extends JFrame {
	
	// Handles all thread execution for the game
	public static final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	
	// Game style constants
	public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);
	public static final Border BEVEL_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 15);
	
	// Dimension constants
	public static final int GAME_BOARD_PANEL_WIDTH = 300;
	public static final int INFO_PANEL_WIDTH = 150;

	// Controls the game flow. Doesn't matter what the initial delay is
	//since it is set later
	static Timer fallTimer = new Timer(0, new FallTimer());
	
	public GameFrame() {
		
		UIBox.gameBoardPanel.setBorder(LINE_BORDER);
		
		add(UIBox.gameBoardPanel, BorderLayout.WEST);
		add(createInfoPanel(), BorderLayout.CENTER);
		add(UIBox.menuButtonsPanel, BorderLayout.SOUTH);
		
		setTitle("Tetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(GAME_BOARD_PANEL_WIDTH+INFO_PANEL_WIDTH,600);
		setResizable(false); // I don't want to mess with trying to make this work right
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	// Creates the info panel (holds next piece / scoring info / settings
	private JPanel createInfoPanel() {
		
		JPanel infoPanel = new JPanel(new BorderLayout());
		
		// Configure and add the next piece panel
		UIBox.nextPiecePanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 130));
		UIBox.nextPiecePanel.setBorder(new TitledBorder("Next Piece"));
		infoPanel.add(UIBox.nextPiecePanel, BorderLayout.NORTH);
			
		// Configure and add the scoring panel. I don't think you're able
		// to set the preferred size on components that are added to the
		// center of a border layout - they simply expand to fill the empty
		// space between the north and the south. Therefore, there is no
		// point in trying to force the size on the scoring panel
		UIBox.scorePanel.setBorder(new TitledBorder("Scoring Info"));
		infoPanel.add(UIBox.scorePanel, BorderLayout.CENTER);
		
		// Add in checkbox listeners
		UIBox.ghostSquaresCbx.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				
				
				if (UIBox.ghostSquaresCbx.isSelected())
					UIBox.gameBoardPanel.paintGhostPiece();
				else 
					UIBox.gameBoardPanel.eraseGhostPiece();
				
				// In case ghost overlaps current piece
				UIBox.gameBoardPanel.paintCurrentPiece();
				
			}
			
		});
		
		UIBox.musicCbx.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				
				if (UIBox.musicCbx.isSelected())
					AudioManager.playCurrentSoundtrack();
				else
					AudioManager.stopCurrentSoundtrack();
				
			}
			
		});
		
		UIBox.ghostSquaresCbx.setFocusable(false);
		UIBox.musicCbx.setFocusable(false);
		
		JPanel settingsPanel = new JPanel(new GridLayout(2,1));
		settingsPanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 100));
		settingsPanel.setBorder(new TitledBorder("Settings"));
		settingsPanel.add(UIBox.ghostSquaresCbx);
		settingsPanel.add(UIBox.musicCbx);
		infoPanel.add(settingsPanel, BorderLayout.SOUTH);
		
		return infoPanel;
		
	}
	
}
