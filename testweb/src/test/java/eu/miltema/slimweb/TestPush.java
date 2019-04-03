package eu.miltema.slimweb;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.gson.Gson;

public class TestPush extends BaseTest {

	@Before
	public void setup() throws Exception {
		get("/component-push");//Make sure server has initialized components, before invoking push
	}

	@Test
	public void testMessage() throws Exception {
		assertArrayEquals(new int[] {3, 5}, new Gson().fromJson(getWebsocketClientResponse("/component-push"), int[].class));
	}

	@Test
	public void testSessionExists() throws Exception {
		post("/session", "");
		assertArrayEquals(new int[] {1, 11}, new Gson().fromJson(getWebsocketClientResponse("/component-push-requires-session"), int[].class));
	}

	@Test
	public void testMissingSession() throws Exception {
		assertEquals("", getWebsocketClientResponse("/component-push-requires-session"));
	}
}
