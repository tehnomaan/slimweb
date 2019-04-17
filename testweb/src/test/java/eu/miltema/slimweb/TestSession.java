package eu.miltema.slimweb;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class TestSession extends BaseTest {

	@Test
	public void testComponentNotRequiringSession() throws Exception {
		assertTrue(get("/component-simple").contains("124"));
	}

	@Test
	public void testRequiresSessionButSessionMissing() throws Exception {
		try {
			get("/c2/with-session");
		}
		catch(IOException ioe) {
			assertEquals("Http status code 303", ioe.getMessage());
			assertEquals("../../view/login", headers.firstValue("Location").get());
		}
	}

	@Test
	public void testRequiresSession() throws Exception {
		post("/session", "");
		assertEquals("\"custom session object\"", get("/c2/with-session"));
	}

	@Test
	public void testRequiresSessionJsonResult() throws Exception {
		get("/c2/with-session", "Accept: application/json");
		assertEquals(250, statusCode);
		assertEquals("../../view/login", headers.firstValue("Location").get());
	}
}
