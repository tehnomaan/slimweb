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
	public void testMethodNotRequiringSession() throws Exception {
		assertTrue(get("/c2/no-session").contains("nono"));
	}

	@Test
	public void testRequiresSession() throws Exception {
		try {
			get("/c2/with-session");
		}
		catch(IOException ioe) {
			assertEquals("Http status code 303", ioe.getMessage());
			assertEquals("../../view/login", headers.firstValue("Location").get());
		}
	}

	@Test
	public void testRequiresSessionJsonResult() throws Exception {
		get("/c2/with-session", "Content-Type: application/json");
		assertEquals(250, statusCode);
		assertEquals("../../view/login", headers.firstValue("Location").get());
	}
}
