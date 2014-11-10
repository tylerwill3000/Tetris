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
import model.GameBoardModel;
import model.Piece;
import model.PieceFactory;
import model.SettingsManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;

@SuppressWarnings("serial")
public class GameFrame extends JFrame {
	
	// Game style constants
	public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);
	public static final Border BEVEL_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 15);
	
	// Dimension constants
	public static final int GAME_BOARD_PANEL_WIDTH = 300;
	public static final int INFO_PANEL_WIDTH = 150;
	
	// Major panel components
	private GameBoardPanel gameBoardPanel = new GameBoardPanel();
	private NextPiecePanel nextPiecePanel = new NextPiecePanel();
	private ScorePanel scorePanel = new ScorePanel();
	
	// Minor GUI components
	private JButton start = new JButton("Start");
	private JButton pause = new JButton("Pause");
	private JButton resume = new JButton("Resume");
	private JCheckBox ghostSquaresCbx = new JCheckBox("Ghost Squares", true);
	private JCheckBox musicCbx = new JCheckBox("Music", true);
	
	// Controls the game flow. Doesn't matter what the initial delay is
	//since it is set later
	private Timer fallTimer = new Timer(0, new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			
			// If the piece can be lowered, lower it and then return
			if (gameBoardPanel.currentPiece.canMove(1,0)) {
				gameBoardPanel.lowerPiece();
				return; 
			}
			
			gameBoardPanel.placePiece();
			
			// See if game is complete by placing this piece
			if (GameBoardModel.getLevel() == 11)
				processGameComplete();
			
			else {
				
				// Not the most elegant way to handle level up stuff, this works for now
				if (GameBoardModel.justLeveled)
					processLevelUp();
				
				// Enable the keyboard if it was disabled
				// as a result of the player hitting the space 
				// bar to place the piece
				if (!gameBoardPanel.isKeyboardEnabled())
					gameBoardPanel.enableKeyboard();

				// Generate a new piece. Before continuing, make sure
				// it is able to emerge onto the board. If not, it's
				// game over
				Piece nextPiece = PieceFactory.receiveNextPiece();

				if (!nextPiece.canEmerge())
					processGameOver();
			
				else {
					paintNewPieces(nextPiece);
					scorePanel.refreshScoreInfo();
				}
				
			}
			
		}
		
		// Resets timer delay / flashes level label
		private void processLevelUp() {
			fallTimer.setDelay(GameBoardModel.getTimerDelay());
			scorePanel.flashLevelLabel();
			GameBoardModel.justLeveled = false;
		}
		
		// What happens when the final level is cleared
		private void processGameComplete() {
			fallTimer.stop();
			scorePanel.refreshScoreInfo(); // Gets final score to show up
			scorePanel.flashWinMessage();
			
			//AudioManager.playVictoryJingle();
			
			gameBoardPanel.disableKeyboard();
			
			// Re-enable start button and disable pause /resume
			pause.removeActionListener(pauseButtonListener);
			resume.removeActionListener(resumeButtonListener);
			start.addActionListener(startButtonListener);
		}
		
		// What happens when the next piece can't emerge
		private void processGameOver() {
			
			fallTimer.stop();
			
			AudioManager.stopCurrentSoundtrack();
			AudioManager.playGameOverSound();
			
			scorePanel.refreshScoreInfo();
			gameBoardPanel.disableKeyboard();
			gameBoardPanel.paintGameOverFill();
			
			// Re-enable the start button
			start.addActionListener(startButtonListener);
			
			// Disable the pause / resume buttons again. They are
			// re-enabled once the start button is pressed
			pause.removeActionListener(pauseButtonListener);
			resume.removeActionListener(resumeButtonListener);
			
		}
		
		// Erases the old pieces and paints the new ones
		private void paintNewPieces(Piece nextPiece) {
			
			gameBoardPanel.currentPiece = nextPiece;
			nextPiecePanel.eraseCurrentPiece();
			nextPiecePanel.currentPiece = PieceFactory.peekAtNextPiece();
			
			nextPiecePanel.paintCurrentPiece();
			gameBoardPanel.paintCurrentAndGhost();
			
		}
		
	});
	
	// Buttons get declared listeners since I want to be able to control
	// when they are enabled / disabled
	private ActionListener startButtonListener = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			
			// Clear all old data from the model
			GameBoardModel.reset();
			
			// Reprint all squares on the game board. Since
			// the model was reset, they will all be blank
			gameBoardPanel.fullReprint();
			
			// Clears all old score info from previous games
			scorePanel.refreshScoreInfo();
			
			// Reset timer delay to default value
			fallTimer.setDelay(GameBoardModel.INITIAL_TIMER_DELAY);
			
			// Set initial pieces
			gameBoardPanel.currentPiece = PieceFactory.receiveNextPiece();
			nextPiecePanel.eraseCurrentPiece(); // In case the piece is still painted from a previous game
			nextPiecePanel.currentPiece = PieceFactory.peekAtNextPiece();
			
			// Paint initial pieces
			gameBoardPanel.paintCurrentAndGhost();
			nextPiecePanel.paintCurrentPiece();
			
			gameBoardPanel.enableKeyboard();
			
			// Enable pause / resume buttons. There is no reason for these to be
			// enabled before the game starts
			pause.addActionListener(pauseButtonListener);
			resume.addActionListener(resumeButtonListener);
			
			// Start button is disabled once pressed. It will re-enable
			// after game over
			start.removeActionListener(this);
			
			fallTimer.start();
			AudioManager.playCurrentSoundtrack();
			
		}
		
	};
	
	private ActionListener pauseButtonListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {			
			fallTimer.stop();
			AudioManager.stopCurrentSoundtrack();
			gameBoardPanel.disableKeyboard();
		}
	
	};
	
	private ActionListener resumeButtonListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			fallTimer.start();
			AudioManager.playCurrentSoundtrack();
			gameBoardPanel.enableKeyboard();
		}
		
	};
	
	public GameFrame() {
		
		gameBoardPanel.setBorder(LINE_BORDER);
		add(gameBoardPanel, BorderLayout.WEST);
		add(createInfoPanel(), BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(start);
		buttonPanel.add(pause);
		buttonPanel.add(resume);
		add(buttonPanel, BorderLayout.SOUTH);
		
		// Prevent all button components from receiving the focus
		// when they are clicked - focus should always be on the
		// game board panel
		start.setFocusable(false);
		pause.setFocusable(false);
		resume.setFocusable(false);
		
		start.addActionListener(startButtonListener);
		
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
		nextPiecePanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 130));
		nextPiecePanel.setBorder(new TitledBorder("Next Piece"));
		infoPanel.add(nextPiecePanel, BorderLayout.NORTH);
			
		// Configure and add the scoring panel. I don't think you're able
		// to set the preferred size on components that are added to the
		// center of a border layout - they simply expand to fill the empty
		// space between the north and the south. Therefore, there is no
		// point in trying to force the size on the scoring panel
		scorePanel.setBorder(new TitledBorder("Scoring Info"));
		infoPanel.add(scorePanel, BorderLayout.CENTER);
		
		// Add in checkbox listeners
		ghostSquaresCbx.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				
				SettingsManager.toggleGhostSquares();
				
				if (ghostSquaresCbx.isSelected())
					gameBoardPanel.paintGhostPiece();
				else 
					gameBoardPanel.eraseGhostPiece();
				
				// In case ghost overlaps current piece
				gameBoardPanel.paintCurrentPiece();
				
			}
			
		});
		
		musicCbx.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				
				SettingsManager.toggleMusic();
				
				if (musicCbx.isSelected())
					AudioManager.playCurrentSoundtrack();
				else
					AudioManager.stopCurrentSoundtrack();
				
			}
			
		});
		
		ghostSquaresCbx.setFocusable(false);
		musicCbx.setFocusable(false);
		
		JPanel settingsPanel = new JPanel(new GridLayout(2,1));
		settingsPanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 100));
		settingsPanel.setBorder(new TitledBorder("Settings"));
		settingsPanel.add(ghostSquaresCbx);
		settingsPanel.add(musicCbx);
		infoPanel.add(settingsPanel, BorderLayout.SOUTH);
		
		return infoPanel;
		
	}
	
}
