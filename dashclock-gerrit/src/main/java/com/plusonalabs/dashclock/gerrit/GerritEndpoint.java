package com.plusonalabs.dashclock.gerrit;

import static com.plusonalabs.dashclock.gerrit.prefs.GerritPreferences.*;

import com.plusonalabs.dashclock.gerrit.prefs.SecurePreferences;

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
