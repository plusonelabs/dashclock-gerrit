package com.plusonelabs.dashclock.gerrit;

import static android.content.Intent.*;
import static com.plusonelabs.dashclock.gerrit.prefs.GerritPreferences.*;
import static com.plusonelabs.dashclock.gerrit.util.StringUtil.*;
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
			publishGerritState();
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.e(GerritDashClockExtension.class.getSimpleName(),
						"A problem occured while populating the gerrit dashclock extension.", e);
			}
		}
	}

	private void publishGerritState() {
		ExtensionData extensionData = new ExtensionData();
		extensionData.visible(false);
		if (endpoint.getUrl() != null) {
			fetchFromGerrit();
			updateStatus(extensionData);
		}
		publishUpdate(extensionData);
	}

	private void fetchFromGerrit() {
		SharedPreferences prefs = GerritPreferences.getSharedPreferences(getApplicationContext());
		String project = prefs.getString(GerritPreferences.DISPLAY_FILTER_PROJECT, null);
		String branch = prefs.getString(GerritPreferences.DISPLAY_FILTER_BRANCH, null);
		String reviewer = prefs.getString(GerritPreferences.DISPLAY_REVIEWER, null);
		gerrit.fetchChanges(createAuthenticationProvider(), project, branch, reviewer);
	}

	private AuthenticationProvider createAuthenticationProvider() {
		if (hasContent(endpoint.getUsername())) {
			return new BasicAuthWithCookieAuthenticationProvider();
		}
		return new AnonymousAuthenticationProvider();
	}

	private void updateStatus(ExtensionData extensionData) {
		SharedPreferences prefs = GerritPreferences.getSharedPreferences(getApplicationContext());
		String total = createQuantitiy(gerrit.getAllChanges(), R.plurals.changes);
		String assigned = createQuantitiy(gerrit.getAssignedChanges(), R.plurals.assignedChanges);
		String changesScope = prefs.getString(DISPLAY_CHANGES_SCOPE, DISPLAY_CHANGES_SCOPE_ALL);
		if (changesScope.equals(DISPLAY_CHANGES_SCOPE_ALL) && !gerrit.getAllChanges().isEmpty()) {
			setAllChangesDetails(extensionData, total, assigned);
		} else if (changesScope.equals(DISPLAY_CHANGES_SCOPE_ASSIGNED)
				&& !gerrit.getAssignedChanges().isEmpty()) {
			setAssignedChangesDetails(extensionData, total, assigned);
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

	private String createQuantitiy(List<Change> changes, int plural) {
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
