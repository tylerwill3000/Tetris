package com.github.tylerwilliams.tetris.swing;

import com.github.tylerwilliams.tetris.*;
import com.github.tylerwilliams.tetris.audio.TetrisAudioSystem;
import com.github.tylerwilliams.tetris.event.TetrisEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.github.tylerwilliams.tetris.Utility.formatSeconds;

public class MasterTetrisFrame extends JFrame {

    // Prevents tooltips from disappearing while mouse is over them
    static {
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }

    static final Font ARIAL_HEADER = new Font("Arial", Font.BOLD, 17);
    static final Font ARIAL_DESCRIPTION = new Font("Arial", Font.PLAIN, 13);
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private final TetrisAudioSystem audioSystem;
    private final TetrisGame game;
    private final BoardPanel boardPanel;
    @SuppressWarnings("FieldCanBeLocal")
    private final TetronimoDisplayPanel nextTetronimoPanel;
    private final TetronimoDisplayPanel holdPanel;
    private final MenuPanel menuPanel;
    private final SettingsPanel settingsPanel;
    private ScorePanel scorePanel;

    // Tracks progress of Asynchronous UI effects
    private Future<?> clearTask;
    private Future<?> flashLabelTask;

    private final KeyAdapter keyHandler = new KeyAdapter() {

        final Set<Integer> pressedKeyCodes = new HashSet<>();

        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            pressedKeyCodes.add(keyCode);

            switch (keyCode) {
                case KeyEvent.VK_LEFT -> {
                    if (pressedKeyCodes.contains(KeyEvent.VK_S)) {
                        game.superSlideActiveTetronimoLeft();
                        audioSystem.playSuperSlideSound();
                    } else {
                        game.moveActiveTetronimoLeft();
                    }
                }

                case KeyEvent.VK_RIGHT -> {
                    if (pressedKeyCodes.contains(KeyEvent.VK_S)) {
                        game.superSlideActiveTetronimoRight();
                        audioSystem.playSuperSlideSound();
                    } else {
                        game.moveActiveTetronimoRight();
                    }
                }

                case KeyEvent.VK_DOWN -> game.moveActiveTetronimoDown();

                case KeyEvent.VK_UP -> {
                    if (game.rotateActiveTetronimo(Rotation.CLOCKWISE)) {
                        audioSystem.playClockwiseRotationSound();
                    }
                }

                case KeyEvent.VK_F -> {
                    if (game.rotateActiveTetronimo(Rotation.COUNTER_CLOCKWISE)) {
                        audioSystem.playCounterClockwiseRotationSound();
                    }
                }

                case KeyEvent.VK_D -> { // Hold set

                    Tetronimo activeTetronimo = game.getActiveTetronimo();
                    if (game.getHoldTetronimo().isEmpty() && !activeTetronimo.isHold()) {
                        activeTetronimo.tagAsHold();
                        audioSystem.playHoldSound();
                        game.setHoldTetronimo(activeTetronimo);
                        Tetronimo nextTetronimo = game.getConveyor().next();
                        game.spawn(nextTetronimo);
                    }
                }

                case KeyEvent.VK_E -> { // Hold release

                    if (game.getHoldTetronimo().isPresent()) {
                        Tetronimo heldPiece = game.getHoldTetronimo().get();
                        game.spawn(heldPiece);
                        game.clearHoldTetronimo();
                        audioSystem.playReleaseSound();
                    }
                }

                case KeyEvent.VK_SPACE -> {
                    game.dropCurrentTetronimo();
                    audioSystem.playTetronimoPlacementSound();
                    game.tryMoveActiveTetronimoDown();
                }
            }

            repaint();
        }

