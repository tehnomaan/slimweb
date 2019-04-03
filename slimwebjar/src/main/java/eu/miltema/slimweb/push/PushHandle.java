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
	public HttpSession getSession();
}
