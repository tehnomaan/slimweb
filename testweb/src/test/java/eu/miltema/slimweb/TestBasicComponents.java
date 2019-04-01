package eu.miltema.slimweb;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.function.Function;

import org.junit.Test;

public class TestBasicComponents {

	private static HttpClient httpClient = HttpClient.newBuilder().build();

	protected String response;

	private String sendRequest(String componentPath, Function<Builder, Builder> methodBuilder) throws IOException, InterruptedException {
		String uri = "http://localhost:8080/testweb/controller/" + (componentPath.startsWith("/") ? componentPath.substring(1) : componentPath);
		HttpRequest request = methodBuilder.apply(HttpRequest.newBuilder().uri(URI.create(uri))).build();
		HttpResponse<String> httpResponse = httpClient.send(request, BodyHandlers.ofString());
		if (httpResponse.statusCode() >= 300)
			throw new IOException("Http status code " + httpResponse.statusCode());
		return (response = httpResponse.body());
	}

	protected String get(String componentPath) throws InterruptedException, IOException {
		return sendRequest(componentPath, b -> b.GET());
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

	@Test
	public void testDefaultGet() throws Exception {
		assertTrue(get("/component-simple").contains("124"));
	}

	@Test
	public void testParameters() throws Exception {
		get("/component-simple?fString=abc&fInt=777");
		assertTrue(response.contains("abc777"));
		assertTrue(response.contains("778"));
	}

	@Test
	public void testNamedGet() throws Exception {
		assertTrue(get("/component-simple/double-int?fInt=11").contains("22"));
	}

	@Test
	public void testGetWithCustomName() throws Exception {
		assertTrue(get("/c2").contains("ZikZak"));
	}

	@Test
	public void testDelete() throws Exception {
		assertTrue(delete("/component-simple").contains("124"));
	}

	@Test
	public void testPost() throws Exception {
		assertTrue(post("/component-simple/integer", "{fInt:100}").contains("101"));
	}

	@Test
	public void testPut() throws Exception {
		assertTrue(put("/component-simple/integer", "{fInt:100}").contains("300"));
	}
}
