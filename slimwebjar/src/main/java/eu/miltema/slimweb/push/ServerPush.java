package eu.miltema.slimweb.push;

import java.util.*;

/**
 * A component implements this interface to receive push notifications
 */
public interface ServerPush {

	/**
	 * Called when client has started a push/websocket connection
	 * @param pushHandle push handle
	 * @param parameters parameters from URL
	 * @throws Exception when anything goes wrong in component
	 */
	void pushStarted(PushHandle pushHandle, Map<String, String> parameters) throws Exception;

	/**
	 * Called when client has terminated push/websocket connection
	 * @param pushHandle push handle
	 * @throws Exception when anything goes wrong in component
	 */
	void pushTerminated(PushHandle pushHandle) throws Exception;
}
