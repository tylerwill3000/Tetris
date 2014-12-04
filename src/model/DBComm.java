package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Interfaces to the scores database
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
	
	// Pulls scores from the DB and returns the corresponding object matrix for the data
	public static Object[][] getHighScoresData(int numScores) throws ClassNotFoundException, SQLException {
		
		Connection conn = null;
		try {
			
			conn = getConnection();
			ResultSet scores = conn.createStatement().executeQuery(
				"select playerName, playerScore, playerLines, playerLevel, playerDifficulty from score order by playerScore desc limit " + numScores);
			
			// Obtain column count and use it to initialize object array
			int colCount = scores.getMetaData().getColumnCount();
			Object[][] data = new Object[numScores][colCount];
			
			// Populate data
			while (scores.next()) {
				
				int currentRow = scores.getRow()-1;
				
				// colCount-2 since difficulty / levels completed are handled separately
				for (int col = 0; col < colCount-2; col++)
					data[currentRow][col] = scores.getString(col+1);
				
				String level = scores.getString(4);				
				String diff = scores.getString(5);
				
				// Level
				data[currentRow][3] = level.equals("11") ? "Complete" : level;
				
				// Difficulty
				if (diff.equals("0"))
					data[currentRow][4] = "Easy";
				else if (diff.equals("1"))
					data[currentRow][4] = "Medium";
				else
					data[currentRow][4] = "Hard";
				
			}
			
			return data;
			
		}
		finally {
			conn.close();
		}
		
	}
	
}