        public void keyReleased(KeyEvent e) {
            pressedKeyCodes.remove(e.getKeyCode());
        }
    };

    public MasterTetrisFrame() {
        this.audioSystem = TetrisAudioSystem.getInstance();

        this.game = new TetrisGame();
        this.game.getFallTimer().setInitialDelay(0);
        this.game.getFallTimer().addActionListener(e -> repaint());

        this.game.getGameTimer().addActionListener(e -> {
            scorePanel.timeLabel.repaint();
            scorePanel.timeProgressBar.repaint();
        });

        this.game.subscribe(TetrisEvent.TIME_ATTACK_FAIL, e -> onGameOver());
        this.game.subscribe(TetrisEvent.SPAWN_FAIL, e -> onGameOver());
        this.game.subscribe(TetrisEvent.GAME_WON, e -> onWin());

        this.game.subscribe(TetrisEvent.LINES_CLEARED, event -> {
            audioSystem.playClearLineSound();
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

        this.nextTetronimoPanel = new TetronimoDisplayPanel("Next") {
            @Override
            public Collection<ColoredSquare> getCurrentColors() {
                Tetronimo nextTetronimo = game.getConveyor().peek();
                return nextTetronimo == null ? List.of() : nextTetronimo.getPreviewPanelSquares();
            }
        };

        this.holdPanel = new TetronimoDisplayPanel("Hold") {
            @Override
            public Collection<ColoredSquare> getCurrentColors() {
                return game.getHoldTetronimo().map(Tetronimo::getPreviewPanelSquares).orElse(Collections.emptyList());
            }
        };

        this.menuPanel = new MenuPanel();
        this.settingsPanel = new SettingsPanel();
        this.scorePanel = new ScorePanel();

        LinkedHashMap<String, String> controls = new LinkedHashMap<>();
        controls.put("Up:", "Rotate CW");
        controls.put("'F':", "Rotate CCW");
        controls.put("Down:", "Shift down");
        controls.put("Left:", "Shift left");
        controls.put("Right:", "Shift right");
        controls.put("'S' + left:", "Super-slide left");
        controls.put("'S' + right:", "Super-slide right");
        controls.put("Spacebar:", "Instant drop");
        controls.put("'D':", "Set hold");
        controls.put("'E':", "Release hold");

        JPanel keys = new JPanel(new GridLayout(controls.size(), 1));
        JPanel actions = new JPanel(new GridLayout(controls.size(), 1));
        controls.forEach((key, action) -> {
            keys.add(new JLabel(key));
            actions.add(new JLabel(action));
        });

        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBorder(new TitledBorder("Controls"));
        controlsPanel.add(keys, BorderLayout.WEST);
        controlsPanel.add(actions, BorderLayout.EAST);

        JPanel holdContainer = new JPanel(new BorderLayout());
        holdContainer.add(holdPanel, BorderLayout.NORTH);
        holdContainer.add(controlsPanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(nextTetronimoPanel, BorderLayout.NORTH);
        infoPanel.add(scorePanel, BorderLayout.CENTER);
        infoPanel.add(settingsPanel, BorderLayout.SOUTH);

        add(holdContainer, BorderLayout.WEST);
        add(boardPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        add(menuPanel, BorderLayout.SOUTH);

        setIconImage(new ImageIcon(ImageFile.GAME_ICON.getUrl()).getImage());
        setTitle("Tetris");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();
        setLocationRelativeTo(null);
    }

    private void onStart() {
        game.reset();

        if (clearTask != null && !clearTask.isDone()) {
            clearTask.cancel(true);
        }

        if (flashLabelTask != null && !flashLabelTask.isDone()) {
            flashLabelTask.cancel(true);
        }

        settingsPanel.difficultyComboBox.setEnabled(false);
        settingsPanel.gameModeComboBox.setEnabled(false);
        settingsPanel.ghostSquaresCheckbox.setEnabled(false);
        settingsPanel.soundtrackCheckbox.setEnabled(false);
        settingsPanel.soundEffectsCheckbox.setEnabled(false);

        menuPanel.startButton.setEnabled(false);
        menuPanel.pauseButton.setEnabled(true);
        menuPanel.resumeButton.setEnabled(false);
        menuPanel.giveUpButton.setEnabled(true);
        menuPanel.leaderboardButton.setEnabled(false);

        boardPanel.enableKeyHandler();

        holdPanel.repaint();

        scorePanel.levelLabel.setVisible(game.getGameMode() != GameMode.FREE_PLAY);
        scorePanel.timeProgressBar.setVisible(game.getGameMode() == GameMode.TIME_ATTACK);
        scorePanel.linesClearedProgressBar.setVisible(game.getGameMode() != GameMode.FREE_PLAY);
        scorePanel.totalLinesLabel.repaint();
    }

    private void onPause() {
        game.getFallTimer().stop();
        game.getGameTimer().stop();

        settingsPanel.ghostSquaresCheckbox.setEnabled(true);
        settingsPanel.soundtrackCheckbox.setEnabled(true);
        settingsPanel.soundEffectsCheckbox.setEnabled(true);

        audioSystem.stopCurrentSoundtrack();
        audioSystem.playPauseSound();

        boardPanel.disableKeyHandler();

        menuPanel.resumeButton.setEnabled(true);
        menuPanel.pauseButton.setEnabled(false);
        menuPanel.giveUpButton.setEnabled(true);
        menuPanel.leaderboardButton.setEnabled(true);
    }

    private void onResume() {
        game.getFallTimer().start();
        game.getGameTimer().start();

        settingsPanel.ghostSquaresCheckbox.setEnabled(false);
        settingsPanel.soundtrackCheckbox.setEnabled(false);
        settingsPanel.soundEffectsCheckbox.setEnabled(false);

        audioSystem.resumeCurrentSoundtrack();

        boardPanel.enableKeyHandler();

        menuPanel.resumeButton.setEnabled(false);
        menuPanel.pauseButton.setEnabled(true);
        menuPanel.giveUpButton.setEnabled(true);
        menuPanel.leaderboardButton.setEnabled(true);
    }

    private void onWin() {
        settingsPanel.difficultyComboBox.setEnabled(true);
        settingsPanel.gameModeComboBox.setEnabled(true);
        settingsPanel.ghostSquaresCheckbox.setEnabled(true);
        settingsPanel.soundtrackCheckbox.setEnabled(true);
        settingsPanel.soundEffectsCheckbox.setEnabled(true);

        menuPanel.startButton.setEnabled(true);
        menuPanel.pauseButton.setEnabled(false);
        menuPanel.resumeButton.setEnabled(false);
        menuPanel.giveUpButton.setEnabled(false);
        menuPanel.leaderboardButton.setEnabled(true);

        audioSystem.stopCurrentSoundtrack();
        audioSystem.playVictoryFanfare();

        boardPanel.disableKeyHandler();
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
        settingsPanel.soundtrackCheckbox.setEnabled(true);
        settingsPanel.soundEffectsCheckbox.setEnabled(true);
        settingsPanel.gameModeComboBox.setEnabled(true);
        settingsPanel.difficultyComboBox.setEnabled(true);

        boardPanel.disableKeyHandler();

        scorePanel.levelLabel.setVisible(true); // explicitly set visible in case it's hidden due to free play mode
        scorePanel.levelLabel.setText("Game Over!!!");

        flashLabelTask = THREAD_POOL.submit(() -> scorePanel.levelLabel.flash(Color.RED));
        clearTask = THREAD_POOL.submit(boardPanel::spiralClear);
    }

    private class BoardPanel extends ColorGrid {
        private static final int SPIRAL_SLEEP_INTERVAL = 6;
        private static final int CLEAR_SLEEP_INTERVAL = 79;
        private static final int BOARD_PANEL_SQUARE_DIMENSION = 40;

        BoardPanel() {
            super(TetrisGame.VERTICAL_DIMENSION - TetrisGame.LEADING_OVERFLOW_ROWS, TetrisGame.HORIZONTAL_DIMENSION, BOARD_PANEL_SQUARE_DIMENSION);
            setFocusable(true);
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }

        void enableKeyHandler() {
            addKeyListener(keyHandler);
        }

        void disableKeyHandler() {
            removeKeyListener(keyHandler);
        }

        @SuppressWarnings("DataFlowIssue")
        void spiralClear() {
            try {
                game.persistActiveTetronimoColors();
                game.clearActiveTetronimo();

                List<ColoredSquare> spiralSquares = new ArrayList<>();

                int nextLeftCol = 0,
                        nextRightCol = TetrisGame.HORIZONTAL_DIMENSION - 1,
                        nextTopRow = TetrisGame.LEADING_OVERFLOW_ROWS,
                        nextBottomRow = TetrisGame.VERTICAL_DIMENSION - 1;

                int maxSquares = (TetrisGame.VERTICAL_DIMENSION - TetrisGame.LEADING_OVERFLOW_ROWS) * TetrisGame.HORIZONTAL_DIMENSION;

                while (spiralSquares.size() < maxSquares) {
                    // All cells in the next leftmost column
                    for (int row = nextTopRow; row <= nextBottomRow; row++) {
                        spiralSquares.add(new ColoredSquare(TetronimoType.getRandomColor(), row, nextLeftCol));
                    }
                    nextLeftCol++;

                    // All cells in the next bottom row
                    for (int col = nextLeftCol; col <= nextRightCol; col++) {
                        spiralSquares.add(new ColoredSquare(TetronimoType.getRandomColor(), nextBottomRow, col));
                    }
                    nextBottomRow--;

                    // All cells in the next rightmost column
                    for (int row = nextBottomRow; row >= nextTopRow; row--) {
                        spiralSquares.add(new ColoredSquare(TetronimoType.getRandomColor(), row, nextRightCol));
                    }
                    nextRightCol--;

                    // All cells in the next top row
                    for (int col = nextRightCol; col >= nextLeftCol; col--) {
                        spiralSquares.add(new ColoredSquare(TetronimoType.getRandomColor(), nextTopRow, col));
                    }
                    nextTopRow++;
                }

                // Run 1 loop to paint in all unoccupied squares
                for (ColoredSquare spiralSquare : spiralSquares) {
                    if (game.isOpenAndInBounds(spiralSquare.row(), spiralSquare.column())) {
                        game.setColor(spiralSquare.row(), spiralSquare.column(), spiralSquare.color());
                    }
                    repaint();
                    Thread.sleep(SPIRAL_SLEEP_INTERVAL);
                }

                // Run a second loop to erase all of them
                for (ColoredSquare spiralSquare : spiralSquares) {
                    game.clearSquare(spiralSquare.row(), spiralSquare.column());
                    repaint();
                    Thread.sleep(SPIRAL_SLEEP_INTERVAL);
                }

                menuPanel.leaderboardButton.disableWhileShown(new ScoreResultsFrame(game, menuPanel));
            } catch (InterruptedException e) {
                // Will happen if new game is started before spiral clear is finished
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        void jumpClear() {
            try {
                // Fill all rows bottom to top
                for (int row = TetrisGame.VERTICAL_DIMENSION - 1; row >= TetrisGame.LEADING_OVERFLOW_ROWS; row--) {
                    for (int col = 0; col < TetrisGame.HORIZONTAL_DIMENSION; col++) {
                        if (game.isOpenAndInBounds(row, col)) {
                            game.setColor(row, col, TetronimoType.getRandomColor());
                        }
                    }
                    repaint();
                    Thread.sleep(CLEAR_SLEEP_INTERVAL);
                }

                // Clear all rows top to bottom.
                for (int row = TetrisGame.LEADING_OVERFLOW_ROWS; row < TetrisGame.VERTICAL_DIMENSION; row++) {
                    for (int col = 0; col < TetrisGame.HORIZONTAL_DIMENSION; col++) {
                        game.clearSquare(row, col);
                    }
                    repaint();
                    Thread.sleep(CLEAR_SLEEP_INTERVAL);
                }

                menuPanel.leaderboardButton.disableWhileShown(new ScoreResultsFrame(game, menuPanel));
            } catch (InterruptedException e) {
                // Will happen if we start a new game before task is done
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Collection<ColoredSquare> getCurrentColors() {
            return game.getColoredSquares();
        }

        @Override
        protected int getYCoordinate(ColoredSquare square) {
            return (square.row() - TetrisGame.LEADING_OVERFLOW_ROWS) * getUnitHeight();
        }
    }

    private class ScorePanel extends JPanel {

        private final JLabel scoreLabel = new JLabel("Score: 0", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setText("Score: " + game.getScore());
            }
        };

        private final JLabel totalLinesLabel = new JLabel("Lines: 0 / " + game.getDifficulty().getLinesPerLevel(), JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                String text = "Lines: " + game.getCurrentLevelLinesCleared();
                if (game.getGameMode() != GameMode.FREE_PLAY) {
                    text += " / " + game.getDifficulty().getLinesPerLevel();
                }
                setText(text);
            }
        };

        private final FlashableLabel levelLabel = new FlashableLabel("Level: 1", JLabel.CENTER);

        private final JLabel timeLabel = new JLabel("Time: 00:00", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                String timeDisplay;
                if (game.getGameMode() == GameMode.TIME_ATTACK) {
                    timeDisplay = "Time: %s / %s".formatted(formatSeconds(game.getCurrentLevelTime()), formatSeconds(game.getDifficulty().getTimeAttackSecondsPerLevel()));
                } else {
                    timeDisplay = "Time: %s".formatted(formatSeconds(game.getGameTime()));
                }

                setText(timeDisplay);
            }
        };

        private final ProgressBar linesClearedProgressBar = new ProgressBar(11, Color.GREEN) {
            @Override
            protected double getCurrentPercentage() {
                return 100.0 * (game.getCurrentLevelLinesCleared() * 1.0 / game.getDifficulty().getLinesPerLevel());
            }
        };

        private final ProgressBar timeProgressBar = new ProgressBar(11, Color.YELLOW) {
            @Override
            protected double getCurrentPercentage() {
                int currentTime = game.getCurrentLevelTime();
                int totalTime = game.getDifficulty().getTimeAttackSecondsPerLevel();
                int timeRemaining = totalTime - currentTime;

                if (timeRemaining <= 10) {
                    this.barColor = Color.RED;
                } else if (timeRemaining <= 20) {
                    this.barColor = Color.YELLOW;
                } else {
                    this.barColor = Color.GREEN;
                }

                return 100.0 * currentTime / totalTime;
            }
        };

        ScorePanel() {
            setBorder(new TitledBorder("Score"));

            setLayout(new GridLayout(6, 1));

            scoreLabel.setFont(ARIAL_HEADER);
            totalLinesLabel.setFont(ARIAL_HEADER);
            levelLabel.setFont(ARIAL_HEADER);
            timeLabel.setFont(ARIAL_HEADER);

            add(scoreLabel);
            add(levelLabel);
            add(totalLinesLabel);

            JPanel linesClearedProgressBarPanel = new JPanel();
            linesClearedProgressBarPanel.add(linesClearedProgressBar);
            add(linesClearedProgressBarPanel);

            add(timeLabel);

            timeProgressBar.setVisible(false);

            JPanel timeProgressBarPanel = new JPanel();
            timeProgressBarPanel.add(timeProgressBar);
            add(timeProgressBarPanel);
        }

    }

    private class SettingsPanel extends JPanel {
        private final Settings settings;
        private final JCheckBox ghostSquaresCheckbox;
        private final JCheckBox soundtrackCheckbox;
        private final JCheckBox soundEffectsCheckbox;
        private final JComboBox<GameMode> gameModeComboBox;
        private final JComboBox<Difficulty> difficultyComboBox;

        SettingsPanel() {
            settings = SettingsRepository.load().orElseGet(Settings::new);

            soundtrackCheckbox = new JCheckBox("Soundtrack", settings.soundtrack);
            soundtrackCheckbox.setToolTipText("Controls whether soundtrack is played during game play");
            soundtrackCheckbox.addItemListener(e -> {
                audioSystem.setSoundtrackEnabled(soundtrackCheckbox.isSelected());
                settings.soundtrack = soundtrackCheckbox.isSelected();
                SettingsRepository.save(settings);
            });
            audioSystem.setSoundtrackEnabled(settings.soundtrack);

            soundEffectsCheckbox = new JCheckBox("Sound Effects", settings.soundEffects);
            soundEffectsCheckbox.setToolTipText("Controls whether sound effects (rotation, drop, etc.) are played");
            soundEffectsCheckbox.addItemListener(e -> {
                audioSystem.setEffectsEnabled(soundEffectsCheckbox.isSelected());
                settings.soundEffects = soundEffectsCheckbox.isSelected();
                SettingsRepository.save(settings);
            });
            audioSystem.setEffectsEnabled(settings.soundEffects);

            ghostSquaresCheckbox = new JCheckBox("Ghost Squares", settings.ghostSquares);
            ghostSquaresCheckbox.setToolTipText("Controls whether tetronimo placement squares are shown as the tetronimo falls");
            ghostSquaresCheckbox.addItemListener(e -> {
                game.setGhostSquaresEnabled(ghostSquaresCheckbox.isSelected());
                settings.ghostSquares = ghostSquaresCheckbox.isSelected();
                SettingsRepository.save(settings);

                boardPanel.repaint();
            });
            game.setGhostSquaresEnabled(settings.ghostSquares);

            difficultyComboBox = new JComboBox<>(Difficulty.values());
            difficultyComboBox.setSelectedItem(settings.difficulty);
            difficultyComboBox.addActionListener(e -> {
                var difficulty = getSelectedDifficulty();
                game.setDifficulty(difficulty);
                settings.difficulty = difficulty;
                SettingsRepository.save(settings);
            });
            difficultyComboBox.setToolTipText(
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
                        "<li>The likelihood of different tetronimo types appearing. Harder difficulties will cause 'easier' tetronimos to appear less often</li>" +
                        "<li>" +
                            "Initial tetronimo speed:" +
                            "<ul>" +
                                "<li>Initial fall delay of " + Difficulty.EASY.getInitialTimerDelay() + " milliseconds on easy</li>" +
                                "<li>Initial fall delay of " + Difficulty.MEDIUM.getInitialTimerDelay() + " milliseconds on medium</li>" +
                                "<li>Initial fall delay of " + Difficulty.HARD.getInitialTimerDelay() + " milliseconds on hard</li>" +
                            "</ul>" +
                        "</li>" +
                        "<p>The tetronimo speed increases at a rate of " + Difficulty.TIMER_SPEEDUP + " milliseconds per level, regardless of difficulty</p>" +
                    "</ul>" +
                "</html>"
            );
            game.setDifficulty(settings.difficulty);

            gameModeComboBox = new JComboBox<>(GameMode.values());
            gameModeComboBox.setSelectedItem(settings.gameMode);
            gameModeComboBox.addActionListener(e -> {
                var gameMode = getSelectedGameMode();
                game.setGameMode(gameMode);
                settings.gameMode = gameMode;
                SettingsRepository.save(settings);
            });
            gameModeComboBox.setToolTipText(
                "<html>" +
                    "<ul>" +
                        "<li>" +
                            "<b>" + GameMode.CAMPAIGN + ":</b> 10 levels of play, with each successive level increasing tetronimo speed" +
                        "</li>" +
                        "<li>" +
                            "<b>" + GameMode.TIME_ATTACK + ":</b> Limits available time per level:" +
                            "<ul>" +
                                "<li>On easy, you are given <b>" + Difficulty.EASY.getTimeAttackSecondsPerLevel() + "</b> seconds per level</li>" +
                                "<li>On medium, you are given <b>" + Difficulty.MEDIUM.getTimeAttackSecondsPerLevel() + "</b> seconds per level</li>" +
                                "<li>On hard, you are given <b>" + Difficulty.HARD.getTimeAttackSecondsPerLevel() + "</b> seconds per level</li>" +
                            "</ul>" +
                        "</li>" +
                        "<li>" +
                            "<b>" + GameMode.FREE_PLAY + ":</b> Unlimited free play until the player game overs. " +
                                "Each line cleared will decrease the fall timer interval by 2 milliseconds, down to a minimum of <b>" + TetrisGame.FREE_PLAY_MINIMUM_FALL_TIMER_DELAY + "</b> milliseconds" +
                        "</li>" +
                "</html>"
            );
            game.setGameMode(settings.gameMode);

            setBorder(new TitledBorder("Settings"));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            add(ghostSquaresCheckbox);
            add(soundtrackCheckbox);
            add(soundEffectsCheckbox);

            add(Box.createRigidArea(new Dimension(0, 5)));

            add(new JLabel("Game Mode"));
            var gameModePanel = new JPanel();
            gameModePanel.setLayout(new BoxLayout(gameModePanel, BoxLayout.X_AXIS));
            gameModePanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
            gameModePanel.add(gameModeComboBox);
            add(gameModePanel);

            add(Box.createRigidArea(new Dimension(0, 5)));

            add(new JLabel("Difficulty"));
            var difficultyPanel = new JPanel();
            difficultyPanel.setLayout(new BoxLayout(difficultyPanel, BoxLayout.X_AXIS));
            difficultyPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
            difficultyPanel.add(difficultyComboBox);
            add(difficultyPanel);
        }

        GameMode getSelectedGameMode() {
            return (GameMode) gameModeComboBox.getSelectedItem();
        }

        Difficulty getSelectedDifficulty() {
            return (Difficulty) difficultyComboBox.getSelectedItem();
        }
    }

    class MenuPanel extends JPanel {
        final TetrisButton startButton = new TetrisButton("Start");
        final TetrisButton pauseButton = new TetrisButton("Pause");
        final TetrisButton resumeButton = new TetrisButton("Resume");
        final TetrisButton giveUpButton = new TetrisButton("Give Up");
        final TetrisButton leaderboardButton = new TetrisButton("Leaderboard");

        private MenuPanel() {
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
            leaderboardButton.addActionListener(e ->
                leaderboardButton.disableWhileShown(new LeaderBoardFrame(null))
            );
            add(leaderboardButton);

            giveUpButton.setMnemonic('g');
            giveUpButton.setEnabled(false);
            giveUpButton.addActionListener(e -> onGameOver());
            add(giveUpButton);
        }
    }
}
