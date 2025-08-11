/**
 * 
 */
package com.app.receiver.util;

/**
 * Contains Utility methods for Receiver module
 *
 * @author Radhakrishnan
 * @version 1.0
 */
public class ReceiverUtils {
	
	/**
	 * Calculates the distance in kilometers between two geographical points using the Haversine formula.
	 *
	 * @param lat1 Latitude of point 1.
	 * @param lon1 Longitude of point 1.
	 * @param lat2 Latitude of point 2.
	 * @param lon2 Longitude of point 2.
	 * @return The distance in kilometers.
	 */
	public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		final int R = 6371; // Radius of the Earth in km
		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return R * c;
	}

}
