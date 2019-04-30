package eu.miltema.slimweb.push;

import javax.servlet.http.HttpSession;

public interface PushHandle {

	/**
	 * Push an object to browser
	 * @param object object to push
	 */
	public void pushObject(Object object);

	/**
	 * Terminate push connection
	 */
	public void terminatePush();

	/**
	 * @return true, if push connection is open
	 */
	public boolean isPushOpen();

	/**
	 * @return http session associated with this push connection
	 */
	public HttpSession getHttpSession();

	/**
	 * Set custom data, which would be accessible in both, pushStarted() and pushTerminated()
	 * @param customData custom data
	 */
	public void setCustomData(Object customData);

	/**
	 * @return custom data
	 */
	public Object getCustomData();

	/**
	 * @return current language
	 */
	public String getLanguage();

	/**
	 * @return session object, which was registered in SlimwebConfiguration
	 */
	public Object getSessionObject();
}
