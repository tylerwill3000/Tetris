package com.tyler.tetris.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import com.tyler.tetris.Block;
import com.tyler.tetris.Block.ColoredSquare;
import com.tyler.tetris.BlockConveyor;
import com.tyler.tetris.ScoreKeeper;
import com.tyler.tetris.TetrisGameModel;

/**
 * Master frame that holds all other components
 * @author Tyler
 */
public class MasterTetrisFrame extends JFrame {
	
	public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);
	public static final Border BEVEL_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	public static final Border ETCHED_BORDER = BorderFactory.createEtchedBorder();
	public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 17);
	public static final int INFO_PANEL_WIDTH = 35 * 5;
	public static final int INITIAL_TIMER_DELAY = 600;
	static final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	
	private BoardPanel boardPanel;
	private NextPiecePanel nextPiecePanel;
	private NextPiecePanel holdPanel;
	private MenuPanel menuPanel;
	private SettingsPanel settingsPanel;
	private ScorePanel scorePanel;
	private TetrisGameModel gameModel;
	private BlockConveyor conveyor;
	private boolean beforeFirstGame = true;
	private Timer fallTimer = new Timer(INITIAL_TIMER_DELAY, e -> onFallTick());
	
	private KeyAdapter keyHandler = new KeyAdapter() {
		
		Set<Integer> pressed = new HashSet<>();
		
		public void keyPressed(KeyEvent e) {
			
			int code = e.getKeyCode();
			pressed.add(code);
			
			switch (code) {
				
			case KeyEvent.VK_LEFT:
				
				if (pressed.contains(KeyEvent.VK_S)) { // Perform super-shift left if 's' is pressed
					gameModel.getBoard().ssActiveBlockLeft();
					gameModel.getAudioSystem().playSuperslideSound();
				}
				else {
					gameModel.getBoard().moveActiveBlockLeft();
				}
				
				break;
				
			case KeyEvent.VK_RIGHT:
				
				if (pressed.contains(KeyEvent.VK_S)) { // Perform super-shift right if 's' is pressed
					gameModel.getBoard().ssActiveBlockRight();
					gameModel.getAudioSystem().playSuperslideSound();
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
					gameModel.getAudioSystem().playCWRotationSound();
				}
				break;
				
			case KeyEvent.VK_F:
				
				if (gameModel.getBoard().rotateActiveBlockCCW()) {
					gameModel.getAudioSystem().playCCWRotationSound();
				}
				break;
			
			case KeyEvent.VK_D: // Hold set
				
				Block activeBlock = gameModel.getBoard().getActiveBlock();
				
				if (!gameModel.getBoard().getHoldBlock().isPresent() && !activeBlock.isHoldBlock()) {
					activeBlock.tagAsHoldBlock();
					gameModel.getAudioSystem().playHoldSound();
					gameModel.getBoard().setHoldBlock(activeBlock);
					Block nextBlock = conveyor.next();
					gameModel.getBoard().spawn(nextBlock);
				}
				
				break;
			
			case KeyEvent.VK_E: // Hold release
				
				if (gameModel.getBoard().getHoldBlock().isPresent()) {
					Block heldPiece = gameModel.getBoard().getHoldBlock().get();
					gameModel.getBoard().spawn(heldPiece);
					gameModel.getBoard().clearHoldBlock();
					gameModel.getAudioSystem().playReleaseSound();
				}
				
				break;
				
			case KeyEvent.VK_SPACE:
				
				gameModel.getBoard().dropCurrentBlock();
				gameModel.getAudioSystem().playPiecePlacementSound();
				onFallTick();
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
	
	MasterTetrisFrame() {
		
		this.fallTimer.setInitialDelay(0);
		this.gameModel = new TetrisGameModel();
		this.boardPanel = new BoardPanel();
		
		this.nextPiecePanel = new NextPiecePanel("Next Piece") {
			@Override
			public Collection<ColoredSquare> getCurrentColors() {
				if (beforeFirstGame) {
					return new ArrayList<>();
				}
				else {
					return conveyor.peek().getNextPanelSquares();
				}
			}
		};
		
		this.holdPanel = new NextPiecePanel("Hold") {
			@Override
			public Collection<ColoredSquare> getCurrentColors() {
				return gameModel.getBoard().getHoldBlock().isPresent() ?
						gameModel.getBoard().getHoldBlock().get().getNextPanelSquares() :
						new ArrayList<>();
			}
		};
		
		this.menuPanel = new MenuPanel();
		this.settingsPanel = new SettingsPanel();
		this.scorePanel = new ScorePanel();
		this.conveyor = new BlockConveyor();
		
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
		infoPanel.add(nextPiecePanel, BorderLayout.NORTH);
		infoPanel.add(scorePanel, BorderLayout.CENTER);
		infoPanel.add(settingsPanel, BorderLayout.SOUTH);
		add(infoPanel, BorderLayout.EAST);
		
		// Menu
		add(menuPanel, BorderLayout.SOUTH);
		
		FrameUtils.setIcon(this, "/images/game-icon.png");
		setTitle("Tetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		int boardPanelWidth = 35 * gameModel.getBoard().getHorizontalDimension();
		int boardPanelHeight = 35 * (gameModel.getBoard().getVerticalDimension() - 3);
		setSize(boardPanelWidth + (INFO_PANEL_WIDTH * 2), boardPanelHeight);
		setResizable(false); // I don't want to mess with trying to make this work right
		
		setLocationRelativeTo(null);
	}
	
	private void onFallTick() {
		Integer linesCleared = gameModel.getBoard().tryFall();
		boolean piecePlaced = linesCleared != null;
		if (piecePlaced) {
			if (linesCleared > 0) {
				gameModel.getAudioSystem().playClearLineSound(linesCleared);
				int currentLevel = gameModel.getScoreKeeper().getLevel();
				int newLevel = gameModel.getScoreKeeper().increaseScore(linesCleared);
				boolean levelUp = newLevel > currentLevel;
				
				if (levelUp) {
					gameModel.getAudioSystem().stopSoundtrack(currentLevel);
					if (newLevel == ScoreKeeper.MAX_LEVEL) {
						gameModel.getAudioSystem().playVictoryFanfare();
						List<Callable<Object>> tasks = Arrays.asList(boardPanel::spiralClear, scorePanel::flashWin);
						try {
							THREAD_EXECUTOR.invokeAll(tasks);
						}
						catch (Exception ex) {
							throw new RuntimeException(ex);
						}
					}
					else {
						gameModel.getAudioSystem().startSoundtrack(newLevel);
					}
				}
			}
			
			if (!gameModel.getBoard().spawn(conveyor.next())) {
				menuPanel.onGiveUp();
				return;
			}
			
		}
		
		boardPanel.repaint();
		nextPiecePanel.repaint();
		scorePanel.repaint();
	};
	
	private class BoardPanel extends PixelGrid {

		private static final int SPIRAL_SLEEP_INTERVAL = 8;
		private static final int CLEAR_SLEEP_INTERVAL = 82;
		
		BoardPanel() {
			super(gameModel.getBoard().getVerticalDimension() - 3, gameModel.getBoard().getHorizontalDimension());
			// Piece movement listener is added once start button is clicked
			setFocusable(true);
			setBorder(MasterTetrisFrame.LINE_BORDER);
		}
			
		void enablePieceMovementInput() {
			addKeyListener(keyHandler);
		}
			
		void disablePieceMovementInput() {
			removeKeyListener(keyHandler);
		}
			
		Object spiralClear() {
			
			List<Block.ColoredSquare> spiralSquares = gameModel.getBoard().getSpiralSquares();
			
			try {
			
				// Run 1 loop to paint in all unoccupied squares
				for (Block.ColoredSquare spiralSquare : spiralSquares) {
					if (spiralSquare.getRow() < 3) continue;
					if (gameModel.getBoard().isOpen(spiralSquare.getRow(), spiralSquare.getColumn())) {
						gameModel.getBoard().setColor(spiralSquare.getRow(), spiralSquare.getColumn(), spiralSquare.getColor());
					}
					repaint();
					Thread.sleep(SPIRAL_SLEEP_INTERVAL);
				}
				
				// Run a second loop to erase all of them
				List<Block.ColoredSquare> allSquares = new ArrayList<>();
				allSquares.addAll(spiralSquares);
				allSquares.addAll(getCurrentColors());
				
				for (Block.ColoredSquare spiralSquare : allSquares) {
					if (spiralSquare.getRow() < 3) continue;
					gameModel.getBoard().clearSquare(spiralSquare.getRow(), spiralSquare.getColumn());
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
				
				// Fill all rows bottom to top
				for (int row = gameModel.getBoard().getVerticalDimension() - 1; row >= 3; row --) {
					for (int col = 0; col < gameModel.getBoard().getHorizontalDimension(); col++) {
						if (gameModel.getBoard().isOpen(row, col)) {
							gameModel.getBoard().setColor(row, col, Color.RED);
						}
					}
					repaint();
					Thread.sleep(CLEAR_SLEEP_INTERVAL);
				}
				
				// Clear all rows top to bottom.
				for (int row = 0; row < gameModel.getBoard().getVerticalDimension(); row ++) {
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
		
		@Override
		public Collection<ColoredSquare> getCurrentColors() {
			return gameModel.getBoard().getColoredSquares();
		}
		
		@Override
		protected int getYCoordinate(Block.ColoredSquare sq) {
			return (sq.getRow() - 3) * getUnitHeight(); // Adjusts for 3 invisible squares at top
		}
		
	}
	
	private class ScorePanel extends JPanel {
		
		private JLabel lblScore = new JLabel("Score: 0", JLabel.CENTER);
		private JLabel lblTotalLines = new JLabel("Lines: 0", JLabel.CENTER);
		private JLabel lblLevel = new JLabel("Level: 1", JLabel.CENTER);
		private JLabel lblTime = new JLabel("Time: 00:00", JLabel.CENTER);
		
		private ProgressBar progressBarLinesCleared = new ProgressBar(11, Color.GREEN) {
			public double getCurrentPercentage() {
				return ((double) gameModel.getScoreKeeper().getCurrentLevelLinesCleared()) /
						gameModel.getScoreKeeper().getLinesPerLevel();
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
				l.setFont(MasterTetrisFrame.LABEL_FONT);
			
			add(lblScore);
			add(lblLevel);
			add(lblTotalLines);
			add(FrameUtils.nestInPanel(progressBarLinesCleared));
			add(lblTime);
			
			add(FrameUtils.nestInPanel(progressBarTime));
			progressBarTime.setVisible(settingsPanel.cbxTimeAttack.isSelected());
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			lblScore.setText("Score: " + gameModel.getScoreKeeper().getScore());
			lblTotalLines.setText("Lines: " + gameModel.getScoreKeeper().getCurrentLevelLinesCleared() + " / " + gameModel.getScoreKeeper().getLinesPerLevel());
			progressBarLinesCleared.repaint();
			progressBarTime.repaint();
			lblLevel.setText("Level: " + gameModel.getScoreKeeper().getLevel());
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
		
		SettingsPanel() {
			
			setLayout(new BorderLayout());
			setBorder(new TitledBorder("Settings"));
			
			cbxMusic.addItemListener(e -> gameModel.getAudioSystem().setSoundtrackMuted(!cbxMusic.isSelected()));
			cbxSoundEffects.addItemListener(e -> gameModel.getAudioSystem().setSoundtrackMuted(!cbxMusic.isSelected()));
			
			
			cbxGhostSquares.addItemListener(e -> {
				gameModel.getBoard().setGhostSquaresEnabled(cbxGhostSquares.isSelected());
				boardPanel.repaint();
			});
			
			List<JCheckBox> checkboxes = Arrays.asList(cbxGhostSquares, cbxMusic, cbxSoundEffects, cbxSaveScores, cbxTimeAttack);
			JPanel checkboxPanel = new JPanel(new GridLayout(checkboxes.size(), 1));
			checkboxes.forEach(cbx -> {
				checkboxPanel.add(cbx);
				cbx.setFocusable(false);
			});
			
			cbxTimeAttack.setToolTipText("When on, grants a bonus per level cleared: " +
					"+" + ScoreKeeper.getTimeAttackBonusPoints(0) + " points on easy, " +
					"+" + ScoreKeeper.getTimeAttackBonusPoints(1) + " points on medium, " +
					"+" + ScoreKeeper.getTimeAttackBonusPoints(2) + " points on hard");
			
			JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			diffPanel.add(new JLabel("Difficulty:  "));
			diffPanel.add(lstDifficulty);
			
			btnChooseSpecials.addActionListener(e -> new SpecialPiecesFrame());
			
			add(checkboxPanel, BorderLayout.NORTH);
			add(diffPanel, BorderLayout.CENTER);
			add(btnChooseSpecials, BorderLayout.SOUTH);
		}
		
		public int getDifficulty() {
			return lstDifficulty.getSelectedIndex();
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
			
			for (TetrisButton b : Arrays.asList(btnStart, btnPause, btnResume, btnGiveUp, btnHighScores)) {
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
			btnGiveUp.addActionListener(e -> onGiveUp());
		}
		
		private void onStart() {
			
			gameModel = new TetrisGameModel(
			                    settingsPanel.cbxTimeAttack.isSelected(),
			                    settingsPanel.cbxGhostSquares.isSelected(),
			                    settingsPanel.cbxSaveScores.isSelected(),
			                    settingsPanel.getDifficulty(),
			                    settingsPanel.cbxMusic.isSelected(),
			                    settingsPanel.cbxSoundEffects.isSelected());
			
			settingsPanel.lstDifficulty.setEnabled(false);
			settingsPanel.btnChooseSpecials.setEnabled(false);
			settingsPanel.cbxTimeAttack.setEnabled(false);
			settingsPanel.cbxGhostSquares.setEnabled(false);
			settingsPanel.cbxMusic.setEnabled(false);
			settingsPanel.cbxSaveScores.setEnabled(false);
			settingsPanel.cbxSoundEffects.setEnabled(false);
			
			btnStart.setEnabled(false);
			btnPause.setEnabled(true);
			btnResume.setEnabled(false);
			btnGiveUp.setEnabled(true);
			btnHighScores.setEnabled(false);
			
			boardPanel.enablePieceMovementInput();
			
			if (settingsPanel.cbxTimeAttack.isSelected()) {
				scorePanel.showProgressBar();
			}
			else {
				scorePanel.hideProgressBar();
			}
			
			gameModel.getBoard().spawn(conveyor.next());
			gameModel.getAudioSystem().startSoundtrack(1);
			fallTimer.start();
			beforeFirstGame = false;
		};
		
		private void onPause() {
			
			fallTimer.stop();
			
			settingsPanel.cbxGhostSquares.setEnabled(true);
			settingsPanel.cbxMusic.setEnabled(true);
			settingsPanel.cbxSoundEffects.setEnabled(true);
			settingsPanel.cbxSaveScores.setEnabled(true);
			
			gameModel.getAudioSystem().stopSoundtrack(gameModel.getScoreKeeper().getLevel());
			gameModel.getAudioSystem().playPauseSound();
			
			boardPanel.disablePieceMovementInput();
			
			menuPanel.btnResume.setEnabled(true);
			menuPanel.btnPause.setEnabled(false);
			menuPanel.btnGiveUp.setEnabled(false);
		};
		
		private void onResume() {
			
			fallTimer.start();
			
			settingsPanel.cbxGhostSquares.setEnabled(false);
			settingsPanel.cbxMusic.setEnabled(false);
			settingsPanel.cbxSoundEffects.setEnabled(false);
			settingsPanel.cbxSaveScores.setEnabled(false);
			
			gameModel.getAudioSystem().resumeSoundtrack(gameModel.getScoreKeeper().getLevel());
			
			boardPanel.enablePieceMovementInput();
			
			btnResume.setEnabled(false);
			btnPause.setEnabled(true);
			btnGiveUp.setEnabled(true);
			
		};
	
		private void onGiveUp() {
			fallTimer.stop();
			gameModel.getAudioSystem().stopSoundtrack(gameModel.getScoreKeeper().getLevel());
			gameModel.getAudioSystem().playGameOverSound();
			THREAD_EXECUTOR.submit(scorePanel::flashGameOver);
			boardPanel.spiralClear();
		};
		
	}
	
}
