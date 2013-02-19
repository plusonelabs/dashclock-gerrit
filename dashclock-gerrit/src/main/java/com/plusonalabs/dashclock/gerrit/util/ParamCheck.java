package com.plusonalabs.dashclock.gerrit.util;

public class ParamCheck {

	public static void paramNotNull(Object object, String name) {
		if (object == null) {
			throw new IllegalArgumentException("The parameter " + name + " can not be null.");
		}
	}

	public static void ensureNotNull(Object object, String name) {
		if (object == null) {
			throw new IllegalStateException("The value " + name + " can not be null.");
		}
	}
}
