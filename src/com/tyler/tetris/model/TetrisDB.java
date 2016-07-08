package com.tyler.tetris.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tyler.tetris.util.FormatUtils;

/**
 * Provides an interface to the score database
 * @author Tyler
 *
 */
public final class TetrisDB {
	
	private static Class<?> mySQLDriver;
	private String dbHost, dbName, dbUser, dbPass;
	
	public TetrisDB(String dbHost, String dbName, String dbUser, String dbPass) {
		this.dbHost = dbHost;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPass = dbPass;
	}
	
	private Connection getConnection() throws SQLException {
		if (mySQLDriver == null) {
			try {
				mySQLDriver = Class.forName("com.mysql.jdbc.Driver");
			}
			catch (ClassNotFoundException e) {
				throw new SQLException("MySQL driver not found - you will not be able to save scores");
			}
		}
		return DriverManager.getConnection(String.format("jdbc:mysql://%s/%s", dbHost, dbName), dbUser, dbPass);
		
	}
	
	public int writeScore(String name, int score, int level, int lines, int difficulty, long gameTime) throws ClassNotFoundException, SQLException {
		
		try (Connection conn = getConnection()) {
				
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
			
			rank.next();
			return rank.getInt(1);
		}
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
	public List<String[]> getHighScores(int numScores, Optional<Integer> difficulty) throws SQLException {
		
		try (Connection conn = getConnection()) {
			
			ResultSet scores = conn.createStatement().executeQuery(
				"select * from scores s " +
				(difficulty.isPresent() ? " where player_difficulty = " + difficulty.get() : "") +
				"order by player_score desc limit " + numScores
			);
			
			List<String[]> data = new ArrayList<>();
			
			// Populate data
			while (scores.next()) {
				data.add(new String[]{
					scores.getString(2), // Player name
					scores.getString(3), // Score
					scores.getString(4), // Level
					scores.getInt(5) == ScoreModel.MAX_LEVEL ? "Complete" : scores.getString(5), // Lines
					scores.getString(6), // Difficulty
					FormatUtils.millisToString(scores.getInt(7)), // Game time
				});
			}
			
			return data;
		}
		
	}
	
}
