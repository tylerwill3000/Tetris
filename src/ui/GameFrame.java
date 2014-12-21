package ui;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameFrame extends JFrame {
	
	// GUI style constants
	static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);
	static final Border BEVEL_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 19);
	
	/** All major GUI components. Declared static so they can 'talk
	 *  to each other through the GameFrame class **/
	static GameBoardPanel gameBoardPanel = new GameBoardPanel();
	static NextPiecePanel nextPiecePanel = new NextPiecePanel("Next Piece");
	static NextPiecePanel holdPanel = new NextPiecePanel("Hold");
	static ScorePanel scorePanel = new ScorePanel();
	static MenuPanel menuPanel = new MenuPanel();
	
	// This is public so it can be accessed by the AudioManager
	// and GameBoardModel
	public static SettingsPanel settingsPanel = new SettingsPanel();
	
	// Handles all thread execution for the game
	static final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	
	// Dimension constants
	private static final int GAME_BOARD_PANEL_WIDTH = GridPainter.SQUARE_SIDE_LENGTH * GameBoardPanel.H_CELLS;
	private static final int GAME_BOARD_PANEL_HEIGHT = GridPainter.SQUARE_SIDE_LENGTH * GameBoardPanel.V_CELLS;
	
	static final int INFO_PANEL_WIDTH = 5 * GridPainter.SQUARE_SIDE_LENGTH;
	
	GameFrame() {
		
		add(createHoldPanel(), BorderLayout.WEST);
		add(gameBoardPanel, BorderLayout.CENTER);
		add(createInfoPanel(), BorderLayout.EAST);
		add(menuPanel, BorderLayout.SOUTH);
		
		FrameUtils.setIcon(this, "game-icon.png");
		setTitle("Tetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(GAME_BOARD_PANEL_WIDTH + INFO_PANEL_WIDTH * 2, GAME_BOARD_PANEL_HEIGHT);
		setResizable(false); // I don't want to mess with trying to make this work right
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	private JPanel createHoldPanel() {
		
		JPanel holdContainer = new JPanel(new BorderLayout());
		holdContainer.add(holdPanel, BorderLayout.NORTH);
		holdContainer.add(createControlsPanel(), BorderLayout.CENTER);
		return holdContainer;
		
	}
	
	// Creates the info panel (holds next piece / scoring info / settings
	private JPanel createInfoPanel() {
		
		JPanel infoPanel = new JPanel(new BorderLayout());
		
		// Add all components to the info panel
		infoPanel.add(nextPiecePanel, BorderLayout.NORTH);
		infoPanel.add(scorePanel, BorderLayout.CENTER);
		infoPanel.add(settingsPanel, BorderLayout.SOUTH);
		
		return infoPanel;
		
	}
	
	// Creates the controls panel. Basically just a bunch of JLabels
	private JPanel createControlsPanel() {
		
		JPanel keyCombos = new JPanel(new GridLayout(15,1));
		for (String keyCombo : new String[]{
				" Up ",
				" 'F' ",
				" Down ",
				" Left ",
				" Right ",
				" 'S' + left: ",
				" 'S' + right: ",
				" Space: ",
				" 'D': ",
				" 'E': ",
				" 'S': ",
				" 'P': ",
				" 'R': ",
				" 'G': ",
				" 'H': "				
		}) keyCombos.add(new JLabel(keyCombo));
		
		JPanel actions = new JPanel(new GridLayout(15,1));
		for (String action : new String[]{
				" Rotate CW ",
				" Rotate CCW ",
				" Shift down ",
				" Shift left ",
				" Shift right ",
				" Superslide left ",
				" Superslide right ",
				" Instant drop ",
				" Set hold ",
				" Release hold ",
				" Start ",
				" Pause ",
				" Resume ",
				" Give Up ",
				" High Scores "
		}) actions.add(new JLabel(action));
		
		JPanel controls = new JPanel(new BorderLayout());
		controls.setBorder(new TitledBorder("Controls"));
		controls.add(keyCombos, BorderLayout.WEST);
		controls.add(actions, BorderLayout.EAST);
		
		return controls;
		
	}
	
}
