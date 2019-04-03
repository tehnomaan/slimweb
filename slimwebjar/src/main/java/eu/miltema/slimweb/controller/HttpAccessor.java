package eu.miltema.slimweb.controller;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;

abstract public class HttpAccessor {

	private static final String SESSION_OBJECT = "__SESSION_OBJECT";
	private static final String SESSION_LANGUAGE = "__SESSION_LANGUAGE";

	private static final Logger log = LoggerFactory.getLogger(HttpAccessor.class);

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

	/**
	 * CSRF check is only necessary for POST, PUT, DELETE requests with ongoing session, because only these can modify data before user can see it
	 */
	public HttpAccessor detectCsrf(String[] validOriginPrefixes) throws ServletException {
		if (request.getSession(false) == null || validOriginPrefixes == null)
			return this;

		String origin = request.getHeader("Origin");
		String referer = request.getHeader("Referer");
		boolean accepted = false;
		for(String mandatoryOriginPrefix : validOriginPrefixes) {

			if (referer != null && origin != null)
				accepted = mandatoryOriginPrefix.equals(origin) && referer.startsWith(mandatoryOriginPrefix);
			else if (origin != null)
				accepted = mandatoryOriginPrefix.equals(origin);
			else if (referer != null)
				accepted = referer.startsWith(mandatoryOriginPrefix);

			if (accepted)
				return this;
		}
		log.warn("CSRF detected, details: origin="+origin+", referer="+referer+", remoteIp="+request.getRemoteAddr());
		throw new ServletException("CSRF attack detected");
	}

	abstract public String getParametersAsJson();
	abstract public String getParameter(String parameterName);
}
