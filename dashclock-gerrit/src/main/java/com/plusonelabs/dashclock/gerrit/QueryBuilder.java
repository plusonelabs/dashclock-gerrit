package com.plusonelabs.dashclock.gerrit;

import static com.plusonelabs.dashclock.gerrit.util.ParamCheck.*;
import static com.plusonelabs.dashclock.gerrit.util.StringUtil.*;

import com.plusonelabs.dashclock.gerrit.util.UrlUtil;

public class QueryBuilder {

	private final String baseUrl;
	private final boolean anonymous;
	private String branch;
	private String project;

	public QueryBuilder(String baseUrl, boolean anonymous) {
		paramNotNull(baseUrl, "baseUrl");
		this.baseUrl = baseUrl;
		this.anonymous = anonymous;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String createQueryUrl() {
		String queryUrl = UrlUtil.appendPath(baseUrl, "changes/?");
		queryUrl += createQueryChanges();
		if (!anonymous) {
			queryUrl += "&";
			queryUrl += createQueryChanges();
			queryUrl += "+reviewer:self";
		}
		return queryUrl;
	}

	private String createQueryChanges() {
		String query = "q=is:open";
		query += createParamProject();
		query += createParamBranch();
		return query;
	}

	private String createParamProject() {
		if (hasContent(project)) {
			return "+project:" + project;
		}
		return EMPTY_STRING;
	}

	private String createParamBranch() {
		if (hasContent(branch)) {
			return "+branch:" + branch;
		}
		return EMPTY_STRING;
	}

}
