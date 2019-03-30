package eu.miltema.slimweb.controller;

import javax.servlet.http.*;

abstract class HttpAccessor {
	public HttpServletRequest request;
	public HttpServletResponse response;
	private String pathInfo[];

	public HttpAccessor init(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		pathInfo = request.getPathInfo().split("/");//pi[0] will become empty because of leading slash
		return this;
	}

	public String getComponentName() {
		return pathInfo[1];
	}

	public String getActionName() {
		return (pathInfo.length > 2 ? pathInfo[2] : null);
	}

	abstract public String getParametersAsJson();
	abstract public String getParameter(String parameterName);
}
