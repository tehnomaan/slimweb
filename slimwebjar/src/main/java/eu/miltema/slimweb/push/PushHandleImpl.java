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
	private Object customData;
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
	public HttpSession getHttpSession() {
		return httpSession;
	}

	@Override
	public void setCustomData(Object customData) {
		this.customData = customData;
	}

	@Override
	public Object getCustomData() {
		return customData;
	}

	@Override
	public String getLanguage() {
		try {
			String language = (httpSession == null ? null : (String) httpSession.getAttribute("__SESSION_OBJECT"));
			return (language == null ? "en" : language);
		}
		catch(IllegalStateException ise) {
			return null;
		}
	}

	@Override
	public Object getSessionObject() {
		try {
			return (httpSession == null ? null : httpSession.getAttribute("__SESSION_OBJECT"));
		}
		catch(IllegalStateException ise) {
			return null;
		}
	}
}
