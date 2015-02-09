package util;

/**
 * Utility class for formatting data
 * @author Tyler
 */
public class FormatUtils {
	
	private final static int MILLIS_PER_MINUTE = 60000;
	
	/**
	 * Converts the specifiedd milliseconds to a string representation
	 * in the format '[minutes]:[seconds]'. Each section (minutes, seconds)
	 * will be at most 2 digits long
	 */
	public static String millisToString(long millis) {
		
		String totalMinutes = String.valueOf(millis / MILLIS_PER_MINUTE);
		String totalSeconds = String.valueOf(millis % MILLIS_PER_MINUTE / 1000);
		
		// Pad with opening zero if necessary
		if (totalMinutes.length() == 1) totalMinutes = "0" + totalMinutes;
		if (totalSeconds.length() == 1) totalSeconds = "0" + totalSeconds;
		
		return totalMinutes + ":" + totalSeconds;
		
	}
	
}
