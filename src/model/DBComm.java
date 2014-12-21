package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ui.SettingsPanel;

// Interfaces to the scores database
public class DBComm {
	
	private final static String DB_HOST_NAME = "localhost";
	
	// I probably shouldn't store these in my source code, but this works
	// for now. It's just a school project, after all
	private final static String DB_USER = "root";
	private final static String DB_PASS = "TyDaWi@timpfmys!";
	
	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://" + DB_HOST_NAME + "/tetris", DB_USER, DB_PASS);
	}
	
	// Returns the rank for the new score
	public static int writeScore(String name, int score, int level, int lines, int difficulty) throws ClassNotFoundException, SQLException {
		
		Connection conn = null;
		
		try {
				
			conn = getConnection();
			
			Statement stmt = conn.createStatement();
			
			stmt.executeUpdate(new StringBuilder()
				.append("insert into score ")
				.append("(playerName, playerScore, playerLevel, playerLines, playerDifficulty) values ")
				.append(String.format("('%s', %d, %d, %d, %d);", name, score, level, lines, difficulty))
				.toString()
			);
			
			ResultSet rank = stmt.executeQuery(new StringBuilder()
				.append("select count(*) from score ")
				.append("where playerScore >= " + score + ";")
				.toString()
			);
			
			rank.next();
			return rank.getInt(1);
			
		}
		finally {
			if (conn != null) conn.close();
		}		
		
	}
	
	// Pulls scores from the DB and returns the corresponding object matrix for the data
	public static Object[][] getHighScoresData(int numScores, int difficulty) throws ClassNotFoundException, SQLException {
		
		Connection conn = null;
		
		try {
			
			conn = getConnection();
			
			ResultSet scores = conn.createStatement().executeQuery(createScoresQuery(numScores, difficulty));
			
			// Obtain column count and use it to initialize object array.
			// Add 1 to make room for the "Rank" column
			int colCount = scores.getMetaData().getColumnCount();
			List<Object[]> data = new ArrayList<>();
			
			// Populate data
			for (int rank = 1; scores.next(); rank++) {
				
				Object[] rowData = new Object[colCount+1];
				
				rowData[0] = rank;
				rowData[1] = scores.getString(1); // Name
				rowData[2] = scores.getString(2); // Score
				rowData[3] = scores.getString(3); // Lines
				
				int level = scores.getInt(4);				
				int diff = scores.getInt(5);
				
				rowData[4] = level == 11 ? "Complete" : level; // Level
				rowData[5] = SettingsPanel.DIFFICULTIES[diff]; // Difficulty
				
				data.add(rowData);
				
			}
			
			return data.toArray(new Object[data.size()][colCount+1]);
			
		}
		finally {
			if (conn != null) conn.close();
		}
		
	}
	
	// Constructs the appropriate SQL query based on the desired number of scores and difficulty
	private static String createScoresQuery(int numScores, int difficulty) {
		
		StringBuilder query = new StringBuilder();
		
		// Initial query statement
		query.append("select playerName, playerScore, playerLines, playerLevel, playerDifficulty from score ");
		
		// Add in difficulty restrictions for all selections but '3' (All)
		if (difficulty != 3) query.append("where playerDifficulty = " + difficulty + " ");
		
		// Ordering and record count limiting
		query.append("order by playerScore desc limit " + numScores + ";");
		
		return query.toString();
		
	}
	
}
