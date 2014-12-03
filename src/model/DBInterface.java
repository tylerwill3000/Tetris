package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Interface to the scores database
public class DBInterface {
	
	public static void writeScore(String name, int score, int level, int lines) throws ClassNotFoundException, SQLException {
		
		// In case the player left the name input field blank (-_-)
		if (name.equals("")) name = "Unspecified";
		
		Class.forName("com.mysql.jdbc.Driver");
		
		Connection conn = null;
		
		try {
				
			conn = DriverManager.getConnection("jdbc:mysql://localhost/tetris","root","TyDaWi@timpfmys!");
			
			PreparedStatement insert = conn.prepareStatement("insert into score (playerName, playerScore, playerLevel, playerLines) values (?, ?, ?, ?)");
			insert.setString(1, name);
			insert.setString(2, String.valueOf(score));
			insert.setString(3, String.valueOf(level));
			insert.setString(4, String.valueOf(lines));
			insert.executeUpdate();
			
		}
		finally {
			conn.close();
		}		
		
	}
	
}
