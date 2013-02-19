package com.plusonalabs.dashclock.gerrit.util;

public class UrlUtil {

	public static String appendPath(String url, String path) {
		String result = url;
		if (!result.endsWith("/") && !path.startsWith("/")) {
			result += "/" + path;
		} else if (result.endsWith("/") && path.startsWith("/")) {
			result += path.substring(1);
		} else {
			result += path;
		}
		return result;
	}
}
