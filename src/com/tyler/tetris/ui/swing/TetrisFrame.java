package com.tyler.tetris.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.tyler.tetris.model.Block;
import com.tyler.tetris.model.Block.ColoredSquare;
import com.tyler.tetris.model.BlockConveyor;
import com.tyler.tetris.model.ScoreModel;
import com.tyler.tetris.model.TetrisGameModel;
import com.tyler.tetris.ui.swing.widget.ProgressBar;
import com.tyler.tetris.ui.swing.widget.TetrisButton;
import com.tyler.tetris.util.FrameUtils;

/**
 * Master frame that holds all other components
 * @author Tyler
 */
public class TetrisFrame extends JFrame {
	
	public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);
	public static final Border BEVEL_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	public static final Border ETCHED_BORDER = BorderFactory.createEtchedBorder();
	public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 17);
	public static final int INFO_PANEL_WIDTH = GridPainter.SQUARE_SIDE_LENGTH * 5;
	public static final int INITIAL_TIMER_DELAY = 600;
	
	/************************************ UI Components ************************************/
	private BoardPanel boardPanel;
	private NextPiecePanel nextPiecePanel;
	private NextPiecePanel holdPanel;
	private MenuPanel menuPanel;
	private SettingsPanel settingsPanel;
	private ScorePanel scorePanel;
	private TetrisGameModel gameModel;
	private BlockConveyor conveyor;
	
	/************************************ Controllers ************************************/
	private Supplier<Future<?>> onGiveUp = () -> {
		gameModel.getAudioManager().playGameOverSound();
		THREAD_EXECUTOR.submit(scorePanel::flashGameOver);
		return THREAD_EXECUTOR.submit(boardPanel::spiralClear);
	};
	
	private Timer fallTimer = new Timer(INITIAL_TIMER_DELAY, e -> {
		
		Optional<Integer> linesCleared = gameModel.getBoard().tryFall();
		if (linesCleared.isPresent()) {
			
			int currentLevel = gameModel.getScoreModel().getLevel();
			int newLevel = gameModel.getScoreModel().increaseScore(linesCleared.get());
			boolean levelUp = newLevel > currentLevel;
			
			if (levelUp) {
				gameModel.getAudioManager().stopSoundtrack(currentLevel);
				if (newLevel == ScoreModel.MAX_LEVEL) {
					gameModel.getAudioManager().playVictoryFanfare();
					List<Callable<Object>> tasks = Arrays.asList(boardPanel::spiralClear, scorePanel::flashWin);
					try {
						THREAD_EXECUTOR.invokeAll(tasks);
					}
					catch (Exception ex) {
						throw new RuntimeException(ex);
					}
				}
				else {
					gameModel.getAudioManager().startSoundtrack(newLevel);
				}
			}
			
			if (!gameModel.getBoard().spawn(conveyor.shift())) {
				onGiveUp.get();
				return;
			}
			
		}
		
		boardPanel.repaint();
		nextPiecePanel.repaint();
		scorePanel.repaint();
	});
	
	private Runnable onStart = () -> {
		
		// Refresh the game model with current settings for this game
		gameModel = new TetrisGameModel();
		
		// Apply settings to score model
		gameModel.getScoreModel().setTimeAttack(settingsPanel.timeAttackOn());
		gameModel.getScoreModel().setDifficulty(settingsPanel.getDifficulty());

		// Disable and enable runtime controls based on what is permissible during gameplay
		settingsPanel.disableDifficultyList();
		settingsPanel.disableSpecialPiecesButton();
		settingsPanel.disableBlockStylesButton();
		settingsPanel.disableDatabaseConnectivityButton();
		settingsPanel.disableTimeAttackCheckbox();
		settingsPanel.enableCbxListeners();
		menuPanel.enablePauseButton();
		menuPanel.enableGiveUpButton();
		menuPanel.disableStartButton();
		boardPanel.enablePieceMovementInput();
		
		// Refresh score panel
		if (settingsPanel.timeAttackOn()) {
			scorePanel.showProgressBar();
		}
		else {
			scorePanel.hideProgressBar();
		}
		
		// Spawn first piece
		gameModel.getBoard().spawn(conveyor.shift());
		fallTimer.start();
	};
	
	private Runnable onPause = () -> {
		fallTimer.stop();
		settingsPanel.disableMusicCbxListener();
		gameModel.getAudioManager().stopSoundtrack(gameModel.getScoreModel().getLevel());
		gameModel.getAudioManager().playPauseSound();
		boardPanel.disablePieceMovementInput();
		menuPanel.enableResumeButton();
		menuPanel.disablePauseButton();
		menuPanel.disableGiveUpButton();
	};
	
	private Runnable onResume = () -> {
		fallTimer.start();
		settingsPanel.enableMusicCbxListener();
		gameModel.getAudioManager().resumeSoundtrack(gameModel.getScoreModel().getLevel());
		boardPanel.enablePieceMovementInput();
		menuPanel.enablePauseButton();
		menuPanel.enableGiveUpButton();
		menuPanel.disableResumeButton();
	};
	
	private KeyAdapter pieceMovementKeyHandler = new KeyAdapter() {
		
		Set<Integer> pressed = new HashSet<>();
		
		public void keyPressed(KeyEvent e) {
			
			int code = e.getKeyCode();
			pressed.add(code);
			
			switch (code) {
				
			case KeyEvent.VK_LEFT:
				
				if (pressed.contains(KeyEvent.VK_S)) { // Perform super-shift left if 's' is pressed
					gameModel.getBoard().ssCurrentBlockLeft();
					gameModel.getAudioManager().playSuperslideSound();
				}
				else {
					gameModel.getBoard().moveActiveBlockLeft();
				}
				
				break;
				
			case KeyEvent.VK_RIGHT:
				
				if (pressed.contains(KeyEvent.VK_S)) { // Perform super-shift right if 's' is pressed
					gameModel.getBoard().ssCurrentBlockRight();
					gameModel.getAudioManager().playSuperslideSound();
				}
				else {
					gameModel.getBoard().moveActiveBlockRight();
				}
				
				break;
				
			case KeyEvent.VK_DOWN:
				
				gameModel.getBoard().moveActiveBlockDown();
				break;
				
			case KeyEvent.VK_UP:
					
				if (gameModel.getBoard().rotateActiveBlockCW()) {
					gameModel.getAudioManager().playCWRotationSound();
				}
				break;
				
			case KeyEvent.VK_F:
				
				if (gameModel.getBoard().rotateActiveBlockCCW()) {
					gameModel.getAudioManager().playCCWRotationSound();
				}
				break;
			
			case KeyEvent.VK_D: // Hold set
				
				Block activeBlock = gameModel.getBoard().getActiveBlock();
				if (!gameModel.getCurrentHoldPiece().isPresent() && !activeBlock.isHoldBlock()) {

					// Tag as hold block
					activeBlock.tagAsHoldBlock();
					gameModel.getAudioManager().playHoldSound();

					// Transfer to hold panel
					gameModel.setCurrentHoldPiece(activeBlock);
					
					// Spawn next in board panel
					Block nextBlock = conveyor.shift();
					gameModel.getBoard().spawn(nextBlock);
				}
				
				break;
			
			case KeyEvent.VK_E: // Hold release
				
				if (gameModel.getCurrentHoldPiece().isPresent()) {
					Block heldPiece = gameModel.getCurrentHoldPiece().get();
					gameModel.getBoard().spawn(heldPiece);
					gameModel.clearCurrentHoldPiece();
					gameModel.getAudioManager().playReleaseSound();
				}
				
				break;
				
			case KeyEvent.VK_SPACE:
				
				gameModel.getBoard().dropCurrentBlock();
				gameModel.getAudioManager().playPiecePlacementSound();
				fallTimer.restart(); // Force the next tick to execute immediately on the timer
				break;
			}
			
			holdPanel.repaint();
			nextPiecePanel.repaint();
			boardPanel.repaint();
		}
		
		public void keyReleased(KeyEvent e) {
			pressed.remove(e.getKeyCode());
		}
		
	};
	
	// Handles all thread execution for the game
	static final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	
	TetrisFrame() {
		
		this.gameModel = new TetrisGameModel();
		this.boardPanel = new BoardPanel();
		
		this.nextPiecePanel = new NextPiecePanel("Next Piece") {
			@Override
			public List<ColoredSquare> getCurrentColors() {
				return gameModel.getCurrentHoldPiece().isPresent() ?
				                     gameModel.getCurrentHoldPiece().get().getNextPanelSquares() :
				                     new ArrayList<>();
			}
		};
		
		this.holdPanel = new NextPiecePanel("Hold") {
			@Override
			public List<ColoredSquare> getCurrentColors() {
				return conveyor.peek().getNextPanelSquares();
			}
		};
		
		this.menuPanel = new MenuPanel();
		this.settingsPanel = new SettingsPanel();
		this.scorePanel = new ScorePanel();
		this.conveyor = new BlockConveyor();
		
		// Hold Panel
		JPanel holdContainer = new JPanel(new BorderLayout());
		holdContainer.add(holdPanel, BorderLayout.NORTH);
		holdContainer.add(createControlsPanel(), BorderLayout.CENTER);
		add(holdContainer, BorderLayout.WEST);
		
		// Game board
		add(boardPanel, BorderLayout.CENTER);
		
		// Info panel
		JPanel infoPanel = new JPanel(new BorderLayout());
		infoPanel.add(nextPiecePanel, BorderLayout.NORTH);
		infoPanel.add(scorePanel, BorderLayout.CENTER);
		infoPanel.add(settingsPanel, BorderLayout.SOUTH);
		add(infoPanel, BorderLayout.EAST);
		
		// Menu
		add(menuPanel, BorderLayout.SOUTH);
		
		FrameUtils.setIcon(this, "/images/game-icon.png");
		setTitle("Tetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		int boardPanelWidth = GridPainter.SQUARE_SIDE_LENGTH * gameModel.getBoard().getHorizontalDimension();
		int boardPanelHeight = GridPainter.SQUARE_SIDE_LENGTH * gameModel.getBoard().getVerticalDimension();
		setSize(boardPanelWidth + (INFO_PANEL_WIDTH * 2), boardPanelHeight);
		setResizable(false); // I don't want to mess with trying to make this work right
		
		setLocationRelativeTo(null);
	}
	
	// Creates the controls panel. Basically just a bunch of JLabels
	private JPanel createControlsPanel() {
		
		JPanel keyCombos = new JPanel(new GridLayout(15,1));
		for (String keyCombo : new String[]{
				" Up: ",
				" 'F': ",
				" Down: ",
				" Left: ",
				" Right: ",
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
	
	private class BoardPanel extends GridPainter {

		private static final int SPIRAL_SLEEP_INTERVAL = 8;
		private static final int CLEAR_SLEEP_INTERVAL = 82;
		
		BoardPanel() {
			
			super(gameModel.getBoard().getHorizontalDimension(), gameModel.getBoard().getVerticalDimension());
			
			// Piece movement listener is added once start button is clicked
			
			setFocusable(true);
			setBorder(TetrisFrame.LINE_BORDER);
		}
			
		void enablePieceMovementInput() {
			addKeyListener(pieceMovementKeyHandler);
		}
			
		void disablePieceMovementInput() {
			removeKeyListener(pieceMovementKeyHandler);
		}
			
		Object spiralClear() {
			
			List<Block.ColoredSquare> spiralSquares = getSpiralSquares();
			
			try {
			
				// Run 1 loop to paint in all unoccupied squares
				for (Block.ColoredSquare spiralSquare : spiralSquares) {
					if (!gameModel.getBoard().isOpenSquare(spiralSquare.getRow(), spiralSquare.getCol())) {
						gameModel.getBoard().setSquare(spiralSquare.getRow(), spiralSquare.getCol(), spiralSquare.getColor());
					}
					repaint();
					Thread.sleep(SPIRAL_SLEEP_INTERVAL);
				}
				
				// Run a second loop to erase all of them
				for (Block.ColoredSquare spiralSquare : spiralSquares) {
					gameModel.getBoard().clearSquare(spiralSquare.getRow(), spiralSquare.getCol());
					repaint();
					Thread.sleep(SPIRAL_SLEEP_INTERVAL);
				}
				
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} 
			
			return null;
		};
			
		Object jumpClear() {
			
			try {
				
				// Fill all rows bottom to top. Make sure to account for invisible rows at the top of the board
				for (int row = gameModel.getBoard().getVerticalDimension() + 2; row >= 3; row --) {
					for (int col = 0; col < gameModel.getBoard().getHorizontalDimension(); col++) {
						if (gameModel.getBoard().isOpenSquare(row, col)) {
							gameModel.getBoard().setSquare(row, col, BlockConveyor.getRandomColor());
						}
					}
					repaint();
					Thread.sleep(CLEAR_SLEEP_INTERVAL);
				}
				
				// Clear all rows top to bottom. Again, account for invisible rows at top
				for (int row = 3; row <= gameModel.getBoard().getVerticalDimension() + 2; row ++) {
					for (int col = 0; col < gameModel.getBoard().getHorizontalDimension(); col++) {
						gameModel.getBoard().clearSquare(row, col);
					}
					repaint();
					Thread.sleep(CLEAR_SLEEP_INTERVAL);
				}
				
			} catch (InterruptedException e) {
				
			}
			
			return null;
		};
		
		/**
		 *  Builds the list of spiral squares. Squares are in order from the top left corner spiraling inwards, CCW
		 */
		private List<Block.ColoredSquare> getSpiralSquares() {
			
			List<Block.ColoredSquare> squares = new ArrayList<>();
			
			// Stores indices of next column / row that needs to be processed
			int nextLeftCol = 0,
				nextBottomRow = gameModel.getBoard().getVerticalDimension() + 2, // Account for 3 invisible rows at top of board
				nextRightCol = gameModel.getBoard().getHorizontalDimension() - 1,
				nextTopRow = 3;
			
			// Total squares is equal to the dimensions of the visible panels.
			// Loop until the size of squares reaches this amount
			int maxSquares = gameModel.getBoard().getVerticalDimension() * gameModel.getBoard().getHorizontalDimension();
			while (squares.size() < maxSquares) {
				
				// Get all cells in the next leftmost column
				for (int row = nextTopRow; row <= nextBottomRow; row++) {
					squares.add(new Block.ColoredSquare(row, nextLeftCol));
				}
				
				// Leftmost column has been processed
				nextLeftCol++;
				
				// Get all cells in the next bottom row
				for (int col = nextLeftCol; col <= nextRightCol; col++) {
					squares.add(new Block.ColoredSquare(nextBottomRow, col));
				}
				
				// Bottom row has been processed
				nextBottomRow--;
				
				// Get all cells in the next rightmost column
				for (int row = nextBottomRow; row >= nextTopRow; row--) {
					squares.add(new Block.ColoredSquare(row, nextRightCol));
				}
				
				// Rightmost column has been processed
				nextRightCol--;
				
				// Get all cells in the next top row
				for (int col = nextRightCol; col >= nextLeftCol; col--) {
					squares.add(new Block.ColoredSquare(nextTopRow, col));
				}
				
				// Top row has been processed
				nextTopRow++;
			}
			
			return squares;
			
		}

	}
	
	private class ScorePanel extends JPanel {
		
		private JLabel lblScore = new JLabel("Score: 0", JLabel.CENTER);
		private JLabel lblTotalLines = new JLabel("Lines: 0", JLabel.CENTER);
		private JLabel lblLevel = new JLabel("Level: 1", JLabel.CENTER);
		private JLabel lblTime = new JLabel("Time: 00:00", JLabel.CENTER);
		
		private ProgressBar progressBarLinesCleared = new ProgressBar(11, Color.GREEN) {
			public double getCurrentPercentage() {
				return ((double) gameModel.getScoreModel().getCurrentLevelLinesCleared()) /
						gameModel.getScoreModel().getLinesPerLevel();
			}
		};
		
		private ProgressBar progressBarTime = new ProgressBar(11, Color.YELLOW) {
			public double getCurrentPercentage() {
				return 0; // TODO
			}
		};
		
		private GridLayout layout;
		
		ScorePanel() {
			
			setBorder(new TitledBorder("Scoring Info"));
			layout = new GridLayout(6,1);
			setLayout(layout);
			
			for (JLabel l : Arrays.asList(lblScore, lblTotalLines, lblLevel, lblTime))
				l.setFont(TetrisFrame.LABEL_FONT);
			
			add(lblScore);
			add(lblLevel);
			add(lblTotalLines);
			add(FrameUtils.nestInPanel(progressBarLinesCleared));
			add(lblTime);
			
			add(FrameUtils.nestInPanel(progressBarTime));
			progressBarTime.setVisible(settingsPanel.timeAttackOn());
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			lblScore.setText("Score: " + gameModel.getScoreModel().getScore());
			lblTotalLines.setText("Lines: " + gameModel.getScoreModel().getCurrentLevelLinesCleared() + " / " + gameModel.getScoreModel().getLinesPerLevel());
			progressBarLinesCleared.repaint();
			progressBarTime.repaint();
			lblLevel.setText("Level: " + gameModel.getScoreModel().getLevel());
		}

		void showProgressBar() {
			progressBarTime.setVisible(true);
		}
		
		void hideProgressBar() {
			progressBarTime.setVisible(false);
		}
		
		Object flashLevel() {
			return flash(lblLevel.getText(), Color.YELLOW);
		}
		
		Object flashWin() {
			return flash("You Win!!!", Color.YELLOW);
		}
		
		Object flashGameOver() {
			return flash("Game Over!!!", Color.YELLOW);
		}
		
		Object flash(String textToFlash, Color flashColor) {
				
			lblLevel.setText(textToFlash);
			
			try {
				for (int i = 1; i <= 60; i++) {
					lblLevel.setForeground(i % 2 == 0 ? Color.BLACK : flashColor);
					Thread.sleep(50);
				}
			}
			catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
				
			return null;
		}
		
	}
	
	private class SettingsPanel extends JPanel {
		
		private JCheckBox cbxGhostSquares = new JCheckBox("Ghost Squares", true);
		private JCheckBox cbxMusic = new JCheckBox("Music", true);
		private JCheckBox cbxSoundEffects = new JCheckBox("Sound Effects", true);
		private JCheckBox cbxSaveScores = new JCheckBox("Save Scores", true);
		private JCheckBox cbxTimeAttack = new JCheckBox("Time Attack Mode", false);
		
		private JComboBox<String> lstDifficulty = new JComboBox<String>(new String[]{"Easy", "Medium", "Hard"});
		
		private TetrisButton btnChooseSpecials = new TetrisButton("Special Pieces...");
		private TetrisButton btnChooseBlockStyles = new TetrisButton("Block Styles...");
		private TetrisButton btnDBConfig = new TetrisButton("Database Connectivity...");
		
		private ItemListener ghostSquaresListener = e -> boardPanel.repaint();
		
		private ItemListener musicListener = e -> {
			if (cbxMusic.isSelected()) {
				gameModel.getAudioManager().resumeSoundtrack(gameModel.getScoreModel().getLevel());
			}
			else {
				gameModel.getAudioManager().stopSoundtrack(gameModel.getScoreModel().getLevel());
			}
		};
		
		SettingsPanel() {
			
			setLayout(new BorderLayout());
			setBorder(new TitledBorder("Settings"));
			
			List<JCheckBox> checkboxes = Arrays.asList(cbxGhostSquares, cbxMusic, cbxSoundEffects, cbxSaveScores, cbxTimeAttack);
			JPanel checkboxPanel = new JPanel(new GridLayout(checkboxes.size(), 1));
			checkboxes.forEach(cbx -> {
				checkboxPanel.add(cbx);
				cbx.setFocusable(false);
			});
			
			cbxTimeAttack.setToolTipText("When on, grants a bonus per level cleared: " +
					"+" + ScoreModel.getTimeAttackBonusPoints(0) + " points on easy, " +
					"+" + ScoreModel.getTimeAttackBonusPoints(1) + " points on medium, " +
					"+" + ScoreModel.getTimeAttackBonusPoints(2) + " points on hard");
			
			JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			diffPanel.add(new JLabel("Difficulty:  "));
			diffPanel.add(lstDifficulty);
			
			JPanel buttonContainer = new JPanel(new GridLayout(3,1));
			buttonContainer.add(btnChooseSpecials);
			buttonContainer.add(btnChooseBlockStyles);
			buttonContainer.add(btnDBConfig);
			
			btnChooseSpecials.addActionListener(e -> new SpecialPiecesFrame());
			btnChooseBlockStyles.addActionListener(e -> new BlockStylesFrame());
			btnDBConfig.addActionListener(e -> new DBSettingsFrame());
			
			add(checkboxPanel, BorderLayout.NORTH);
			add(diffPanel, BorderLayout.CENTER);
			add(buttonContainer, BorderLayout.SOUTH);
		}
		
		public boolean ghostSquaresOn() {
			return cbxGhostSquares.isSelected();
		}
		
		public boolean musicOn() {
			return cbxMusic.isSelected();
		}
		
		public boolean effectsOn() {
			return cbxSoundEffects.isSelected();
		}
		
		public boolean saveScoreOn() {
			return cbxSaveScores.isSelected();
		}
		
		public boolean timeAttackOn() {
			return cbxTimeAttack.isSelected();
		}
		
		public int getDifficulty() {
			return lstDifficulty.getSelectedIndex();
		}
		
		void enableMusicCbxListener() {
			cbxMusic.addItemListener(musicListener);
		}
		
		void disableMusicCbxListener() {
			cbxMusic.removeItemListener(musicListener);
		}
		
		void enableCbxListeners() {
			cbxGhostSquares.addItemListener(ghostSquaresListener);
			enableMusicCbxListener();
		}
		
		void disableCbxListeners() {
			cbxGhostSquares.removeItemListener(ghostSquaresListener);
			disableMusicCbxListener();
		}
		
		void enableDifficultyList() {
			lstDifficulty.setEnabled(true);
		}
		
		void enableSpecialPiecesButton() {
			btnChooseSpecials.setEnabled(true);
		}
		
		void enableBlockStylesButton() {
			btnChooseBlockStyles.setEnabled(true);
		}
		
		void enableDatabaseConnectivityButton() {
			btnDBConfig.setEnabled(true);
		}
		
		void enableTimeAttackCheckbox() {
			cbxTimeAttack.setEnabled(true);
		}
		
		void disableDifficultyList() {
			lstDifficulty.setEnabled(false);
		}
		
		void disableSpecialPiecesButton() {
			btnChooseSpecials.setEnabled(false);
		}
		
		void disableBlockStylesButton() {
			btnChooseBlockStyles.setEnabled(false);
		}
		
		void disableDatabaseConnectivityButton() {
			btnDBConfig.setEnabled(false);
		}
		
		void disableTimeAttackCheckbox() {
			cbxTimeAttack.setEnabled(false);
		}
		
	}
	
	private class MenuPanel extends JPanel {
		
		TetrisButton btnStart = new TetrisButton("Start");
		TetrisButton btnPause = new TetrisButton("Pause");
		TetrisButton btnResume = new TetrisButton("Resume");
		TetrisButton btnGiveUp = new TetrisButton("Give Up");
		TetrisButton btnHighScores = new TetrisButton("High Scores");
		
		private ActionListener startListener = e -> onStart.run();
		private ActionListener pauseListener = e -> onPause.run();
		private ActionListener resumeListener = e -> onResume.run();
		private ActionListener highScoresListener = e -> new HighScoreFrame();
		private ActionListener giveUpListener = e -> onGiveUp.get();
		
		public void enableStartButton() {
			btnStart.addActionListener(startListener);
		}
		
		public void enablePauseButton() {
			btnPause.addActionListener(pauseListener);
		}
		
		public void enableResumeButton() {
			btnResume.addActionListener(resumeListener);
		}
		
		public void enableGiveUpButton() {
			btnGiveUp.addActionListener(giveUpListener);
		}
		
		public void enableHighScoresButton() {
			btnHighScores.addActionListener(highScoresListener);
		}
		
		public void disableStartButton() {
			btnStart.removeActionListener(startListener);
		}
		
		public void disablePauseButton() {
			btnPause.removeActionListener(pauseListener);
		}
		
		public void disableResumeButton() {
			btnResume.removeActionListener(resumeListener);
		}
		
		public void disableGiveUpButton() {
			btnGiveUp.removeActionListener(giveUpListener);
		}
		
		public void disableHighScoresButton() {
			btnHighScores.removeActionListener(highScoresListener);
		}
		
		MenuPanel() {
			
			setLayout(new FlowLayout());
			
			Map<TetrisButton, Character> mnemonicMap = new HashMap<TetrisButton, Character>();
			mnemonicMap.put(btnStart, 's');
			mnemonicMap.put(btnPause, 'p');
			mnemonicMap.put(btnResume, 'r');
			mnemonicMap.put(btnGiveUp, 'g');
			mnemonicMap.put(btnHighScores, 'h');
			
			for (TetrisButton b : Arrays.asList(btnStart, btnPause, btnResume, btnGiveUp, btnHighScores)) {
				b.setMnemonic(mnemonicMap.get(b));
				add(b);
			}
			
			enableStartButton();
			enableHighScoresButton();
			
		}
		
	}
	
}
