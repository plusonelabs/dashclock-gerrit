package com.plusonelabs.dashclock.gerrit;

import static com.plusonelabs.dashclock.gerrit.util.ParamCheck.*;
import static com.plusonelabs.dashclock.gerrit.util.StringUtil.*;
import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.plusonelabs.dashclock.gerrit.auth.AuthenticationProvider;

public class Gerrit {

	private static final String GERRIT_RESPONSE_PREPEND = "\\)\\]\\}\\'\n";

	private final GerritEndpoint server;
	private final Gson gson;
	private List<Change> allChanges;
	private List<Change> assignedChanges;

	public Gerrit(GerritEndpoint server) {
		paramNotNull(server, GerritEndpoint.class.getSimpleName());
		this.server = server;
		gson = new Gson();
		allChanges = emptyList();
		assignedChanges = emptyList();
	}

	public void fetchChanges(AuthenticationProvider authenticationProvider, String project,
			String branch, String reviewer) {
		paramNotNull(authenticationProvider, AuthenticationProvider.class.getSimpleName());
		authenticationProvider.preRequest(server);
		HttpRequest request = createRequest(authenticationProvider, project, branch, reviewer);
		authenticationProvider.supplyCredentials(request, server);
		processRequest(request);
	}

	private HttpRequest createRequest(AuthenticationProvider authProvider, String project,
			String branch, String reviewer) {
		QueryBuilder queryBuilder = new QueryBuilder(server.getUrl(), authProvider.isAnonymous());
		queryBuilder.setProject(project);
		queryBuilder.setBranch(branch);
		queryBuilder.setReviewer(reviewer);
		String queryUrl = queryBuilder.createQueryUrl();
		System.out.println(queryUrl);
		HttpRequest request = HttpRequest.get(queryUrl);
		request.acceptGzipEncoding().uncompress(true).trustAllCerts().trustAllHosts();
		request.accept(HttpRequest.CONTENT_TYPE_JSON);
		return request;
	}

	private void processRequest(HttpRequest request) {
		if (request.ok()) {
			String json = sanatizeResponse(request.body());
			deserializeToChanges(json);
		} else {
			if (BuildConfig.DEBUG) {
				Log.e(GerritDashClockExtension.class.getSimpleName(),
						"A problem occurred while populating the gerrit changes: " + request.message());
			}
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
		System.out.println(jsonArray);
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

	private String sanatizeResponse(String gerritResponse) {
		return gerritResponse.replaceFirst(GERRIT_RESPONSE_PREPEND, EMPTY_STRING);
	}

	public List<Change> getAllChanges() {
		return allChanges;
	}

	public List<Change> getAssignedChanges() {
		return assignedChanges;
	}

}
