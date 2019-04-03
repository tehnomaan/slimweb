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
		assertArrayEquals(new int[] {3, 5}, new Gson().fromJson(getWebsocketClientResponse(), int[].class));
	}

	@Test
	public void testInvalidComponent() {
	}
}
