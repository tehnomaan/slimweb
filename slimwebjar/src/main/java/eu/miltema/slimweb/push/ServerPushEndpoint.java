package eu.miltema.slimweb.push;

import java.util.*;
import static java.util.stream.Collectors.*;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.*;
import org.slf4j.*;
import eu.miltema.slimweb.*;
import eu.miltema.slimweb.annot.Component;
import eu.miltema.slimweb.common.SlimwebUtil;

//Vt kuidas saab kätte http sessiooni
//1) http://stackoverflow.com/questions/21888425/accessing-servletcontext-and-httpsession-in-onmessage-of-a-jsr-356-serverendpo
//2) http://stackoverflow.com/questions/17936440/accessing-httpsession-from-httpservletrequest-in-a-web-socket-serverendpoint
@ServerEndpoint(value = "/push/{__component}", configurator = WebsocketConfigurator.class)
public class ServerPushEndpoint {

	private static final Logger log = LoggerFactory.getLogger(ServerPushEndpoint.class);

	private Map<String, Class<?>> mapComponents;

	@OnOpen
	@SuppressWarnings("unchecked")
	public void onOpen(final Session session, @PathParam("__component") String componentName, EndpointConfig config) {
		try {
			if (mapComponents == null)
				mapComponents = new ComponentsReader(s -> log.info(s)).getComponentsAsStream().collect(toMap(c -> SlimwebUtil.urlName(c), c -> c));
			Map<String, Object> uprops = session.getUserProperties();
			HttpSession httpSession = (HttpSession) uprops.get(PushConst.PROPERTY_HTTP_SESSION);
			Map<String, List<String>> originalParams = (Map<String, List<String>>) uprops.get(PushConst.PROPERTY_PARAMETERS);
			Map<String, String> params = originalParams.entrySet().stream().filter(e -> !e.getKey().startsWith("__")).collect(toMap(e -> e.getKey(), e -> e.getValue().stream().collect(joining(","))));
			PushHandleImpl ph = new PushHandleImpl(httpSession, session);
			ph.componentName = componentName;
			ph.componentClass = (Class<? extends ServerPush>) Optional.ofNullable(mapComponents.get(componentName)).orElseThrow(() -> new Exception("Cannot map " + componentName + " to any @Component"));
			if (httpSession == null && ph.componentClass.getAnnotation(Component.class).requireSession()) {
				session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Missing session"));
				return;
			}
			uprops.put(PushConst.PROPERTY_HANDLE, ph);
			ServerPush component = ph.componentClass.getConstructor().newInstance();
			log.info("Request /push/" + componentName);
			component.pushStarted(ph, params);
		} catch (Exception e) {
			log.error("", e);
		}
	}

	@OnMessage
	public void onPong(Session session, PongMessage pongMsg) {
	}

	@OnMessage
	public String onMessage(String json, boolean isFinal, Session session) {
		if (!isFinal)
			throw new RuntimeException("Partial messages not supported");
		return null;
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		try {
			PushHandleImpl ph = (PushHandleImpl) session.getUserProperties().get(PushConst.PROPERTY_HANDLE);
			if (ph == null)
				return;
			log.info("Terminating /push/" + ph.componentName);
			if (ph.componentName != null) {
				Class<?> componentClass = mapComponents.get(ph.componentName);
				ServerPush component = (ServerPush) componentClass.getConstructor().newInstance();
				component.pushTerminated(ph);
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	@OnError
	public void onError(Session session, Throwable thr) {
	}

}
