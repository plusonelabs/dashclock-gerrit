package com.plusonelabs.dashclock.gerrit.prefs;

import static com.plusonelabs.dashclock.gerrit.prefs.GerritPreferences.*;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.plusonelabs.dashclock.gerrit.R;

public class ServerFragment extends Fragment {

	private SecurePreferences prefs;
	private EditText textServerUrl;
	private EditText textUsername;
	private EditText textPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = GerritPreferences.getSecurePreferences(getActivity().getApplicationContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.settings_server, null, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		textServerUrl = (EditText) getActivity().findViewById(R.id.settings_server_server_url);
		textUsername = (EditText) getActivity().findViewById(R.id.settings_server_username);
		textPassword = (EditText) getActivity().findViewById(R.id.settings_server_password);
	}

	@Override
	public void onResume() {
		super.onResume();
		textServerUrl.setText(prefs.getString(SERVER_URL));
		textUsername.setText(prefs.getString(SERVER_USERNAME));
		textPassword.setText(prefs.getString(SERVER_PASSWORD));
	}

	@Override
	public void onPause() {
		super.onPause();
		putInPreferences(SERVER_URL, getText(textServerUrl));
		putInPreferences(SERVER_USERNAME, getText(textUsername));
		putInPreferences(SERVER_PASSWORD, getText(textPassword));
	}

	private void putInPreferences(String key, String value) {
		if (value == null || value.isEmpty()) {
			prefs.removeValue(key);
		} else {
			prefs.put(key, value);
		}
	}

	private String getText(EditText editText) {
		String text = editText.getText().toString().trim();
		return text;
	}
}
