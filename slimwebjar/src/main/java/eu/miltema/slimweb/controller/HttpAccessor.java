package eu.miltema.slimweb.controller;

import javax.servlet.http.*;

abstract public class HttpAccessor {

	private static final String SESSION_OBJECT = "__SESSION_OBJECT";
	private static final String SESSION_LANGUAGE = "__SESSION_LANGUAGE";

	public HttpServletRequest request;
	public HttpServletResponse response;
	private String pi;
	private String pathInfo[];
	private String httpMethod;

	public HttpAccessor init(HttpServletRequest request) {
		this.request = request;
		return this;
	}

	public HttpAccessor init(HttpServletRequest request, HttpServletResponse response, String httpMethod) {
		this.request = request;
		this.response = response;
		this.httpMethod = httpMethod;
		this.pi = request.getPathInfo();
		return this;
	}

	/**
	 * @return current component name from URL path
	 */
	public String getComponentName() {
		if (pathInfo == null) {
			if (pi == null)
				throw new HttpException(404, "Missing component name in URL");
			pathInfo = request.getPathInfo().split("/");//pi[0] will become empty because of leading slash
		}
		return pathInfo[1];
	}

	/**
	 * @return current action name from URL path
	 */
	public String getActionName() {
		return (pathInfo.length > 2 ? pathInfo[2] : null);
	}

	/**
	 * @return request URL
	 */
	public String getUrl() {
		String q = request.getQueryString();
		return request.getServletPath() + (pi == null ? "" : pi) + (q == null ? "" : "?" + q);
	}

	/**
	 * @return http method (get, post, put, delete)
	 */
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

	/**
	 * @return session object
	 */
	public Object getSessionObject() {
		HttpSession session = request.getSession(false);
		return (session == null ? null : session.getAttribute(SESSION_OBJECT));
	}

	public void terminateSession() {
		HttpSession session = request.getSession(false);
		if (session != null)
			session.invalidate();
	}

	/**
	 * @return language in session; if no language in session, then return language from http header Accept-Language; if nothing there, then return "en"
	 */
	public String getLanguage() {
		HttpSession session = request.getSession(false);
		String language = (session == null ? request.getLocale().getLanguage() : (String) session.getAttribute(SESSION_LANGUAGE));
		return (language == null ? "en" : language);
	}

	/**
	 * Set language in session
	 * @param language language id, for example "en"
	 * @throws IllegalStateException when session does not exist
	 */
	public void setLanguage(String language) throws IllegalStateException {
		HttpSession session = request.getSession(false);
		if (session == null)
			throw new IllegalStateException("Missing session");
		session.setAttribute(SESSION_LANGUAGE, language);
	}

	abstract public String getParametersAsJson();
	abstract public String getParameter(String parameterName);
}
