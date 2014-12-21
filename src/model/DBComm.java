package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ui.SettingsPanel;

// Interfaces to the scores database
public class DBComm {
	
	private static Map<Integer, Integer> scoreIDToRank = null;
	
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
			
			updateScoreIDMap(conn);
			
			rank.next();
			return rank.getInt(1);
			
		}
		finally {
			if (conn != null) conn.close();
		}		
		
	}
	
	// Updates the mapping of score IDs to overall rank. Used after a new score is added
	private static void updateScoreIDMap(Connection conn) throws SQLException {
		
		ResultSet allScores = conn.createStatement().executeQuery("select scoreID from score order by playerScore desc;");
		
		scoreIDToRank = new HashMap<>();
		
		for (int rank = 1; allScores.next(); rank++)
			scoreIDToRank.put(allScores.getInt(1), rank);
			
	}
	
	// Pulls scores from the DB and returns the corresponding object matrix for the data
	public static Object[][] getHighScoresData(int numScores, int difficulty) throws ClassNotFoundException, SQLException {
		
		Connection conn = null;
		
		try {
			
			conn = getConnection();
			
			if (scoreIDToRank == null) updateScoreIDMap(conn);
			
			ResultSet scores = conn.createStatement().executeQuery(createScoresQuery(numScores, difficulty));
			
			int colCount = scores.getMetaData().getColumnCount();
			List<Object[]> data = new ArrayList<>();
			
			// Populate data
			while (scores.next()) {
				
				Object[] rowData = new Object[colCount];
				
				rowData[0] = scoreIDToRank.get(scores.getInt(1)); // Pull rank from map
				rowData[1] = scores.getString(2); // Name
				rowData[2] = scores.getInt(3); // Score
				rowData[3] = scores.getInt(4); // Lines
				
				int level = scores.getInt(5);				
				int diff = scores.getInt(6);
				
				rowData[4] = level == 11 ? "Complete" : level; // Level
				rowData[5] = SettingsPanel.DIFFICULTIES[diff]; // Difficulty
				
				data.add(rowData);
				
			}
			
			return data.toArray(new Object[data.size()][colCount]);
			
		}
		finally {
			if (conn != null) conn.close();
		}
		
	}
	
	// Constructs the appropriate SQL query based on the desired number of scores and difficulty
	private static String createScoresQuery(int numScores, int difficulty) {
		
		StringBuilder query = new StringBuilder();
		
		// Initial query statement
		query.append("select * from score ");
		
		// Add in difficulty restrictions for all selections but '3' (All)
		if (difficulty != 3) query.append("where playerDifficulty = " + difficulty + " ");
		
		// Ordering and record count limiting
		query.append("order by playerScore desc limit " + numScores + ";");
		
		return query.toString();
		
	}
	
}
