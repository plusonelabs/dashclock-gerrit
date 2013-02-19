package com.plusonalabs.dashclock.gerrit.prefs;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.plusonalabs.dashclock.gerrit.R;

public class DisplayFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.display_settings);
	}
}
