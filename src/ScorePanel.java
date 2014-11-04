
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

// Holds all scoring info
@SuppressWarnings("serial")
public class ScorePanel extends JPanel {
	
	private JLabel scoreLabel = new JLabel("Score: 0", JLabel.CENTER);
	private JLabel totalLinesLabel = new JLabel("Lines: 0", JLabel.CENTER);
	private JLabel levelLabel = new JLabel("Level: 1", JLabel.CENTER);
	
	public ScorePanel() {
		
		setLayout(new GridLayout(3,1));
		
		scoreLabel.setFont(GameFrame.LABEL_FONT);
		totalLinesLabel.setFont(GameFrame.LABEL_FONT);
		levelLabel.setFont(GameFrame.LABEL_FONT);
		
		add(scoreLabel);
		add(totalLinesLabel);
		add(levelLabel);
		
	}
	
	public void refreshScoreInfo() {
		scoreLabel.setText("Score: " + GameBoardModel.getScore());
		totalLinesLabel.setText("Lines: " + GameBoardModel.getLinesCompleted());
		levelLabel.setText("Level: " + GameBoardModel.getLevel());
	}
	
}
