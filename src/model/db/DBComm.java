package model.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JTable;

// Interface to the scores database
public class DBComm {
	
	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost/tetris","root","TyDaWi@timpfmys!");
	}
	
	// For writing a new score the the DB
	public static void writeScore(String name, int score, int level, int lines, int difficulty) throws ClassNotFoundException, SQLException {
		
		// In case the player left the name input field blank (-_-)
		if (name.equals("")) name = "Unspecified";
		
		Connection conn = null;
		try {
				
			conn = getConnection();
			
			PreparedStatement insert = conn.prepareStatement(
				"insert into score (playerName, playerScore, playerLevel, playerLines, playerDifficulty) values (?, ?, ?, ?, ?)");
			
			insert.setString(1, name);
			insert.setString(2, String.valueOf(score));
			insert.setString(3, String.valueOf(level));
			insert.setString(4, String.valueOf(lines));
			insert.setString(5, String.valueOf(difficulty));
			
			insert.executeUpdate();
			
		}
		finally {
			conn.close();
		}		
		
	}
	
	public static JTable getHighScoresTable(int numScores) throws ClassNotFoundException, SQLException {
		
		Connection conn = null;
		try {
			
			conn = getConnection();
			ResultSet scores = conn.createStatement().executeQuery(
				"select playerName, playerScore, playerLines, playerLevel, playerDifficulty from score order by playerScore desc limit " + numScores);
			
			// Obtain column count and use it to initialize object array
			int colCount = scores.getMetaData().getColumnCount();
			Object[][] data = new Object[numScores+1][colCount];
			
			// Populate data
			while (scores.next()) {
				
				data[scores.getRow()-1][0] = scores.getString(1); // Name
				data[scores.getRow()-1][1] = scores.getString(2); // Score
				data[scores.getRow()-1][2] = scores.getString(3); // Level
				data[scores.getRow()-1][3] = scores.getString(4); // Lines
				
				// Difficulty
				String diff = scores.getString(5);
				
				if (diff.equals("0"))
					data[scores.getRow()-1][4] = "Easy";
				else if (diff.equals("1"))
					data[scores.getRow()-1][4] = "Medium";
				else
					data[scores.getRow()-1][4] = "Hard";
				
			}
			
			return new JTable(data, new Object[]{"Name","Score","Lines","Level","Difficulty"});
			
		}
		finally {
			conn.close();
		}
		
	}
	
}
