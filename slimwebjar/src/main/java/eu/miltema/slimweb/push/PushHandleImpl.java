package eu.miltema.slimweb.push;

import java.io.IOException;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import org.slf4j.*;
import com.google.gson.Gson;

public class PushHandleImpl implements PushHandle {

	private static final Logger log = LoggerFactory.getLogger(PushHandleImpl.class);

	private Session websocketSession;
	private Gson gson = new Gson();
	private HttpSession httpSession;
	Class<? extends ServerPush> componentClass;
	String componentName;

	public PushHandleImpl(HttpSession httpSession, Session websocketSession) {
		this.httpSession = httpSession;
		this.websocketSession = websocketSession;
	}

	@Override
	public void terminatePush() {
		try {
			websocketSession.close();
		} catch (Exception e) {
			log.error("", e);
		}
	}

	public void pushObject(Object object) {
		try {
			websocketSession.getBasicRemote().sendText(gson.toJson(object));
		} catch (IOException e) {
			log.error("", e);
		}
	}

	@Override
	public boolean isPushOpen() {
		return websocketSession.isOpen();
	}

	@Override
	public HttpSession getSession() {
		return httpSession;
	}
}
