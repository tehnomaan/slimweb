package eu.miltema.slimweb;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class TestBasicComponents extends BaseTest {

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

	@Test
	public void testRedirect() throws Exception {
		try {
			get("/component-simple/redirect");
			assertFalse("IOException with redirect should have occured", true);
		}
		catch(IOException ioe) {
			assertEquals("Http status code 303", ioe.getMessage());
			assertEquals("/view/c2", headers.firstValue("Location").get());
		}
	}

	@Test
	public void testSerializeNulls() throws Exception {
		String result = get("/component-types");
		assertTrue(result.contains("\"name\":null"));
	}

	@Test
	public void testSerializeDates() throws Exception {
		String result = get("/component-types?date=2019-01-31&time=09:02:00&datetime=2019-03-31T23:58:24");
		assertTrue(result.contains("\"date\":\"2019-02-01\""));
		assertTrue(result.contains("\"time\":\"09:07:00\""));
		assertTrue(result.contains("\"datetime\":\"2019-04-01T00:03:24\""));
	}
}
