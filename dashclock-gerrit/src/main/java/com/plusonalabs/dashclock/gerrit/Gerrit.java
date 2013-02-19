package com.plusonalabs.dashclock.gerrit;

import static com.plusonalabs.dashclock.gerrit.util.ParamCheck.*;
import static com.plusonalabs.dashclock.gerrit.util.UrlUtil.*;
import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.List;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.plusonalabs.dashclock.gerrit.auth.AuthenticationProvider;

public class Gerrit {

	private final GerritEndpoint server;
	private final Gson gson;
	private AuthenticationProvider authenticationProvider;
	private List<Change> allChanges;
	private List<Change> assignedChanges;

	public Gerrit(GerritEndpoint server) {
		paramNotNull(server, GerritEndpoint.class.getSimpleName());
		this.server = server;
		gson = new Gson();
	}

	public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}

	public void fetchChanges() {
		ensureNotNull(authenticationProvider, AuthenticationProvider.class.getSimpleName());
		authenticationProvider.preRequest(server);
		HttpRequest request = createRequest();
		authenticationProvider.supplyCredentials(request, server);
		processRequest(request);
	}

	private HttpRequest createRequest() {
		String changesUrl = appendPath(server.getUrl(), "changes/");
		changesUrl = authenticationProvider.appendQueryToChangesUrl(changesUrl);
		HttpRequest request = HttpRequest.get(changesUrl);
		request.acceptGzipEncoding().uncompress(true).trustAllCerts().trustAllHosts();
		request.accept(HttpRequest.CONTENT_TYPE_JSON);
		return request;
	}

	private void processRequest(HttpRequest request) {
		if (request.ok()) {
			String json = sanatizeResponse(request.body());
			deserializeToChanges(json);
		}
	}

	private void deserializeToChanges(String json) {
		JsonElement element = new JsonParser().parse(json);
		JsonArray jsonArray = element.getAsJsonArray();
		if (jsonArray.size() > 0) {
			deserializeToChangeLists(jsonArray);
		} else {
			allChanges = emptyList();
			assignedChanges = emptyList();
		}
	}

	private void deserializeToChangeLists(JsonArray jsonArray) {
		if (jsonArray.get(0).isJsonObject()) {
			allChanges = toChangesList(jsonArray);
			assignedChanges = emptyList();
		} else {
			allChanges = toChangesList((JsonArray) jsonArray.get(0));
			assignedChanges = toChangesList((JsonArray) jsonArray.get(1));
		}
	}

	private List<Change> toChangesList(JsonArray jsonArray) {
		List<Change> result = new ArrayList<Change>();
		for (JsonElement jsonElement : jsonArray) {
			Change change = gson.fromJson(jsonElement, Change.class);
			result.add(change);
		}
		return result;
	}

	private String sanatizeResponse(String body) {
		body = body.replaceFirst("\\)\\]\\}\\'\n", "");
		return body;
	}

	public List<Change> getAllChanges() {
		return allChanges;
	}

	public List<Change> getAssignedChanges() {
		return assignedChanges;
	}

}
