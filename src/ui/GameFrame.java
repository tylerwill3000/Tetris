package ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import model.AudioManager;
import model.GameBoardModel;
import model.Piece;
import model.PieceFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;

@SuppressWarnings("serial")
public class GameFrame extends JFrame {
	
	// Borders used
	public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.BLACK, 2);
	public static final Border BEVEL_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	
	public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 15);
	
	public static final int GAME_BOARD_PANEL_WIDTH = 300;
	public static final int INFO_PANEL_WIDTH = 150;
	
	private JButton start = new JButton("Start");
	private JButton pause = new JButton("Pause");
	private JButton resume = new JButton("Resume");
	
	private JCheckBox ghostSquares = new JCheckBox("Ghost Squares", true);
	
	// All major panel components
	private GameBoardPanel gameBoardPanel = new GameBoardPanel();
	private NextPiecePanel nextPiecePanel = new NextPiecePanel();
	private ScorePanel scorePanel = new ScorePanel();	
	
	// Controls the game flow. Doesn't matter what the initial delay is
	//since it is set later
	private Timer fallTimer = new Timer(0, new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			
			if (gameBoardPanel.currentPiece.canMove(1,0)) 
				gameBoardPanel.lowerPiece();
	
			else {
		
				gameBoardPanel.placePiece();
				
				// Game is complete!
				if (GameBoardModel.getLevel() == 11) {
					fallTimer.stop();
					return;					
				}
				
				// Not the most elegant way to handle level up stuff, this works for now
				if (GameBoardModel.justLeveled) {
				
					// Decrease timer delay
					fallTimer.setDelay(GameBoardModel.getTimerDelay());
					
					scorePanel.flashLevelLabel();
					GameBoardModel.justLeveled = false;
					
				}
				
				// Enable the keyboard if it was disabled
				// as a result of the player hitting the space 
				// bar to place the piece
				if (!gameBoardPanel.isKeyboardEnabled())
					gameBoardPanel.enableKeyboard();

				// Generate a new piece. Before continuing, make sure
				// it is able to emerge onto the board. If not, it's
				// game over
				Piece nextPiece = PieceFactory.receiveNextPiece();

				if (!nextPiece.canEmerge()) {
					
					fallTimer.stop();
					AudioManager.stopCurrentSoundtrack();
					AudioManager.playGameOverSound();
					scorePanel.refreshScoreInfo();
					gameBoardPanel.disableKeyboard();
					gameBoardPanel.paintGameOverFill();
					
					// Re-enable the start button
					start.addActionListener(startButtonListener);
					
				}
			
				else {

					gameBoardPanel.currentPiece = nextPiece;
					scorePanel.refreshScoreInfo();
					nextPiecePanel.eraseCurrentPiece();
					nextPiecePanel.currentPiece = PieceFactory.peekAtNextPiece();
					
					// Paint new pieces
					nextPiecePanel.paintCurrentPiece();
					gameBoardPanel.paintCurrentAndGhost();
					
				}
				
			}
			
		}
		
	});
	
	// Start button gets its own declared listener since I need to be able
	// to control whether it's enabled / disabled
	private ActionListener startButtonListener = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			
			// Clear all old data from the model
			GameBoardModel.reset();
			
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
			fallTimer.start();
			AudioManager.playCurrentSoundtrack();
			
			// Start button is disabled once pressed. It will re-enable
			// after game over
			start.removeActionListener(this);
			
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
		
		pause.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				// Disable the start button. Weird things happen
				// if this is pressed while the game is paused
				start.removeActionListener(startButtonListener);
				
				fallTimer.stop();
				AudioManager.stopCurrentSoundtrack();
				gameBoardPanel.disableKeyboard();
			}
			
		});
		
		resume.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				fallTimer.start();
				AudioManager.playCurrentSoundtrack();
				gameBoardPanel.enableKeyboard();
			}
		});
		
		setTitle("Tetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(GAME_BOARD_PANEL_WIDTH+INFO_PANEL_WIDTH,600);
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	// Creates the info panel (holds next piece / scoring info / settings
	private JPanel createInfoPanel() {
		
		JPanel infoPanel = new JPanel(new BorderLayout());
		infoPanel.setBorder(LINE_BORDER);
		
		// 'Next Piece' panel
		JPanel nextPiecePanel = new JPanel(new BorderLayout());
		
		// Put the next piece label into a container to give it
		// some padding
		JPanel nextLabelContainer = new JPanel();
		nextLabelContainer.setBorder(new EmptyBorder(8,8,8,8));
		
		// Prepare the actual "Next Piece" JLabel
		JLabel nextLabel = new JLabel("Next Piece");
		nextLabel.setFont(LABEL_FONT);
		nextLabelContainer.add(nextLabel);
		
		// Add both components to the next piece panel
		nextPiecePanel.add(nextLabelContainer, BorderLayout.NORTH);
		this.nextPiecePanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 110));
		nextPiecePanel.add(this.nextPiecePanel, BorderLayout.CENTER);
				
		infoPanel.add(nextPiecePanel, BorderLayout.NORTH);
		
		// Scoring info panel
		scorePanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 50));
		scorePanel.setBorder(LINE_BORDER);
		infoPanel.add(scorePanel, BorderLayout.CENTER);
		
		// Add in ghost squares checkbox
		ghostSquares.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				GameBoardModel.toggleGhostSquares();				
			}
		});
		
		ghostSquares.setFocusable(false);
		
		JPanel ghostContainer = new JPanel();
		ghostContainer.setBorder(new EmptyBorder(20,10,20,10));
		ghostContainer.add(ghostSquares);
		infoPanel.add(ghostContainer, BorderLayout.SOUTH);
		
		return infoPanel;
		
	}

	
}
