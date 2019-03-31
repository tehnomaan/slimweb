package eu.miltema.slimweb.controller;

import javax.servlet.http.*;

abstract public class HttpAccessor {

	private static final String SESSION_OBJECT = "__SESSION_OBJECT";

	public HttpServletRequest request;
	public HttpServletResponse response;
	private String pi;
	private String pathInfo[];
	private String httpMethod;

	public HttpAccessor init(HttpServletRequest request, HttpServletResponse response, String httpMethod) {
		this.request = request;
		this.response = response;
		this.httpMethod = httpMethod;
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

	public String getMethod() {
		return httpMethod;
	}

	/**
	 * Update existing or create new session object
	 * @param sessionObject custom session object
	 */
	public void setSessionObject(Object sessionObject) {
		HttpSession session = request.getSession();
		session.setAttribute(SESSION_OBJECT, sessionObject);
	}

	public Object getSessionObject() {
		HttpSession session = request.getSession(false);
		return (session == null ? null : session.getAttribute(SESSION_OBJECT));
	}

	abstract public String getParametersAsJson();
	abstract public String getParameter(String parameterName);
}
