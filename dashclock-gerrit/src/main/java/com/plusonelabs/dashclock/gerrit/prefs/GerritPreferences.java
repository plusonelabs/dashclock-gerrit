package com.plusonelabs.dashclock.gerrit.prefs;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

public class GerritPreferences {

	public static final String SERVER_URL = "server_url";
	public static final String SERVER_USERNAME = "server_username";
	public static final String SERVER_PASSWORD = "server_password";
	public static final String DISPLAY_CHANGES_SCOPE = "display_scope";
	public static final String DISPLAY_CHANGES_SCOPE_ALL = "display_scope_all";
	public static final String DISPLAY_CHANGES_SCOPE_ASSIGNED = "display_scope_assigned";

	private static final String SECURE_KEY = "secure_key";
	private static final String DEFAULT_SECURE_PREFERENCES_KEY = "v7Ja53ezpxgNxBPgeCQwVuoqFA9AK4ea";

	private static SecurePreferences securePreferences;

	private GerritPreferences() {
		// prohibit instantiation
	}

	public static SecurePreferences getSecurePreferences(Context context) {
		if (securePreferences == null) {
			String key = getSecurePreferencesKey(context);
			securePreferences = new SecurePreferences(context, "secure-preferences.xml", key, true);
		}
		return securePreferences;
	}

	private static String getSecurePreferencesKey(Context context) {
		String securePreferencesKey = getSharedPreferences(context).getString(SECURE_KEY, null);
		if (securePreferencesKey == null) {
			SecretKey key = generateKey();
			if (key != null) {
				String generatedKey = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
				securePreferencesKey = generatedKey + DEFAULT_SECURE_PREFERENCES_KEY;
				SharedPreferences prefs = getSharedPreferences(context);
				prefs.edit().putString(SECURE_KEY, securePreferencesKey).commit();
			}
		}
		if (securePreferencesKey == null) {
			securePreferencesKey = DEFAULT_SECURE_PREFERENCES_KEY;
		}
		return securePreferencesKey;
	}

	public static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static SecretKey generateKey() {
		try {
			// the SecureRandom is not seeded because since 4.2 it is not deterministic anymore
			SecureRandom secureRandom = new SecureRandom();
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256, secureRandom);
			SecretKey key = keyGenerator.generateKey();
			return key;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

}
