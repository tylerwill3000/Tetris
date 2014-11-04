

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;

@SuppressWarnings("serial")
public class GameFrame extends JFrame {
	
	// Shared by multiple panels, so made a constant
	public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.BLACK, 2);
	
	public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 15);
	
	public static final int GAME_BOARD_PANEL_WIDTH = 300;
	public static final int INFO_PANEL_WIDTH = 150;
	
	private JButton start = new JButton("Start");
	private JButton pause = new JButton("Pause");
	private JButton resume = new JButton("Resume");
	
	private JCheckBox ghostSquares = new JCheckBox("Ghost Squares", true);
	
	// All major panel components
	private GameBoardPanel gameBoard = new GameBoardPanel();
	private NextPiecePanel nextPiecePanel = new NextPiecePanel();
	private ScorePanel scorePanel = new ScorePanel();	
	
	private Timer fallTimer = new Timer(500, new FallTimerListener(gameBoard, nextPiecePanel, scorePanel));
	
	public GameFrame() {
		
		gameBoard.setBorder(LINE_BORDER);
		add(gameBoard, BorderLayout.WEST);
		add(createInfoPanel(), BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(start);
		buttonPanel.add(pause);
		buttonPanel.add(resume);
		add(buttonPanel, BorderLayout.SOUTH);
		
		// Prevent all button components from receiving the focus
		// when they are clicked - focus should always be on the
		// game board frame
		start.setFocusable(false);
		pause.setFocusable(false);
		resume.setFocusable(false);
		
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				gameBoard.enableKeyboard();
				gameBoard.paintCurrentAndGhost();
				nextPiecePanel.paintCurrentPiece();
				fallTimer.start();
				AudioManager.playCurrentSoundtrack();
				
			}
		});
		
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fallTimer.stop();
				AudioManager.stopCurrentSoundtrack();
				gameBoard.disableKeyboard();
			}
		});
		
		resume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fallTimer.start();
				AudioManager.playCurrentSoundtrack();
				gameBoard.enableKeyboard();
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
		this.nextPiecePanel.setPreferredSize(new Dimension(GameFrame.INFO_PANEL_WIDTH,120));
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
		ghostContainer.setBorder(new EmptyBorder(10,10,10,10));
		ghostContainer.add(ghostSquares);
		infoPanel.add(ghostContainer, BorderLayout.SOUTH);
		
		return infoPanel;
		
	}
	
}
