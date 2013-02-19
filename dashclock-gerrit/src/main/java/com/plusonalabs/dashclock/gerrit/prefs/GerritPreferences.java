package com.plusonalabs.dashclock.gerrit.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GerritPreferences {

	public static final String SERVER_URL = "server_url";
	public static final String SERVER_USERNAME = "server_username";
	public static final String SERVER_PASSWORD = "server_password";
	public static final String DISPLAY_CHANGES_SCOPE = "display_scope";
	public static final String DISPLAY_CHANGES_SCOPE_ALL = "display_scope_all";
	public static final String DISPLAY_CHANGES_SCOPE_ASSIGNED = "display_scope_assigned";

	private GerritPreferences() {
		// prohibit instantiation
	}

	public static SecurePreferences getSecurePreferences(Context context) {
		return new SecurePreferences(context, "SomeRandomKey", "secure-preferences.xml", true);
	}

	public static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

}
