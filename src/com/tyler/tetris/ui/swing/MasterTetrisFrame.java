package com.tyler.tetris.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.tyler.tetris.Block;
import com.tyler.tetris.Block.ColoredSquare;
import com.tyler.tetris.BlockConveyor;
import com.tyler.tetris.BlockType;
import com.tyler.tetris.Difficulty;
import com.tyler.tetris.ScoreKeeper;
import com.tyler.tetris.TetrisAudioSystem;
import com.tyler.tetris.TetrisBoard;
import com.tyler.tetris.ui.swing.widget.FlashLabel;
import com.tyler.tetris.ui.swing.widget.ProgressBar;
import com.tyler.tetris.ui.swing.widget.TetrisButton;

public class MasterTetrisFrame extends JFrame {
	
	public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);
	public static final Border BEVEL_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 17);
	static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
	
	private TetrisAudioSystem audioSystem;
	private TetrisBoard board;
	private ScoreKeeper scoreKeeper;
	private BlockConveyor conveyor;
	
	private BoardPanel boardPanel;
	private BlockDisplayPanel nextBlockPanel;
	private BlockDisplayPanel holdPanel;
	private MenuPanel menuPanel;
	private SettingsPanel settingsPanel;
	private ScorePanel scorePanel;
	
	// Tracks progress of Asynchronous UI effects
	private Future<?> clearTask;
	private Future<?> flashLabelTask;
	
	private Timer fallTimer = new Timer(0, e -> onFallTick());
	
	private KeyAdapter keyHandler = new KeyAdapter() {
		
		Set<Integer> pressed = new HashSet<>();
		
		public void keyPressed(KeyEvent e) {
			
			int code = e.getKeyCode();
			pressed.add(code);
			
			switch (code) {
				
			case KeyEvent.VK_LEFT:
				
				if (pressed.contains(KeyEvent.VK_S)) {
					board.ssActiveBlockLeft();
					audioSystem.playSuperslideSound();
				}
				else {
					board.moveActiveBlockLeft();
				}
				
				break;
				
			case KeyEvent.VK_RIGHT:
				
				if (pressed.contains(KeyEvent.VK_S)) {
					board.ssActiveBlockRight();
					audioSystem.playSuperslideSound();
				}
				else {
					board.moveActiveBlockRight();
				}
				
				break;
				
			case KeyEvent.VK_DOWN:
				
				board.moveActiveBlockDown();
				break;
				
			case KeyEvent.VK_UP:
					
				if (board.rotateActiveBlockCW()) {
					audioSystem.playCWRotationSound();
				}
				break;
				
			case KeyEvent.VK_F:
				
				if (board.rotateActiveBlockCCW()) {
					audioSystem.playCCWRotationSound();
				}
				break;
			
			case KeyEvent.VK_D: // Hold set
				
				Block activeBlock = board.getActiveBlock();
				
				if (!board.getHoldBlock().isPresent() && !activeBlock.isHoldBlock()) {
					activeBlock.tagAsHoldBlock();
					audioSystem.playHoldSound();
					board.setHoldBlock(activeBlock);
					Block nextBlock = conveyor.next();
					board.spawn(nextBlock);
				}
				
				break;
			
			case KeyEvent.VK_E: // Hold release
				
				if (board.getHoldBlock().isPresent()) {
					Block heldPiece = board.getHoldBlock().get();
					board.spawn(heldPiece);
					board.clearHoldBlock();
					audioSystem.playReleaseSound();
				}
				
				break;
				
			case KeyEvent.VK_SPACE:
				
				board.dropCurrentBlock();
				audioSystem.playPiecePlacementSound();
				onFallTick();
				break;
			}
			
			repaint();
		}
		
		public void keyReleased(KeyEvent e) {
			pressed.remove(e.getKeyCode());
		}
		
	};
	
	MasterTetrisFrame() {
		
		// Model classes
		this.fallTimer.setInitialDelay(0);
		this.audioSystem = new TetrisAudioSystem();
		this.board = new TetrisBoard();
		this.scoreKeeper = new ScoreKeeper();
		this.conveyor = new BlockConveyor();
		
		this.board.subscribe("blockPlaced", event -> {
			if (!board.spawn(conveyor.next())) {
				onGameOver();
			}
		});
		
		this.board.subscribe("linesCleared", event -> {
			int lines = (int) event;
			audioSystem.playClearLineSound(lines);
			scoreKeeper.increaseScore(lines);
			scorePanel.lblTotalLines.repaint();
			scorePanel.progressBarLinesCleared.repaint();
		});
		
		this.scoreKeeper.subscribe("levelChanged", event -> {
			
			int newLevel = (int) event;
			
			scorePanel.lblLevel.setText("Level: " + newLevel);
			scorePanel.progressBarTime.repaint();
			
			audioSystem.startSoundtrack(newLevel);
			
			fallTimer.setDelay(scoreKeeper.getCurrentTimerDelay());
			
			if (newLevel > 1) {
				flashLabelTask = THREAD_POOL.submit(() -> scorePanel.lblLevel.flash(Color.YELLOW));
			}
		});
		
		this.scoreKeeper.subscribe("gameWon", event -> onWin());
		
		this.scoreKeeper.subscribe("timeAttackFail", e -> onGameOver());
		
		this.scoreKeeper.subscribe("gameTimeChanged", newTime -> {
			scorePanel.lblTime.repaint();
			scorePanel.progressBarTime.repaint();
		});
		
		this.scoreKeeper.subscribe("scoreChanged", score -> {
			scorePanel.lblScore.repaint();
		});
		
		this.boardPanel = new BoardPanel();
		
		this.nextBlockPanel = new BlockDisplayPanel("Next") {
			
			@Override
			public Collection<ColoredSquare> getCurrentColors() {
				if (conveyor.peek() != null) {
					return conveyor.peek().getNextPanelSquares();
				}
				else {
					return new ArrayList<>();
				}
			}
			
		};
		
		this.holdPanel = new BlockDisplayPanel("Hold") {
			
			@Override
			public Collection<ColoredSquare> getCurrentColors() {
				return board.getHoldBlock().isPresent() ?
						board.getHoldBlock().get().getNextPanelSquares() :
						new ArrayList<>();
			}
			
		};
		
		this.menuPanel = new MenuPanel();
		this.settingsPanel = new SettingsPanel();
		this.scorePanel = new ScorePanel();
		
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
		
		// Hold Panel
		JPanel holdContainer = new JPanel(new BorderLayout());
		holdContainer.add(holdPanel, BorderLayout.NORTH);
		holdContainer.add(controls, BorderLayout.CENTER);
		add(holdContainer, BorderLayout.WEST);
		
		// Game board
		add(boardPanel, BorderLayout.CENTER);
		
		// Info panel
		JPanel infoPanel = new JPanel(new BorderLayout());
		infoPanel.add(nextBlockPanel, BorderLayout.NORTH);
		infoPanel.add(scorePanel, BorderLayout.CENTER);
		infoPanel.add(settingsPanel, BorderLayout.SOUTH);
		add(infoPanel, BorderLayout.EAST);
		
		// Menu
		add(menuPanel, BorderLayout.SOUTH);
		
		SwingUtility.setIcon(this, "/images/game-icon.png");
		setTitle("Tetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		int masterWidth = (holdPanel.getColumns() * BlockDisplayPanel.DEFAULT_BLOCK_DIMENSION) +
		                  (boardPanel.getColumns() * BlockDisplayPanel.DEFAULT_BLOCK_DIMENSION) +
		                  (nextBlockPanel.getColumns() * BlockDisplayPanel.DEFAULT_BLOCK_DIMENSION);
		
		int masterHeight = BlockDisplayPanel.DEFAULT_BLOCK_DIMENSION * (boardPanel.getRows());
		
		setSize(masterWidth, masterHeight);
		setResizable(false); // I don't want to mess with trying to make this work right
		
		setLocationRelativeTo(null);
	}
	
	private void onFallTick() {
		board.tryFall();
		repaint();
	};
	
	private void onStart() {
		
		if (clearTask != null && !clearTask.isDone()) {
			clearTask.cancel(true);
		}
		if (flashLabelTask != null && !flashLabelTask.isDone()) {
			flashLabelTask.cancel(true);
		}
		
		settingsPanel.lstDifficulty.setEnabled(false);
		settingsPanel.btnChooseSpecials.setEnabled(false);
		settingsPanel.cbxTimeAttack.setEnabled(false);
		settingsPanel.cbxGhostSquares.setEnabled(false);
		settingsPanel.cbxMusic.setEnabled(false);
		settingsPanel.cbxSaveScores.setEnabled(false);
		settingsPanel.cbxSoundEffects.setEnabled(false);
		
		
		menuPanel.btnStart.setEnabled(false);
		menuPanel.btnPause.setEnabled(true);
		menuPanel.btnResume.setEnabled(false);
		menuPanel.btnGiveUp.setEnabled(true);
		menuPanel.btnHighScores.setEnabled(false);
		
		boardPanel.enableBlockMovement();
		
		// Reset old game data
		scoreKeeper.resetScoreInfo();
		board.clear();
		holdPanel.repaint();
		conveyor.refresh();
		
		scorePanel.lblTotalLines.repaint();
		board.spawn(conveyor.next());
		
		scoreKeeper.startTimer();
		fallTimer.start();
	};
	
	private void onPause() {
		
		fallTimer.stop();
		scoreKeeper.pauseTimer();
		
		settingsPanel.cbxGhostSquares.setEnabled(true);
		settingsPanel.cbxMusic.setEnabled(true);
		settingsPanel.cbxSoundEffects.setEnabled(true);
		settingsPanel.cbxSaveScores.setEnabled(true);
		
		audioSystem.stopSoundtrack(scoreKeeper.getLevel());
		audioSystem.playPauseSound();
		
		boardPanel.disableBlockMovement();
		
		menuPanel.btnResume.setEnabled(true);
		menuPanel.btnPause.setEnabled(false);
		menuPanel.btnGiveUp.setEnabled(true);
		menuPanel.btnHighScores.setEnabled(true);
	};
	
	private void onResume() {
		
		fallTimer.start();
		scoreKeeper.startTimer();
		
		settingsPanel.cbxGhostSquares.setEnabled(false);
		settingsPanel.cbxMusic.setEnabled(false);
		settingsPanel.cbxSoundEffects.setEnabled(false);
		settingsPanel.cbxSaveScores.setEnabled(false);
		
		audioSystem.resumeSoundtrack(scoreKeeper.getLevel());
		
		boardPanel.enableBlockMovement();
		
		menuPanel.btnResume.setEnabled(false);
		menuPanel.btnPause.setEnabled(true);
		menuPanel.btnGiveUp.setEnabled(true);
		menuPanel.btnHighScores.setEnabled(true);
	};

	private void onWin() {
		
		settingsPanel.lstDifficulty.setEnabled(true);
		settingsPanel.btnChooseSpecials.setEnabled(true);
		settingsPanel.cbxTimeAttack.setEnabled(true);
		settingsPanel.cbxGhostSquares.setEnabled(true);
		settingsPanel.cbxMusic.setEnabled(true);
		settingsPanel.cbxSaveScores.setEnabled(true);
		settingsPanel.cbxSoundEffects.setEnabled(true);
		
		menuPanel.btnStart.setEnabled(true);
		menuPanel.btnPause.setEnabled(false);
		menuPanel.btnResume.setEnabled(false);
		menuPanel.btnGiveUp.setEnabled(false);
		menuPanel.btnHighScores.setEnabled(true);
		
		fallTimer.stop();
		board.logActiveBlock();
		board.clearActiveBlock();
		scoreKeeper.pauseTimer();
		audioSystem.playVictoryFanfare();
		boardPanel.disableBlockMovement();
		clearTask = THREAD_POOL.submit(boardPanel::jumpClear);
		scorePanel.lblLevel.setText("You Win!!!");
		flashLabelTask = THREAD_POOL.submit(() -> scorePanel.lblLevel.flash(Color.YELLOW));
	}
	
	private void onGameOver() {
		
		fallTimer.stop();
		scoreKeeper.pauseTimer();
		
		board.logActiveBlock();
		board.clearActiveBlock();
		
		audioSystem.playGameOverSound();
		
		menuPanel.btnStart.setEnabled(true);
		menuPanel.btnPause.setEnabled(false);
		menuPanel.btnResume.setEnabled(false);
		menuPanel.btnGiveUp.setEnabled(false);
		menuPanel.btnHighScores.setEnabled(true);
		
		settingsPanel.cbxGhostSquares.setEnabled(true);
		settingsPanel.cbxMusic.setEnabled(true);
		settingsPanel.cbxSoundEffects.setEnabled(true);
		settingsPanel.cbxSaveScores.setEnabled(true);
		settingsPanel.cbxTimeAttack.setEnabled(true);
		settingsPanel.lstDifficulty.setEnabled(true);
		settingsPanel.btnChooseSpecials.setEnabled(true);
		
		boardPanel.disableBlockMovement();
		
		scorePanel.lblLevel.setText("Game Over!!!");
		flashLabelTask = THREAD_POOL.submit(() -> scorePanel.lblLevel.flash(Color.RED));
		
		clearTask = THREAD_POOL.submit(boardPanel::spiralClear);
	};
	
	private class BoardPanel extends PixelGrid {

		private static final int SPIRAL_SLEEP_INTERVAL = 8;
		private static final int CLEAR_SLEEP_INTERVAL = 82;
		
		BoardPanel() {
			super(board.getVerticalDimension() - 3, board.getHorizontalDimension(), BlockDisplayPanel.DEFAULT_BLOCK_DIMENSION);
			setFocusable(true);
			setBorder(LINE_BORDER);
		}
			
		void enableBlockMovement() {
			addKeyListener(keyHandler);
		}
			
		void disableBlockMovement() {
			removeKeyListener(keyHandler);
		}
			
		void spiralClear() {
			
			try {
				
				Collection<ColoredSquare> spiralSquares = new LinkedHashSet<>();
				
				int nextLeftCol = 0,
				    nextRightCol = board.getHorizontalDimension() - 1,
				    nextTopRow = 0,
				    nextBottomRow = board.getVerticalDimension() - 1;
				
				// Total squares is equal to the dimensions of the visible panels. Loop until the size of squares reaches this amount
				int maxSquares = board.getVerticalDimension() * board.getHorizontalDimension();
				while (spiralSquares.size() < maxSquares) {
					
					// Get all cells in the next leftmost column
					for (int row = nextTopRow; row <= nextBottomRow; row++) {
						spiralSquares.add(new ColoredSquare(row, nextLeftCol));
					}
					nextLeftCol++;
					
					// Get all cells in the next bottom row
					for (int col = nextLeftCol; col <= nextRightCol; col++) {
						spiralSquares.add(new ColoredSquare(nextBottomRow, col));
					}
					nextBottomRow--;
					
					// Get all cells in the next rightmost column
					for (int row = nextBottomRow; row >= nextTopRow; row--) {
						spiralSquares.add(new ColoredSquare(row, nextRightCol));
					}
					nextRightCol--;
					
					// Get all cells in the next top row
					for (int col = nextRightCol; col >= nextLeftCol; col--) {
						spiralSquares.add(new ColoredSquare(nextTopRow, col));
					}
					nextTopRow++;
				}
				
				// Run 1 loop to paint in all unoccupied squares
				for (ColoredSquare spiralSquare : spiralSquares) {
					if (spiralSquare.getRow() < 3) continue;
					if (board.isOpen(spiralSquare.getRow(), spiralSquare.getColumn())) {
						board.setColor(spiralSquare.getRow(), spiralSquare.getColumn(), spiralSquare.getColor());
					}
					repaint();
					Thread.sleep(SPIRAL_SLEEP_INTERVAL);
				}
				
				// Run a second loop to erase all of them
				for (ColoredSquare spiralSquare : spiralSquares) {
					if (spiralSquare.getRow() < 3) continue;
					board.clearSquare(spiralSquare.getRow(), spiralSquare.getColumn());
					repaint();
					Thread.sleep(SPIRAL_SLEEP_INTERVAL);
				}
				
			} catch (InterruptedException e) {
				return; // Will happen if new game is started before spiral clear is finished
			} 
			
		};
			
		void jumpClear() {
			
			try {
				
				// Fill all rows bottom to top
				for (int row = board.getVerticalDimension() - 1; row >= 3; row --) {
					for (int col = 0; col < board.getHorizontalDimension(); col++) {
						if (board.isOpen(row, col)) {
							board.setColor(row, col, BlockType.getRandomColor());
						}
					}
					repaint();
					Thread.sleep(CLEAR_SLEEP_INTERVAL);
				}
				
				// Clear all rows top to bottom.
				for (int row = 3; row < board.getVerticalDimension(); row ++) {
					for (int col = 0; col < board.getHorizontalDimension(); col++) {
						board.clearSquare(row, col);
					}
					repaint();
					Thread.sleep(CLEAR_SLEEP_INTERVAL);
				}
				
			} catch (InterruptedException e) {
				return; // Will happen if we start a new game before task is done
			}
			
		};
		
		@Override
		public Collection<ColoredSquare> getCurrentColors() {
			return board.getColoredSquares();
		}
		
		@Override
		protected int getYCoordinate(Block.ColoredSquare sq) {
			return (sq.getRow() - 3) * getUnitHeight(); // Adjusts for 3 invisible squares at top
		}
		
	}
	
	private class ScorePanel extends JPanel {
		
		private JLabel lblScore = new JLabel("Score: 0", JLabel.CENTER) {
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				setText("Score: " + scoreKeeper.getScore());
			}
			
		};
		
		private JLabel lblTotalLines = new JLabel("Lines: 0 / " + scoreKeeper.getLinesPerLevel(), JLabel.CENTER) {
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				setText("Lines: " + scoreKeeper.getCurrentLevelLinesCleared() + " / " + scoreKeeper.getLinesPerLevel());
			}
			
		};
		
		private FlashLabel lblLevel = new FlashLabel("Level: 1", JLabel.CENTER);
		
		private JLabel lblTime = new JLabel("Time: 00:00", JLabel.CENTER) {
		
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				String timeLabel = "Time: " + formatSeconds(scoreKeeper.getGameTime());
				if (scoreKeeper.isTimeAttack()) {
					timeLabel += " / " + formatSeconds(scoreKeeper.getCurrentTimeAttackLimit()); 
				}
				setText(timeLabel);
			}
		
		};
		
		private ProgressBar progressBarLinesCleared = new ProgressBar(11, Color.GREEN) {

			@Override
			protected double getCurrentPercentage() {
				return scoreKeeper.getLinesClearedPercentage();
			}
			
		};
		private ProgressBar progressBarTime = new ProgressBar(11, Color.YELLOW) {
			
			@Override
			protected double getCurrentPercentage() {
				
				int currentTime = scoreKeeper.getGameTime();
				int maxLimit = scoreKeeper.getCurrentTimeAttackLimit();
				double percentage = 100.0 * currentTime / maxLimit;
				
				int timeTillMax = maxLimit - currentTime;
				if (timeTillMax <= 10) {
					this.barColor = Color.RED;
				}
				else if (timeTillMax <= 20) {
					this.barColor = Color.YELLOW;
				}
				else {
					this.barColor = Color.GREEN;
				}
				
				return percentage;
			}
			
		};
		
		private GridLayout layout;
		
		ScorePanel() {
			
			setBorder(new TitledBorder("Score"));
			layout = new GridLayout(6,1);
			setLayout(layout);
			
			for (JLabel l : Arrays.asList(lblScore, lblTotalLines, lblLevel, lblTime))
				l.setFont(MasterTetrisFrame.LABEL_FONT);
			
			add(lblScore);
			add(lblLevel);
			add(lblTotalLines);
			add(SwingUtility.nestInPanel(progressBarLinesCleared));
			add(lblTime);
			
			progressBarTime.setVisible(false);
			add(SwingUtility.nestInPanel(progressBarTime));
		}
		
	}
	
	private class SettingsPanel extends JPanel {
		
		private JCheckBox cbxGhostSquares;
		private JCheckBox cbxMusic;
		private JCheckBox cbxSoundEffects;
		private JCheckBox cbxSaveScores;
		private JCheckBox cbxTimeAttack;
		
		private JComboBox<Difficulty> lstDifficulty;
		
		private TetrisButton btnChooseSpecials;
		
		SettingsPanel() {
			
			cbxMusic = new JCheckBox("Music", true);
			cbxMusic.addItemListener(e -> audioSystem.setSoundtrackMuted(!cbxMusic.isSelected()));
			
			cbxSoundEffects = new JCheckBox("Sound Effects", true);
			cbxSoundEffects.addItemListener(e -> audioSystem.setEffectsMuted(!cbxSoundEffects.isSelected()));
			
			cbxSaveScores = new JCheckBox("Save Scores", true);
			
			cbxGhostSquares = new JCheckBox("Ghost Squares", true);
			cbxGhostSquares.addItemListener(e -> {
				board.setGhostSquaresEnabled(cbxGhostSquares.isSelected());
				boardPanel.repaint();
			});
			
			cbxTimeAttack = new JCheckBox("Time Attack Mode", false);
			cbxTimeAttack.addItemListener(e -> {
				scoreKeeper.setTimeAttack(cbxTimeAttack.isSelected());
				scorePanel.progressBarTime.setVisible(cbxTimeAttack.isSelected());
			});
			cbxTimeAttack.setToolTipText("When on, grants a bonus per level cleared: " +
					"+" + Difficulty.EASY.getTimeAttackBonus() + " points on easy, " +
					"+" + Difficulty.MEDIUM.getTimeAttackBonus() + " points on medium, " +
					"+" + Difficulty.HARD.getTimeAttackBonus() + " points on hard");
			
			lstDifficulty = new JComboBox<Difficulty>(Difficulty.values());
			lstDifficulty.addActionListener(e -> {
				scoreKeeper.setDifficulty(getSelectedDifficulty());
				conveyor.setDifficulty(getSelectedDifficulty());
				fallTimer.setDelay(scoreKeeper.getCurrentTimerDelay());
			});
			lstDifficulty.setSelectedIndex(0);
			
			btnChooseSpecials = new TetrisButton("Special Pieces...");
			btnChooseSpecials.addActionListener(e -> new SpecialPiecesFrame());
			
			setLayout(new BorderLayout());
			setBorder(new TitledBorder("Settings"));
			
			List<JCheckBox> checkboxes = Arrays.asList(cbxGhostSquares, cbxMusic, cbxSoundEffects,  cbxTimeAttack); //,cbxSaveScores;
			JPanel checkboxPanel = new JPanel(new GridLayout(checkboxes.size(), 1));
			checkboxes.forEach(cbx -> {
				checkboxPanel.add(cbx);
				cbx.setFocusable(false);
			});
			
			JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			diffPanel.add(new JLabel("Difficulty:  "));
			diffPanel.add(lstDifficulty);
			
			add(checkboxPanel, BorderLayout.NORTH);
			add(diffPanel, BorderLayout.CENTER);
			add(btnChooseSpecials, BorderLayout.SOUTH);
		}

		public Difficulty getSelectedDifficulty() {
			return (Difficulty) lstDifficulty.getSelectedItem();
		}
		
	}
	
	private class MenuPanel extends JPanel {
		
		TetrisButton btnStart = new TetrisButton("Start");
		TetrisButton btnPause = new TetrisButton("Pause");
		TetrisButton btnResume = new TetrisButton("Resume");
		TetrisButton btnGiveUp = new TetrisButton("Give Up");
		TetrisButton btnHighScores = new TetrisButton("High Scores");
		
		private MenuPanel() {
			
			setLayout(new FlowLayout());
			
			Map<TetrisButton, Character> mnemonicMap = new HashMap<TetrisButton, Character>();
			mnemonicMap.put(btnStart, 's');
			mnemonicMap.put(btnPause, 'p');
			mnemonicMap.put(btnResume, 'r');
			mnemonicMap.put(btnGiveUp, 'g');
			mnemonicMap.put(btnHighScores, 'h');
			
			for (TetrisButton b : Arrays.asList(btnStart, btnPause, btnResume, btnGiveUp)) {//, btnHighScores)) {
				b.setMnemonic(mnemonicMap.get(b));
				add(b);
			}
			
			btnStart.setEnabled(true);
			btnStart.addActionListener(e -> onStart());
			
			btnPause.setEnabled(false);
			btnPause.addActionListener(e -> onPause());
			
			btnResume.setEnabled(false);
			btnResume.addActionListener(e -> onResume());
			
			btnHighScores.setEnabled(true);
			btnHighScores.addActionListener(e -> new HighScoreFrame());
			
			btnGiveUp.setEnabled(false);
			btnGiveUp.addActionListener(e -> onGameOver());
			
		}
		
	}
	
	private class SpecialPiecesFrame extends JFrame {
		
		private TetrisButton btnClose;
		
		public SpecialPiecesFrame() { 
			
			this.btnClose = new TetrisButton("Close");
			this.btnClose.addActionListener(e -> dispose());
			
			JPanel blockPanels = new JPanel(new GridLayout(1,3));
			for (BlockType blockType : BlockType.getSpecialBlocks()) {
				
				BlockDisplayPanel display = new BlockDisplayPanel("\"" + blockType + "\"", new Block(blockType));
				BlockSelectorButton selector = new BlockSelectorButton(blockType);
				
				JLabel pointBonus = new JLabel("+" + ScoreKeeper.getSpecialPieceBonusPoints(blockType) + " points per line");
				pointBonus.setHorizontalAlignment(SwingConstants.CENTER);
				pointBonus.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				
				JPanel menu = new JPanel(new BorderLayout());
				menu.add(selector, BorderLayout.NORTH);
				menu.add(pointBonus, BorderLayout.SOUTH);
				
				JPanel blockPanel = new JPanel(new BorderLayout());
				blockPanel.add(display, BorderLayout.CENTER);
				blockPanel.add(menu, BorderLayout.SOUTH);
				blockPanels.add(blockPanel);
			}
			
			add(blockPanels, BorderLayout.CENTER);
			add(SwingUtility.nestInPanel(btnClose), BorderLayout.SOUTH);
			
			SwingUtility.setIcon(this, "/images/star.png");
			setTitle("Special Pieces");
			setResizable(false);
			pack();
			setLocationRelativeTo(null);
			setVisible(true);
		}
		
		private class BlockSelectorButton extends JButton {
			
			private BlockType blockType;
			private boolean active;
			
			private BlockSelectorButton(BlockType blockType) {
				this.blockType = blockType;
				setActiveState(conveyor.isActive(blockType));
				setFocusable(false);
				addMouseMotionListener(new MouseAdapter() {
					public void mouseMoved(MouseEvent e) {
						setCursor(new Cursor(Cursor.HAND_CURSOR));
					}
				});
				addActionListener(e -> toggle());
				setPreferredSize(new Dimension(getWidth(), 28));
			}
			
			private void toggle() {
				setActiveState(!active);
				if (active) {
					conveyor.enableBlockType(settingsPanel.getSelectedDifficulty(), blockType);
				}
				else {
					conveyor.disableBlockType(blockType);
				}
			}
			
			public void setActiveState(boolean active) {
				this.active = active;
				setBackground(active ? Color.YELLOW : Color.LIGHT_GRAY);
				setText(active ? "Active" : "Inactive");
			}
			
		}
	}
	
	private static String formatSeconds(int seconds) {
		int totalMinutes = seconds / 60;
		int secondsLeftover = seconds % 60;
		return (totalMinutes < 10 ? "0" : "") + totalMinutes +
		       ":" +
		       (secondsLeftover < 10 ? "0" : "") + secondsLeftover;
	}
	
}
