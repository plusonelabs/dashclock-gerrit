package com.plusonelabs.dashclock.gerrit.auth;

import com.github.kevinsawicki.http.HttpRequest;
import com.plusonelabs.dashclock.gerrit.GerritEndpoint;

public interface AuthenticationProvider {

	void preRequest(GerritEndpoint endpoint);

	void supplyCredentials(HttpRequest request, GerritEndpoint endpoint);

	String appendQueryToChangesUrl(String url);

}
