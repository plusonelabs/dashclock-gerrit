package com.plusonelabs.dashclock.gerrit.auth;

import static com.plusonelabs.dashclock.gerrit.util.UrlUtil.*;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import com.github.kevinsawicki.http.HttpRequest;
import com.plusonelabs.dashclock.gerrit.GerritEndpoint;

public class BasicAuthWithCookieAuthenticationProvider implements AuthenticationProvider {

	@Override
	public void preRequest(GerritEndpoint endpoint) {
		initCookieManager();
		performAuthenticationRequest(endpoint);
	}

	private void initCookieManager() {
		CookieManager manager = new CookieManager();
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
	}

	private void performAuthenticationRequest(GerritEndpoint endpoint) {
		String loginUrl = appendPath(endpoint.getUrl(), "login/");
		HttpRequest request = createRequest(endpoint, loginUrl);
		processRequest(endpoint, request);
	}

	private HttpRequest createRequest(GerritEndpoint endpoint, String loginUrl) {
		HttpRequest request = HttpRequest.get(loginUrl);
		request.acceptGzipEncoding().uncompress(true).trustAllCerts().trustAllHosts();
		request.basic(endpoint.getUsername(), endpoint.getPassword());
		return request;
	}

	private void processRequest(GerritEndpoint endpoint, HttpRequest request) {
		if (!request.ok()) {
			throw new IllegalStateException("Could not authenticate at Gerrit server "
					+ endpoint.getUrl());
		}
	}

	@Override
	public void supplyCredentials(HttpRequest request, GerritEndpoint endpoint) {
		request.basic(endpoint.getUsername(), endpoint.getPassword());
	}

	@Override
	public String appendQueryToChangesUrl(String url) {
		return url += "?q=is:open&q=is:open+reviewer:self";
	}
}
