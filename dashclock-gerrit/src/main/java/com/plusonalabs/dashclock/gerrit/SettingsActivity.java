package com.plusonalabs.dashclock.gerrit;

import java.util.List;

import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preferences_header, target);
	}
}
