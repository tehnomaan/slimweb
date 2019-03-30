package eu.miltema.slimweb.controller;

import javax.servlet.http.*;

abstract class HttpAccessor {
	public HttpServletRequest request;
	public HttpServletResponse response;
	private String pi;
	private String pathInfo[];

	public HttpAccessor init(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.pi = request.getPathInfo();
		return this;
	}

	public String getComponentName() {
		if (pathInfo == null) {
			if (pi == null)
				throw new HttpException(404, "Missing component name in URL");
			pathInfo = request.getPathInfo().split("/");//pi[0] will become empty because of leading slash
		}
		return pathInfo[1];
	}

	public String getActionName() {
		return (pathInfo.length > 2 ? pathInfo[2] : null);
	}

	public String getUrl() {
		String q = request.getQueryString();
		return request.getServletPath() + (pi == null ? "" : pi) + (q == null ? "" : "?" + q);
	}

	abstract public String getParametersAsJson();
	abstract public String getParameter(String parameterName);
}
