package eu.miltema.slimweb;

import static org.junit.Assert.assertTrue;

import org.junit.*;

public class TestLanguage extends BaseTest {

	@Before
	public void setup() throws Exception {
		baseUrl = baseUrl.replace("view", "controller");
		delete("/session");
		baseUrl = baseUrl.replace("controller", "view");
	}

	@Test
	public void testSpecificLanguage() throws Exception {
		assertTrue(get("/tpt1", "Accept-Language: et").contains("nimi"));
	}

	@Test
	public void testNoLanguage() throws Exception {
		assertTrue(get("/tpt1").contains("name"));
	}

	@Test
	public void testUnknownLanguage() throws Exception {
		assertTrue(get("/tpt1", "Accept-Language: de").contains("name"));
	}

	@Test
	public void testSessionLanguage() throws Exception {
		baseUrl = baseUrl.replace("view", "controller");
		post("/session", "");
		post("/session/lang", "{language:\"et\"}");
		baseUrl = baseUrl.replace("controller", "view");
		assertTrue(get("/tpt1").contains("nimi"));
	}
}
