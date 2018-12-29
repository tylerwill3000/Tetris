package com.github.tylersharpe.tetris.swing;

import com.github.tylersharpe.tetris.Score;
import com.github.tylersharpe.tetris.TetrisGame;
import com.github.tylersharpe.tetris.ScoreRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

class ScoreResultsFrame extends JFrame {

  private final static int NAME_LENGTH = 40;

  private JTextField nameField = new JTextField(10);

  ScoreResultsFrame(ScoreRepository scoreRepository, TetrisGame game) {

    setLayout(new GridLayout(3, 1));

    nameField.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (nameField.getText().length() > NAME_LENGTH) {
          nameField.setText(nameField.getText().substring(0, NAME_LENGTH));
        }
      }
    });

    int rank = scoreRepository.determineRank(game.getScore());

    var scoreLabel = new JLabel();
    scoreLabel.setFont(MasterTetrisFrame.ARIAL_HEADER);
    scoreLabel.setHorizontalAlignment(JLabel.CENTER);
    scoreLabel.setText("Your score: " + game.getScore() + "      Your rank: " + rank);
    add(scoreLabel);

    if (ScoreRepository.isLeaderBoardRank(rank)) {
      JLabel congrats = new JLabel("Congratulations! You made the leaderboard! Enter the name to save your score under or press cancel: ");
      congrats.setFont(MasterTetrisFrame.ARIAL_DESCRIPTION);

      var inputPanel = new JPanel();
      inputPanel.add(congrats);
      inputPanel.add(nameField);

      var saveScoreButton = new TetrisButton("Save");
      saveScoreButton.setMnemonic('s');
      saveScoreButton.addActionListener(e -> {
        String saveName = nameField.getText().trim();

        if (saveName.equals("")) {
          JOptionPane.showMessageDialog(null, "You must enter a name to save your score");
          return;
        }

        try {
          scoreRepository.saveScore(new Score(saveName, game.getScore(), game.getGameTime(),
                  game.getDifficulty(), game.getTotalLinesCleared(), game.getLevel()));
          dispose();
          new LeaderBoardFrame(scoreRepository, rank);
        } catch (IOException ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(null, "Could not save score", "Error", JOptionPane.ERROR_MESSAGE);
        }
      });

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

}
