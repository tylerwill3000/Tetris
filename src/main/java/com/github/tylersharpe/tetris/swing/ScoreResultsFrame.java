package com.github.tylersharpe.tetris.swing;

import com.github.tylersharpe.tetris.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

class ScoreResultsFrame extends JFrame {

    private final static int NAME_LENGTH = 40;

    private final JTextField nameField = new JTextField(10);
    private final ScoreRepository scoreRepository;
    private final TetrisGame tetrisGame;
    private final MasterTetrisFrame.MenuPanel menuPanel;
    private final LocalDateTime scoreDate;

    ScoreResultsFrame(ScoreRepository scoreRepository, TetrisGame tetrisGame, MasterTetrisFrame.MenuPanel menuPanel) {
        this.scoreRepository = scoreRepository;
        this.tetrisGame = tetrisGame;
        this.menuPanel = menuPanel;
        this.scoreDate = LocalDateTime.now();

        setLayout(new GridLayout(3, 1));

        nameField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (nameField.getText().length() > NAME_LENGTH) {
                    nameField.setText(nameField.getText().substring(0, NAME_LENGTH));
                }
            }
        });

        int rank;
        try {
            rank = scoreRepository.determineRank(tetrisGame.getScore(), tetrisGame.getDifficulty(), tetrisGame.getGameMode(), this.scoreDate);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Could not determine rank: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JLabel scoreLabel = new JLabel();
        scoreLabel.setFont(MasterTetrisFrame.ARIAL_HEADER);
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setText("Your score: " + tetrisGame.getScore() + "      Your rank: " + rank);
        add(scoreLabel);

        if (ScoreRepository.isLeaderBoardRank(rank)) {
            JLabel congratsLabel = new JLabel("Congratulations! You made the leaderboard! Enter the name to save your score under or press cancel: ");
            congratsLabel.setFont(MasterTetrisFrame.ARIAL_DESCRIPTION);

            var inputPanel = new JPanel();
            inputPanel.add(congratsLabel);
            inputPanel.add(nameField);

            var saveScoreButton = new TetrisButton("Save");
            saveScoreButton.setMnemonic('s');
            saveScoreButton.addActionListener(e -> onSaveScoreClicked(rank));

            var closeButton = new TetrisButton("Cancel");
            closeButton.setMnemonic('c');
            closeButton.addActionListener(e -> dispose());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(saveScoreButton);
            buttonPanel.add(closeButton);

            add(inputPanel);
            add(buttonPanel);
        }

        setIconImage(new ImageIcon(ImageFile.SAVE_ICON.getUrl()).getImage());
        setTitle("Score Results");
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onSaveScoreClicked(int rank) {
        String saveName = nameField.getText().trim();

        if (saveName.isBlank()) {
            JOptionPane.showMessageDialog(null, "You must enter a name to save your score");
            return;
        }

        try {
            int totalLinesCleared = tetrisGame.getTotalLinesCleared();
            Difficulty difficulty = tetrisGame.getDifficulty();

            Boolean completedGame = null;
            Integer maxLevel = null;
            if (tetrisGame.getGameMode() != GameMode.FREE_PLAY) {
                completedGame = totalLinesCleared == difficulty.getLinesPerLevel() * TetrisGame.MAX_LEVEL;
                maxLevel = tetrisGame.getLevel();
            }

            Score score = new Score(tetrisGame.getScore(),
                                    tetrisGame.getTotalLinesCleared(),
                                    rank,
                                    maxLevel,
                                    Duration.ofSeconds(tetrisGame.getGameTime()),
                                    saveName,
                                    tetrisGame.getDifficulty(),
                                    tetrisGame.getGameMode(),
                                    completedGame,
                                    scoreDate);
            scoreRepository.saveScore(score);

            dispose();

            menuPanel.leaderboardButton.disableWhileShown(new LeaderBoardFrame(scoreRepository, score));
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Could not save score: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
