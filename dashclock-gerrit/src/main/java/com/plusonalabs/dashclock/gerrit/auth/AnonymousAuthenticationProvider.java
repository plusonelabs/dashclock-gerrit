package com.plusonalabs.dashclock.gerrit.auth;

import com.github.kevinsawicki.http.HttpRequest;
import com.plusonalabs.dashclock.gerrit.GerritEndpoint;

public class AnonymousAuthenticationProvider implements AuthenticationProvider {

	@Override
	public void preRequest(GerritEndpoint endpoint) {
		// nothing to do here
	}

	@Override
	public void supplyCredentials(HttpRequest request, GerritEndpoint endpoint) {
		// nothing to do here
	}

	@Override
	public String appendQueryToChangesUrl(String url) {
		return url;
	}
}
