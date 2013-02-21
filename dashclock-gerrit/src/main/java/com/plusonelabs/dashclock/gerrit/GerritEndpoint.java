package com.plusonelabs.dashclock.gerrit;

import static com.plusonelabs.dashclock.gerrit.prefs.GerritPreferences.*;

import com.plusonelabs.dashclock.gerrit.prefs.SecurePreferences;

public class GerritEndpoint {

	private final SecurePreferences prefs;

	public GerritEndpoint(SecurePreferences prefs) {
		this.prefs = prefs;
	}

	public String getUrl() {
		return prefs.getString(SERVER_URL);
	}

	public String getUsername() {
		return prefs.getString(SERVER_USERNAME);
	}

	public String getPassword() {
		return prefs.getString(SERVER_PASSWORD);
	}

}
