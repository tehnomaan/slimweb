package eu.miltema.slimweb.push;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.*;

public class WebsocketConfigurator extends ServerEndpointConfig.Configurator {

	@Override
	public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
		HttpSession httpSession = (HttpSession) request.getHttpSession();
		if (httpSession != null)
			config.getUserProperties().put(PushConst.PROPERTY_HTTP_SESSION, httpSession);

		Map<String, List<String>> pm = request.getParameterMap();
		if (pm != null && !pm.isEmpty())
			config.getUserProperties().put(PushConst.PROPERTY_PARAMETERS, pm);
	}

}
