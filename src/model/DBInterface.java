package model;

// Interface to the scores database
public class DBInterface {
	
	public static void writeScore(String name, int score) {
		
		// In case the player left the name input field blank (-_-)
		if (name.equals("")) name = "Unspecified";
		
	}
}
