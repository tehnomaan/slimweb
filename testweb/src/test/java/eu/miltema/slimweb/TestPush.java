package eu.miltema.slimweb;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletionStage;

import org.junit.*;

import com.google.gson.Gson;

public class TestPush extends BaseTest {

	@Before
	public void setup() throws Exception {
		get("/component-push");//Make sure server has initialized components, before invoking push
	}

	@Test
	public void testMessage() throws Exception {
		StringBuilder response = new StringBuilder();
		Listener listener = new Listener() {
			@Override
			public void onOpen(WebSocket webSocket) {
				Listener.super.onOpen(webSocket);
			}
			@Override
			public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
				response.append(data);
				synchronized (baseUrl) {
					baseUrl.notify();
				}
				return Listener.super.onText(webSocket, data, last);
			}
			@Override
			public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
				return Listener.super.onClose(webSocket, statusCode, reason);
			}
		};
		baseUrl = baseUrl.replaceAll("controller", "push").replace("http", "ws");
		WebSocket ws = httpClient.newWebSocketBuilder().buildAsync(URI.create(baseUrl + "/component-push"), listener).join();
		ws.sendText("zzz", true);
		synchronized (baseUrl) {
			baseUrl.wait(2000);
		}
		assertArrayEquals(new int[] {3, 5}, new Gson().fromJson(response.toString(), int[].class));
	}

	@Test
	public void testInvalidComponent() {
	}
}
