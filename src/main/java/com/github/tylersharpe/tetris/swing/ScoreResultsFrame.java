package com.github.tylersharpe.tetris.swing;

import com.github.tylersharpe.tetris.Score;
import com.github.tylersharpe.tetris.ScoreRepository;
import com.github.tylersharpe.tetris.TetrisGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.LocalDate;

class ScoreResultsFrame extends JFrame {

    private final static int NAME_LENGTH = 40;

    private final JTextField nameField = new JTextField(10);
    private final ScoreRepository scoreRepository;
    private final TetrisGame tetrisGame;

    ScoreResultsFrame(ScoreRepository scoreRepository, TetrisGame tetrisGame) {
        this.scoreRepository = scoreRepository;
        this.tetrisGame = tetrisGame;

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
            rank = scoreRepository.determineRank(tetrisGame.getScore());
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

        SwingUtility.setIcon(this, "/images/save-icon.png");
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
            scoreRepository.saveScore(
                    new Score(
                            saveName,
                            tetrisGame.getScore(),
                            tetrisGame.getGameTime(),
                            tetrisGame.getDifficulty(),
                            tetrisGame.getTotalLinesCleared(),
                            tetrisGame.getLevel(),
                            LocalDate.now())
            );
            dispose();
            new LeaderBoardFrame(scoreRepository, rank);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Could not save score: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
