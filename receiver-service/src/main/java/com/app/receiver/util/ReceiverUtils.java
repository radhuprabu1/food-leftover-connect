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
	 * @param lat1 Latitude of point 1 (in decimal degrees)
	 * @param lon1 Longitude of point 1 (in decimal degrees)
	 * @param lat2 Latitude of point 2 (in decimal degrees)
	 * @param lon2 Longitude of point 2 (in decimal degrees)
	 * @return The distance in kilometers, or null if any input is null or invalid.
	 */
	public static Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
		// Validate inputs
		if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
			return null;
		}
		if (!isValidLatitude(lat1) || !isValidLatitude(lat2) || !isValidLongitude(lon1) || !isValidLongitude(lon2)) {
			return null;
		}

		final double R = 6371.0; // Radius of the Earth in km
		double lat1Rad = Math.toRadians(lat1);
		double lat2Rad = Math.toRadians(lat2);
		double deltaLat = Math.toRadians(lat2 - lat1);
		double deltaLon = Math.toRadians(lon2 - lon1);

		double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
				+ Math.cos(lat1Rad) * Math.cos(lat2Rad)
				* Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return R * c;
	}

	private static boolean isValidLatitude(Double lat) {
		return lat >= -90 && lat <= 90;
	}

	private static boolean isValidLongitude(Double lon) {
		return lon >= -180 && lon <= 180;
	}

}
