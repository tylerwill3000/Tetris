package com.github.tylerwill.tetris.swing;

import com.github.tylerwill.tetris.*;
import com.github.tylerwill.tetris.event.TetrisEvent;
import com.github.tylerwill.tetris.score.FlatFileScoreDao;
import com.github.tylerwill.tetris.score.ScoreDao;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MasterTetrisFrame extends JFrame {

  // Prevents tooltips from disappearing while mouse is over them
  static { ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE); }

  static final Font ARIAL_HEADER = new Font("Arial", Font.BOLD, 17);
  static final Font ARIAL_DESCRIPTION = new Font("Arial", Font.PLAIN, 13);
  private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

  private TetrisAudioSystem audioSystem;
  private TetrisGame game;
  private ScoreDao scoresDao = new FlatFileScoreDao();

  private BoardPanel boardPanel;
  private BlockDisplayPanel nextBlockPanel;
  private BlockDisplayPanel holdPanel;
  private MenuPanel menuPanel;
  private SettingsPanel settingsPanel;
  private ScorePanel scorePanel;

  // Tracks progress of Asynchronous UI effects
  private Future<?> clearTask;
  private Future<?> flashLabelTask;

  private KeyAdapter keyHandler = new KeyAdapter() {

    Set<Integer> pressed = new HashSet<>();

    public void keyPressed(KeyEvent e) {

      int code = e.getKeyCode();
      pressed.add(code);

      switch (code) {

        case KeyEvent.VK_LEFT:

          if (pressed.contains(KeyEvent.VK_S)) {
            game.superSlideActiveBlockLeft();
            audioSystem.playSuperslideSound();
          } else {
            game.moveActiveBlockLeft();
          }

          break;

        case KeyEvent.VK_RIGHT:

          if (pressed.contains(KeyEvent.VK_S)) {
            game.superSlideActiveBlockRight();
            audioSystem.playSuperslideSound();
          } else {
            game.moveActiveBlockRight();
          }

          break;

        case KeyEvent.VK_DOWN:

          game.moveActiveBlockDown();
          break;

        case KeyEvent.VK_UP:

          if (game.rotateActiveBlockCW()) {
            audioSystem.playCWRotationSound();
          }
          break;

        case KeyEvent.VK_F:

          if (game.rotateActiveBlockCCW()) {
            audioSystem.playCCWRotationSound();
          }
          break;

        case KeyEvent.VK_D: // Hold set

          Block activeBlock = game.getActiveBlock();

          if (!game.getHoldBlock().isPresent() && !activeBlock.isHoldBlock()) {
            activeBlock.tagAsHoldBlock();
            audioSystem.playHoldSound();
            game.setHoldBlock(activeBlock);
            Block nextBlock = game.getConveyor().next();
            game.spawn(nextBlock);
          }

          break;

        case KeyEvent.VK_E: // Hold release

          if (game.getHoldBlock().isPresent()) {
            Block heldPiece = game.getHoldBlock().get();
            game.spawn(heldPiece);
            game.clearHoldBlock();
            audioSystem.playReleaseSound();
          }

          break;

        case KeyEvent.VK_SPACE:

          game.dropCurrentBlock();
          audioSystem.playBlockPlacementSound();
          game.tryFall();
          break;
      }

      repaint();
    }

    public void keyReleased(KeyEvent e) {
      pressed.remove(e.getKeyCode());
    }

  };

  public MasterTetrisFrame() {

    this.audioSystem = new TetrisAudioSystem();

    this.game = new TetrisGame();
    this.game.getFallTimer().setInitialDelay(0);
    this.game.getFallTimer().addActionListener(e -> repaint());
    this.game.getGameTimer().addActionListener(e -> {
      scorePanel.timeLabel.repaint();
      scorePanel.timeProgressBar.repaint();
    });

    this.game.subscribe(new TetrisEvent[]{ TetrisEvent.SPAWN_FAIL, TetrisEvent.TIME_ATTACK_FAIL }, e -> onGameOver());
    this.game.subscribe(TetrisEvent.GAME_WON, e -> onWin());
    this.game.subscribe(TetrisEvent.LINES_CLEARED, event -> {
      int lines = (int) event;
      audioSystem.playClearLineSound(lines);
      scorePanel.totalLinesLabel.repaint();
      scorePanel.linesClearedProgressBar.repaint();
    });
    this.game.subscribe(TetrisEvent.LEVEL_CHANGED, event -> {

      int newLevel = (int) event;

      scorePanel.levelLabel.setText("Level: " + newLevel);
      scorePanel.timeProgressBar.repaint();

      audioSystem.startSoundtrack(newLevel);

      if (newLevel > 1) {
        flashLabelTask = THREAD_POOL.submit(() -> scorePanel.levelLabel.flash(Color.YELLOW));
      }
    });
    this.game.subscribe(TetrisEvent.SCORE_CHANGED, score -> scorePanel.scoreLabel.repaint());

    this.boardPanel = new BoardPanel();

    this.nextBlockPanel = new BlockDisplayPanel("Next") {
      @Override public Collection<Block.ColoredSquare> getCurrentColors() {
        Block nextBlock = game.getConveyor().peek();
        return nextBlock == null ? Collections.emptyList() : nextBlock.getNextPanelSquares();
      }
    };

    this.holdPanel = new BlockDisplayPanel("Hold") {
      @Override public Collection<Block.ColoredSquare> getCurrentColors() {
        return game.getHoldBlock().isPresent() ?
            game.getHoldBlock().get().getNextPanelSquares() :
            Collections.emptyList();
      }
    };

    this.menuPanel = new MenuPanel();
    this.settingsPanel = new SettingsPanel();
    this.scorePanel = new ScorePanel();

    LinkedHashMap<String, String> keys_action = new LinkedHashMap<>(); // LinkedHashMap because order matters for display
    keys_action.put("Up:",          "Rotate CW");
    keys_action.put("'F':",         "Rotate CCW");
    keys_action.put("Down:",        "Shift down");
    keys_action.put("Left:",        "Shift left");
    keys_action.put("Right:",       "Shift right");
    keys_action.put("'S' + left:",  "Super-slide left");
    keys_action.put("'S' + right:", "Super-slide right");
    keys_action.put("Spacebar:",    "Instant drop");
    keys_action.put("'D':",         "Set hold");
    keys_action.put("'E':",         "Release hold");
    keys_action.put("Alt + 'S':",   "Start game");
    keys_action.put("Alt + 'P':",   "Pause game");
    keys_action.put("Alt + 'R':",   "Resume game");
    keys_action.put("Alt + 'G':",   "Give up");
    keys_action.put("Alt + 'L':",   "View leaderboard");

    JPanel keys = new JPanel(new GridLayout(keys_action.size(), 1));
    JPanel actions = new JPanel(new GridLayout(keys_action.size(), 1));
    keys_action.forEach((key, action) -> {
      keys.add(new JLabel(key));
      actions.add(new JLabel(action));
    });

    JPanel controls = new JPanel(new BorderLayout());
    controls.setBorder(new TitledBorder("Controls"));
    controls.add(keys, BorderLayout.WEST);
    controls.add(actions, BorderLayout.EAST);

    JPanel holdContainer = new JPanel(new BorderLayout());
    holdContainer.add(holdPanel, BorderLayout.NORTH);
    holdContainer.add(controls, BorderLayout.CENTER);
    add(holdContainer, BorderLayout.WEST);

    add(boardPanel, BorderLayout.CENTER);

    JPanel infoPanel = new JPanel(new BorderLayout());
    infoPanel.add(nextBlockPanel, BorderLayout.NORTH);
    infoPanel.add(scorePanel, BorderLayout.CENTER);
    infoPanel.add(settingsPanel, BorderLayout.SOUTH);
    add(infoPanel, BorderLayout.EAST);

    add(menuPanel, BorderLayout.SOUTH);

    SwingUtility.setIcon(this, "/images/game-icon.png");
    setTitle("Tetris");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    int masterWidth = (holdPanel.getColumns() * BlockDisplayPanel.DEFAULT_BLOCK_DIMENSION) +
                      (boardPanel.getColumns() * BlockDisplayPanel.DEFAULT_BLOCK_DIMENSION) +
                      (nextBlockPanel.getColumns() * BlockDisplayPanel.DEFAULT_BLOCK_DIMENSION);

    int masterHeight = BlockDisplayPanel.DEFAULT_BLOCK_DIMENSION * (boardPanel.getRows());

    setSize(masterWidth, masterHeight);
    setResizable(false); // I don't want to mess with trying to make this work right

    setLocationRelativeTo(null);
  }

  private void onStart() {

    game.beginNew();

    if (clearTask != null && !clearTask.isDone()) {
      clearTask.cancel(true);
    }
    if (flashLabelTask != null && !flashLabelTask.isDone()) {
      flashLabelTask.cancel(true);
    }

    settingsPanel.difficultyCombobox.setEnabled(false);
    settingsPanel.timeAttackCheckbox.isSelected();
    settingsPanel.specialsButton.setEnabled(false);
    settingsPanel.timeAttackCheckbox.setEnabled(false);
    settingsPanel.ghostSquaresCheckbox.setEnabled(false);
    settingsPanel.musicCheckbox.setEnabled(false);
    settingsPanel.saveScoresCheckbox.setEnabled(false);
    settingsPanel.soundEffectsCheckbox.setEnabled(false);

    menuPanel.startButton.setEnabled(false);
    menuPanel.pauseButton.setEnabled(true);
    menuPanel.resumeButton.setEnabled(false);
    menuPanel.giveUpButton.setEnabled(true);
    menuPanel.leaderboardButton.setEnabled(false);

    // Ensures end of game effects don't bleed over into the new game
    audioSystem.stopGameOverSound();
    audioSystem.stopVictoryFanfare();

    boardPanel.enableBlockMovement();

    holdPanel.repaint();

    scorePanel.timeProgressBar.setVisible(settingsPanel.timeAttackCheckbox.isSelected());
    scorePanel.totalLinesLabel.repaint();
  }

  private void onPause() {

    game.getFallTimer().stop();
    game.getGameTimer().stop();

    settingsPanel.ghostSquaresCheckbox.setEnabled(true);
    settingsPanel.musicCheckbox.setEnabled(true);
    settingsPanel.soundEffectsCheckbox.setEnabled(true);
    settingsPanel.saveScoresCheckbox.setEnabled(true);

    audioSystem.stopCurrentSoundtrack();
    audioSystem.playPauseSound();

    boardPanel.disableBlockMovement();

    menuPanel.resumeButton.setEnabled(true);
    menuPanel.pauseButton.setEnabled(false);
    menuPanel.giveUpButton.setEnabled(true);
    menuPanel.leaderboardButton.setEnabled(true);
  }

  private void onResume() {

    game.getFallTimer().start();
    game.getGameTimer().start();

    settingsPanel.ghostSquaresCheckbox.setEnabled(false);
    settingsPanel.musicCheckbox.setEnabled(false);
    settingsPanel.soundEffectsCheckbox.setEnabled(false);
    settingsPanel.saveScoresCheckbox.setEnabled(false);

    audioSystem.resumeCurrentSoundtrack();

    boardPanel.enableBlockMovement();

    menuPanel.resumeButton.setEnabled(false);
    menuPanel.pauseButton.setEnabled(true);
    menuPanel.giveUpButton.setEnabled(true);
    menuPanel.leaderboardButton.setEnabled(true);
  }

  private void onWin() {

    settingsPanel.difficultyCombobox.setEnabled(true);
    settingsPanel.specialsButton.setEnabled(true);
    settingsPanel.timeAttackCheckbox.setEnabled(true);
    settingsPanel.ghostSquaresCheckbox.setEnabled(true);
    settingsPanel.musicCheckbox.setEnabled(true);
    settingsPanel.saveScoresCheckbox.setEnabled(true);
    settingsPanel.soundEffectsCheckbox.setEnabled(true);

    menuPanel.startButton.setEnabled(true);
    menuPanel.pauseButton.setEnabled(false);
    menuPanel.resumeButton.setEnabled(false);
    menuPanel.giveUpButton.setEnabled(false);
    menuPanel.leaderboardButton.setEnabled(true);

    audioSystem.stopCurrentSoundtrack();
    audioSystem.playVictoryFanfare();

    boardPanel.disableBlockMovement();
    clearTask = THREAD_POOL.submit(boardPanel::jumpClear);
    scorePanel.levelLabel.setText("You Win!!!");
    flashLabelTask = THREAD_POOL.submit(() -> scorePanel.levelLabel.flash(Color.YELLOW));
  }

  private void onGameOver() {

    game.getFallTimer().stop();
    game.getGameTimer().stop();

    audioSystem.stopCurrentSoundtrack();
    audioSystem.playGameOverSound();

    menuPanel.startButton.setEnabled(true);
    menuPanel.pauseButton.setEnabled(false);
    menuPanel.resumeButton.setEnabled(false);
    menuPanel.giveUpButton.setEnabled(false);
    menuPanel.leaderboardButton.setEnabled(true);

    settingsPanel.ghostSquaresCheckbox.setEnabled(true);
    settingsPanel.musicCheckbox.setEnabled(true);
    settingsPanel.soundEffectsCheckbox.setEnabled(true);
    settingsPanel.saveScoresCheckbox.setEnabled(true);
    settingsPanel.timeAttackCheckbox.setEnabled(true);
    settingsPanel.difficultyCombobox.setEnabled(true);
    settingsPanel.specialsButton.setEnabled(true);

    boardPanel.disableBlockMovement();

    scorePanel.levelLabel.setText("Game Over!!!");
    flashLabelTask = THREAD_POOL.submit(() -> scorePanel.levelLabel.flash(Color.RED));

    clearTask = THREAD_POOL.submit(boardPanel::spiralClear);
  }

  private class BoardPanel extends PixelGrid {

    private static final int SPIRAL_SLEEP_INTERVAL = 8;
    private static final int CLEAR_SLEEP_INTERVAL = 79;

    BoardPanel() {
      super(game.getVerticalDimension() - 3, game.getHorizontalDimension(), BlockDisplayPanel.DEFAULT_BLOCK_DIMENSION);
      setFocusable(true);
      setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    }

    void enableBlockMovement() {
      addKeyListener(keyHandler);
    }

    void disableBlockMovement() {
      removeKeyListener(keyHandler);
    }

    void spiralClear() {
      try {
        game.logActiveBlock();
        game.clearActiveBlock();

        Collection<Block.ColoredSquare> spiralSquares = new LinkedHashSet<>();

        int nextLeftCol = 0,
            nextRightCol = game.getHorizontalDimension() - 1,
            nextTopRow = 0,
            nextBottomRow = game.getVerticalDimension() - 1;

        // Total squares is equal to the dimensions of the visible panels. Loop until the size of squares reaches this amount
        int maxSquares = game.getVerticalDimension() * game.getHorizontalDimension();
        while (spiralSquares.size() < maxSquares) {

          // All cells in the next leftmost column
          for (int row = nextTopRow; row <= nextBottomRow; row++) {
            spiralSquares.add(new Block.ColoredSquare(row, nextLeftCol));
          }
          nextLeftCol++;

          // All cells in the next bottom row
          for (int col = nextLeftCol; col <= nextRightCol; col++) {
            spiralSquares.add(new Block.ColoredSquare(nextBottomRow, col));
          }
          nextBottomRow--;

          // All cells in the next rightmost column
          for (int row = nextBottomRow; row >= nextTopRow; row--) {
            spiralSquares.add(new Block.ColoredSquare(row, nextRightCol));
          }
          nextRightCol--;

          // All cells in the next top row
          for (int col = nextRightCol; col >= nextLeftCol; col--) {
            spiralSquares.add(new Block.ColoredSquare(nextTopRow, col));
          }
          nextTopRow++;
        }

        // Run 1 loop to paint in all unoccupied squares
        for (Block.ColoredSquare spiralSquare : spiralSquares) {
          if (spiralSquare.getRow() < 3) continue;
          if (game.isOpen(spiralSquare.getRow(), spiralSquare.getColumn())) {
            game.setColor(spiralSquare.getRow(), spiralSquare.getColumn(), spiralSquare.getColor());
          }
          repaint();
          Thread.sleep(SPIRAL_SLEEP_INTERVAL);
        }

        // Run a second loop to erase all of them
        for (Block.ColoredSquare spiralSquare : spiralSquares) {
          if (spiralSquare.getRow() < 3) continue;
          game.clearSquare(spiralSquare.getRow(), spiralSquare.getColumn());
          repaint();
          Thread.sleep(SPIRAL_SLEEP_INTERVAL);
        }

        if (settingsPanel.saveScoresCheckbox.isSelected()) {
          new ScoreResultsFrame(scoresDao, game);
        }

      } catch (InterruptedException e) {
        // Will happen if new game is started before spiral clear is finished
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    void jumpClear() {
      try {

        // Fill all rows bottom to top
        for (int row = game.getVerticalDimension() - 1; row >= 3; row --) {
          for (int col = 0; col < game.getHorizontalDimension(); col++) {
            if (game.isOpen(row, col)) {
              game.setColor(row, col, BlockType.getRandomColor());
            }
          }
          repaint();
          Thread.sleep(CLEAR_SLEEP_INTERVAL);
        }

        // Clear all rows top to bottom.
        for (int row = 3; row < game.getVerticalDimension(); row ++) {
          for (int col = 0; col < game.getHorizontalDimension(); col++) {
            game.clearSquare(row, col);
          }
          repaint();
          Thread.sleep(CLEAR_SLEEP_INTERVAL);
        }

        if (settingsPanel.saveScoresCheckbox.isSelected()) {
          new ScoreResultsFrame(scoresDao, game);
        }

      } catch (InterruptedException e) {
        // Will happen if we start a new game before task is done
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public Collection<Block.ColoredSquare> getCurrentColors() {
      return game.getColoredSquares();
    }

    @Override
    protected int getYCoordinate(Block.ColoredSquare square) {
      return (square.getRow() - 3) * getUnitHeight(); // Adjusts for 3 invisible squares at top
    }

  }

  private class ScorePanel extends JPanel {

    private GridLayout layout;

    private JLabel scoreLabel = new JLabel("Score: 0", JLabel.CENTER) {
      @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setText("Score: " + game.getScore());
      }
    };

    private JLabel totalLinesLabel = new JLabel("Lines: 0 / " + game.getDifficulty().getLinesPerLevel(), JLabel.CENTER) {
      @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setText("Lines: " + game.getCurrentLevelLinesCleared() + " / " + game.getDifficulty().getLinesPerLevel());
      }
    };

    private FlashLabel levelLabel = new FlashLabel("Level: 1", JLabel.CENTER);

    private JLabel timeLabel = new JLabel("Time: 00:00", JLabel.CENTER) {
      @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        String timeLabel = "Time: " + Utility.formatSeconds(game.getCurrentLevelTime());
        if (game.isTimeAttack()) {
          timeLabel += " / " + Utility.formatSeconds(game.getDifficulty().getTimeAttackSecondsPerLevel());
        }
        setText(timeLabel);
      }
    };

    private ProgressBar linesClearedProgressBar = new ProgressBar(11, Color.GREEN) {
      @Override protected double getCurrentPercentage() {
        return 100.0 * (game.getCurrentLevelLinesCleared() * 1.0 / game.getDifficulty().getLinesPerLevel());
      }
    };

    private ProgressBar timeProgressBar = new ProgressBar(11, Color.YELLOW) {
      @Override protected double getCurrentPercentage() {

        int currentTime = game.getCurrentLevelTime();
        int maxLimit = game.getDifficulty().getTimeAttackSecondsPerLevel();
        double percentage = 100.0 * currentTime / maxLimit;

        int timeTillMax = maxLimit - currentTime;
        if (timeTillMax <= 10) {
          this.barColor = Color.RED;
        } else if (timeTillMax <= 20) {
          this.barColor = Color.YELLOW;
        } else {
          this.barColor = Color.GREEN;
        }

        return percentage;
      }
    };

    ScorePanel() {
      setBorder(new TitledBorder("Score"));

      layout = new GridLayout(6, 1);
      setLayout(layout);

      scoreLabel.setFont(ARIAL_HEADER);
      totalLinesLabel.setFont(ARIAL_HEADER);
      levelLabel.setFont(ARIAL_HEADER);
      timeLabel.setFont(ARIAL_HEADER);

      add(scoreLabel);
      add(levelLabel);
      add(totalLinesLabel);
      add(SwingUtility.nestInPanel(linesClearedProgressBar));
      add(timeLabel);

      timeProgressBar.setVisible(false);
      add(SwingUtility.nestInPanel(timeProgressBar));
    }

  }

  private class SettingsPanel extends JPanel {

    private JCheckBox ghostSquaresCheckbox;
    private JCheckBox musicCheckbox;
    private JCheckBox soundEffectsCheckbox;
    private JCheckBox saveScoresCheckbox;
    private JCheckBox timeAttackCheckbox;

    private JComboBox<Difficulty> difficultyCombobox;

    private TetrisButton specialsButton;

    SettingsPanel() {

      musicCheckbox = new JCheckBox("Music", true);
      musicCheckbox.setToolTipText("Controls whether music is played during game play");
      musicCheckbox.addItemListener(e -> audioSystem.setSoundtrackEnabled(musicCheckbox.isSelected()));

      soundEffectsCheckbox = new JCheckBox("Sound Effects", true);
      soundEffectsCheckbox.setToolTipText("Controls whether sound effects (rotation, drop, etc.) are played");
      soundEffectsCheckbox.addItemListener(e -> audioSystem.setEffectsEnabled(soundEffectsCheckbox.isSelected()));

      saveScoresCheckbox = new JCheckBox("Save Scores", true);
      saveScoresCheckbox.setToolTipText("Controls whether you are prompted to save your score after the game is finished");

      ghostSquaresCheckbox = new JCheckBox("Ghost Squares", true);
      ghostSquaresCheckbox.setToolTipText("Controls whether block placement squares are shown as the block falls");
      ghostSquaresCheckbox.addItemListener(e -> {
        game.setGhostSquaresEnabled(ghostSquaresCheckbox.isSelected());
        boardPanel.repaint();
      });

      timeAttackCheckbox = new JCheckBox("Time Attack Mode", false);
      timeAttackCheckbox.addItemListener(e -> game.setTimeAttack(timeAttackCheckbox.isSelected()));
      timeAttackCheckbox.setToolTipText(
        "<html>" +
          "<p>Limits available time per level as well as grants a point bonus per level cleared:</p>" +
          "<ul>" +
            "<li>On easy, you are given <b>" + Difficulty.EASY.getTimeAttackSecondsPerLevel() + "</b> seconds per level and <b>+" + Difficulty.EASY.getTimeAttackBonus() + "</b> bonus points are awarded per level cleared</li>" +
            "<li>On medium, you are given <b>" + Difficulty.MEDIUM.getTimeAttackSecondsPerLevel() + "</b> seconds per level and <b>+" + Difficulty.MEDIUM.getTimeAttackBonus() + "</b> bonus points are awarded per level cleared</li>" +
            "<li>On hard, you are given <b>" + Difficulty.HARD.getTimeAttackSecondsPerLevel() + "</b> seconds per level and <b>+" + Difficulty.HARD.getTimeAttackBonus() + "</b> bonus points are awarded per level cleared</li>" +
          "</ul>" +
       "</html>"
      );

      difficultyCombobox = new JComboBox<>(Difficulty.values());
      difficultyCombobox.addActionListener(e -> game.setDifficulty(getSelectedDifficulty()));
      difficultyCombobox.setSelectedIndex(0);

      specialsButton = new TetrisButton("Special Pieces...");
      specialsButton.addActionListener(e -> new SpecialPiecesFrame());

      setLayout(new BorderLayout());
      setBorder(new TitledBorder("Settings"));

      List<JCheckBox> checkboxes = Arrays.asList(ghostSquaresCheckbox, musicCheckbox, soundEffectsCheckbox, saveScoresCheckbox, timeAttackCheckbox);
      JPanel checkboxPanel = new JPanel(new GridLayout(checkboxes.size(), 1));
      for (JCheckBox checkbox : checkboxes) {
        checkboxPanel.add(checkbox);
        checkbox.setFocusable(false);
      }

      JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      diffPanel.add(new JLabel("Difficulty:  "));
      diffPanel.add(difficultyCombobox);
      difficultyCombobox.setToolTipText(
        "<html>" +
          "<p>Sets the game difficulty. The difficulty affects the following game parameters:</p>" +
          "<ul>" +
            "<li>" +
              "Number of lines required to complete each level:" +
              "<ul>" +
                "<li>" + Difficulty.EASY.getLinesPerLevel() + " on easy</li>" +
                "<li>" + Difficulty.MEDIUM.getLinesPerLevel() + " on medium</li>" +
                "<li>" + Difficulty.HARD.getLinesPerLevel() + " on hard</li>" +
              "</ul>" +
            "</li>" +
            "<li>" +
              "Bonus points awarded upon game completion:" +
              "<ul>" +
                "<li>" + Difficulty.EASY.getWinBonus() + " on easy</li>" +
                "<li>" + Difficulty.MEDIUM.getWinBonus() + " on medium</li>" +
                "<li>" + Difficulty.HARD.getWinBonus() + " on hard</li>" +
              "</ul>" +
            "</li>" +
            "<li>The likelihood of different block types appearing. Harder difficulties will cause 'easier' blocks to appear less often</li>" +
            "<li>" +
              "Initial block speed:" +
              "<ul>" +
                "<li>Initial fall delay of " + Difficulty.EASY.getInitialTimerDelay() + " milliseconds on easy</li>" +
                "<li>Initial fall delay of " + Difficulty.MEDIUM.getInitialTimerDelay() + " milliseconds on medium</li>" +
                "<li>Initial fall delay of " + Difficulty.HARD.getInitialTimerDelay() + " milliseconds on hard</li>" +
              "</ul>" +
            "</li>" +
            "<p>The block falling speed increases at a rate of " + Difficulty.TIMER_SPEEDUP + " milliseconds per level, regardless of difficulty</p>" +
          "</ul>" +
        "</html>"
      );

      add(checkboxPanel, BorderLayout.NORTH);
      add(diffPanel, BorderLayout.CENTER);
      add(specialsButton, BorderLayout.SOUTH);
    }

    Difficulty getSelectedDifficulty() {
      return (Difficulty) difficultyCombobox.getSelectedItem();
    }

  }

  private class MenuPanel extends JPanel {

    TetrisButton startButton = new TetrisButton("Start");
    TetrisButton pauseButton = new TetrisButton("Pause");
    TetrisButton resumeButton = new TetrisButton("Resume");
    TetrisButton giveUpButton = new TetrisButton("Give Up");
    TetrisButton leaderboardButton = new TetrisButton("Leaderboard");

    private MenuPanel() {

      setLayout(new FlowLayout());

      startButton.setMnemonic('s');
      startButton.setEnabled(true);
      startButton.addActionListener(e -> onStart());
      add(startButton);

      pauseButton.setMnemonic('p');
      pauseButton.setEnabled(false);
      pauseButton.addActionListener(e -> onPause());
      add(pauseButton);

      resumeButton.setMnemonic('r');
      resumeButton.setEnabled(false);
      resumeButton.addActionListener(e -> onResume());
      add(resumeButton);

      leaderboardButton.setMnemonic('l');
      leaderboardButton.setEnabled(true);
      leaderboardButton.addActionListener(e -> new LeaderboardFrame(scoresDao));
      add(leaderboardButton);

      giveUpButton.setMnemonic('g');
      giveUpButton.setEnabled(false);
      giveUpButton.addActionListener(e -> onGameOver());
      add(giveUpButton);
    }

  }

  private class SpecialPiecesFrame extends JFrame {

    private TetrisButton btnClose;

    SpecialPiecesFrame() {

      this.btnClose = new TetrisButton("Close");
      this.btnClose.addActionListener(e -> dispose());

      List<BlockType> specialBlocks = BlockType.getSpecialBlocks();
      JPanel blockPanels = new JPanel(new GridLayout(1, specialBlocks.size()));
      for (BlockType blockType : specialBlocks) {

        BlockDisplayPanel display = new BlockDisplayPanel("\"" + blockType + "\"", new Block(blockType));
        BlockSelectorButton selector = new BlockSelectorButton(blockType);

        JLabel pointBonus = new JLabel("+" + blockType.getBonusPointsPerLine() + " points per line");
        pointBonus.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel toggleControlPanel = new JPanel(new BorderLayout());
        toggleControlPanel.add(selector, BorderLayout.NORTH);
        toggleControlPanel.add(pointBonus, BorderLayout.SOUTH);

        JPanel blockPanel = new JPanel(new BorderLayout());
        blockPanel.add(display, BorderLayout.CENTER);
        blockPanel.add(toggleControlPanel, BorderLayout.SOUTH);
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
        setActiveState(game.getConveyor().isEnabled(blockType));
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
          game.getConveyor().enableBlockType(settingsPanel.getSelectedDifficulty(), blockType);
        } else {
          game.getConveyor().disableBlockType(blockType);
        }
      }

      void setActiveState(boolean active) {
        this.active = active;
        setBackground(active ? Color.YELLOW : Color.LIGHT_GRAY);
        setText(active ? "Active" : "Inactive");
      }

    }
  }

}
