package eu.miltema.slimweb;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

abstract class BaseTest {

	protected static HttpClient httpClient;

	protected String response;
	protected HttpHeaders headers;
	protected int statusCode;
	protected String baseUrl = "http://localhost:8080/testweb/controller/";
	protected String referer = "https://www.miltema.eu";

	public BaseTest() {
		CookieHandler.setDefault(new CookieManager());
		httpClient = HttpClient.newBuilder().cookieHandler(CookieHandler.getDefault()).build();
	}

	protected String sendRequest(String componentPath, Function<Builder, Builder> methodBuilder, String ... headerKeyValues) throws IOException, InterruptedException {
		String uri = baseUrl + (componentPath.startsWith("/") ? componentPath.substring(1) : componentPath);
		Builder builder = HttpRequest.newBuilder(URI.create(uri));
		if (headerKeyValues != null)
			for(int i = 0; i < headerKeyValues.length; i++)
				builder.header(headerKeyValues[i].split(":")[0], headerKeyValues[i].split(":")[1]);

		HttpRequest request = methodBuilder.apply(builder).build();
		HttpResponse<String> httpResponse = httpClient.send(request, BodyHandlers.ofString());
		headers = httpResponse.headers();
		if ((statusCode = httpResponse.statusCode()) >= 300)
			throw new IOException("Http status code " + httpResponse.statusCode());
		return (response = httpResponse.body());
	}

	protected String get(String componentPath, String ... headerKeyValues) throws InterruptedException, IOException {
		return sendRequest(componentPath, b -> b.GET(), headerKeyValues);
	}

	protected String delete(String componentPath) throws InterruptedException, IOException {
		return sendRequest(componentPath, b -> b.DELETE());
	}

	protected String post(String componentPath, String json) throws InterruptedException, IOException {
		return sendRequest(componentPath, b -> b.POST(BodyPublishers.ofString(json)));
	}

	protected String put(String componentPath, String json) throws InterruptedException, IOException {
		return sendRequest(componentPath, b -> b.PUT(BodyPublishers.ofString(json)));
	}

	protected String getWebsocketClientResponse() throws InterruptedException {
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
		return response.toString();
	}
}
