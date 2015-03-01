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
import util.FormatUtils;

/**
 * Provides an interface to the score database
 * @author Tyler
 *
 */
public final class DB {
	
	static { // Statically load DB driver upon class load
		try { Class.forName("com.mysql.jdbc.Driver"); }
		catch (ClassNotFoundException e) {} // Won't happen
	}
	
	// Associates the scoreID primary key from the score DB with that score's overall rank
	private static Map<Integer, Integer> _scoreIDToRank = null;
	
	private static Connection getConnection() throws SQLException {
		
		String dbHost = Properties.getDBHostProperty();
		String dbName = Properties.getDBNameProperty();
		String dbUser = Properties.getDBUserProperty();
		String dbPass = Properties.getDBPassProperty();
		
		return DriverManager.getConnection(String.format("jdbc:mysql://%s/%s", dbHost, dbName), dbUser, dbPass);
		
	}
	
	/**
	 * Writes a new score record to the database.
	 * 
	 * @param name Player name
	 * @param score Player score
	 * @param level Level attained
	 * @param lines Total lines cleared
	 * @param difficulty Game difficulty
	 * @return The overall rank for this new score
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static int writeScore(String name, int score, int level, int lines, int difficulty, long gameTime) throws ClassNotFoundException, SQLException {
		
		Connection conn = null;
		
		try {
				
			conn = getConnection();
			
			Statement stmt = conn.createStatement();
			
			stmt.executeUpdate(new StringBuilder()
				.append("insert into score ")
				.append("(playerName, playerScore, playerLevel, playerLines, playerDifficulty, gameTime) values ")
				.append(String.format("('%s', %d, %d, %d, %d, %d);", name, score, level, lines, difficulty, gameTime))
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
	
	private static void updateScoreIDMap(Connection conn) throws SQLException {
		
		ResultSet allScores = conn.createStatement().executeQuery("select scoreID from score order by playerScore desc;");
		
		_scoreIDToRank = new HashMap<>();
		
		for (int rank = 1; allScores.next(); rank++)
			_scoreIDToRank.put(allScores.getInt(1), rank);
			
	}
	
	/**
	 *  Pulls scores from the DB and returns the corresponding object matrix for the data.
	 *  
	 * @param numScores Number of records to pull for specified difficulty
	 * @param difficulty Difficulty to query for
	 * @return An object matrix of the query results
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Object[][] getHighScoresData(int numScores, int difficulty) throws ClassNotFoundException, SQLException {
		
		Connection conn = null;
		
		try {
			
			conn = getConnection();
			
			updateScoreIDMap(conn);
			
			ResultSet scores = conn.createStatement().executeQuery(createScoresQuery(numScores, difficulty));
			
			List<Object[]> data = new ArrayList<>();
			
			// Populate data
			while (scores.next()) {
				
				data.add(new Object[]{
					_scoreIDToRank.get(scores.getInt(1)), // Score ID, pulled from map
					scores.getString(2), // Player name
					scores.getInt(3), // Score
					scores.getInt(4), // Level
					scores.getInt(5) == 11 ? "Complete" : scores.getInt(5), // Lines
					SettingsPanel.DIFFICULTIES[scores.getInt(6)], // Difficulty string
					FormatUtils.millisToString(scores.getInt(7)), // Game time
				});
				
			}
			
			return data.toArray(new Object[data.size()][scores.getMetaData().getColumnCount()]);
			
		}
		finally {
			if (conn != null) conn.close();
		}
		
	}
	
	/**
	 *  Constructs the appropriate SQL query based on the desired number of scores and difficulty
	 * @param numScores Number of scores to query for
	 * @param difficulty Difficulty to query for
	 * @return A SQL query that will retrieve the appropriate scores
	 */
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
