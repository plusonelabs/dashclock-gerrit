package com.plusonelabs.dashclock.gerrit.util;

public class StringUtil {

	public static final String EMPTY_STRING = "";

	public static boolean hasContent(String string) {
		if (string != null) {
			return !string.trim().equals(EMPTY_STRING);
		}
		return false;
	}

}
