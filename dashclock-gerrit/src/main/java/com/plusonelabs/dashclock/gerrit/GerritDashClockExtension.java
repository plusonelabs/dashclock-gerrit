package com.plusonelabs.dashclock.gerrit;

import static android.content.Intent.*;
import static com.plusonelabs.dashclock.gerrit.prefs.GerritPreferences.*;
import static com.plusonelabs.dashclock.gerrit.util.UrlUtil.*;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.plusonelabs.dashclock.gerrit.auth.AnonymousAuthenticationProvider;
import com.plusonelabs.dashclock.gerrit.auth.AuthenticationProvider;
import com.plusonelabs.dashclock.gerrit.auth.BasicAuthWithCookieAuthenticationProvider;
import com.plusonelabs.dashclock.gerrit.prefs.GerritPreferences;

public class GerritDashClockExtension extends DashClockExtension {

	private Gerrit gerrit;
	private GerritEndpoint endpoint;

	@Override
	protected void onInitialize(boolean isReconnect) {
		endpoint = new GerritEndpoint(getSecurePreferences(getApplicationContext()));
		gerrit = new Gerrit(endpoint);
		setUpdateWhenScreenOn(true);
	}

	@Override
	protected void onUpdateData(int reason) {
		try {
			setupGerritAuthentication();
			publishGerritState();
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.e(GerritDashClockExtension.class.getSimpleName(),
						"A problem occured while populating the gerrit dashclock extension.", e);
			}
		}
	}

	private void setupGerritAuthentication() {
		AuthenticationProvider authenticationProvider = null;
		if (endpoint.getUsername() == null) {
			authenticationProvider = new AnonymousAuthenticationProvider();
		} else {
			authenticationProvider = new BasicAuthWithCookieAuthenticationProvider();
		}
		gerrit.setAuthenticationProvider(authenticationProvider);
	}

	private void publishGerritState() {
		ExtensionData extensionData = new ExtensionData();
		extensionData.visible(false);
		if (endpoint.getUrl() != null) {
			fetchFromGerrit(extensionData);
		}
		publishUpdate(extensionData);
	}

	private void fetchFromGerrit(ExtensionData extensionData) {
		gerrit.fetchChanges();
		String numberOfChanges = createQuantitiyString(gerrit.getAllChanges(),
				R.plurals.numberOfChanges);
		String numberOfAssignedChanges = createQuantitiyString(gerrit.getAssignedChanges(),
				R.plurals.numberOfAssignedChanges);
		setStatus(extensionData, numberOfChanges, numberOfAssignedChanges);
	}

	private void setStatus(ExtensionData extensionData, String numberOfChanges,
			String numberOfAssignedChanges) {
		SharedPreferences prefs = GerritPreferences.getSharedPreferences(getApplicationContext());
		String changesScope = prefs.getString(DISPLAY_CHANGES_SCOPE, DISPLAY_CHANGES_SCOPE_ALL);
		if (changesScope.equals(DISPLAY_CHANGES_SCOPE_ALL) && !gerrit.getAllChanges().isEmpty()) {
			setAllChangesDetails(extensionData, numberOfChanges, numberOfAssignedChanges);
		} else if (changesScope.equals(DISPLAY_CHANGES_SCOPE_ASSIGNED)
				&& !gerrit.getAssignedChanges().isEmpty()) {
			setAssignedChangesDetails(extensionData, numberOfChanges, numberOfAssignedChanges);
		}
	}

	private void setAllChangesDetails(ExtensionData extensionData, String numberOfChanges,
			String numberOfAssignedChanges) {
		extensionData.visible(true).icon(R.drawable.ic_extension_icon)
				.status(String.valueOf(gerrit.getAllChanges().size()))
				.expandedTitle(numberOfChanges).expandedBody(numberOfAssignedChanges);
		setClickIntent(gerrit.getAllChanges(), extensionData);
	}

	private void setAssignedChangesDetails(ExtensionData extensionData, String numberOfChanges,
			String numberOfAssignedChanges) {
		extensionData.visible(true).icon(R.drawable.ic_extension_icon)
				.status(String.valueOf(gerrit.getAssignedChanges().size()))
				.expandedTitle(numberOfAssignedChanges).expandedBody(numberOfChanges);
		setClickIntent(gerrit.getAllChanges(), extensionData);
	}

	private String createQuantitiyString(List<Change> changes, int plural) {
		Resources res = getResources();
		int changesCount = changes.size();
		return res.getQuantityString(plural, changesCount, changesCount);
	}

	private void setClickIntent(List<Change> changes, ExtensionData extensionData) {
		String changeUrl = endpoint.getUrl();
		if (changes.size() == 1) {
			changeUrl = appendPath(changeUrl, "/#/c/" + changes.get(0).getChangeId() + "/");
		} else {
			changeUrl = appendPath(changeUrl, "/#/q/status:open,n,z");
		}
		extensionData.clickIntent(new Intent(ACTION_VIEW, Uri.parse(changeUrl)));
	}
}
