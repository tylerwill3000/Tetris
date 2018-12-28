package com.github.tylersharpe.tetris.swing;

import com.github.tylersharpe.tetris.Score;
import com.github.tylersharpe.tetris.TetrisGame;
import com.github.tylersharpe.tetris.ScoreRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class ScoreResultsFrame extends JFrame {

  private final static int NAME_LENGTH = 20;

  private JLabel lblScore = new JLabel();
	private JTextField nameField = new JTextField(10);
	private TetrisButton saveScoreButton = new TetrisButton("Save");
	private TetrisButton closeButton = new TetrisButton("Cancel");

  ScoreResultsFrame(ScoreRepository scoresDao, TetrisGame game) {

    setLayout(new GridLayout(3, 1));

    nameField.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (nameField.getText().length() > NAME_LENGTH) {
          nameField.setText(nameField.getText().substring(0, NAME_LENGTH));
        }
      }
    });

    int rank = scoresDao.determineRank(game.getScore());
    lblScore.setFont(MasterTetrisFrame.ARIAL_HEADER);
    lblScore.setHorizontalAlignment(JLabel.CENTER);
    lblScore.setText("Your score: " + game.getScore() + "      Your rank: " + rank);
    add(lblScore);

    if (ScoreRepository.isLeaderBoardRank(rank)) {

      JPanel inputPanel = new JPanel();
      JLabel congrats = new JLabel("Congratulations! You made the leaderboard! Enter the name to save your score under or press cancel: ");
      congrats.setFont(MasterTetrisFrame.ARIAL_DESCRIPTION);
      inputPanel.add(congrats);
      inputPanel.add(nameField);

      JPanel buttonPanel = new JPanel();
      buttonPanel.add(saveScoreButton);
      buttonPanel.add(closeButton);

      saveScoreButton.setMnemonic('s');
      closeButton.setMnemonic('c');

      saveScoreButton.addActionListener(e -> {

        String saveName = nameField.getText();

        if (saveName.equals("")) {
          JOptionPane.showMessageDialog(null, "You must enter a name to save your score");
          return;
        }

        try {
          scoresDao.saveScore(new Score(saveName, game.getScore(), game.getGameTime(),
                                game.getDifficulty(), game.getTotalLinesCleared(), game.getLevel()));
          dispose();
          new LeaderBoardFrame(scoresDao, rank);
        } catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(null, "Error saving score: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      });

      closeButton.addActionListener(e -> dispose());

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
