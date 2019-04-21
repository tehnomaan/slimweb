package eu.miltema.slimweb;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class TestValidation extends BaseTest {

	@Test(expected = IOException.class)
	public void testMissingField() throws Exception {
		try {
			post("/component-validation/validate", "{}");
		}
		catch(IOException ioe) {
			assertEquals("Http status code 400", ioe.getMessage());
			assertTrue(response.contains("\"email\":"));
			throw ioe;
		}
	}

	@Test(expected = IOException.class)
	public void testInvalidEmail() throws Exception {
		try {
			post("/component-validation/validate", "{\"email\":\"uz@domain.com.\"}");
		}
		catch(IOException ioe) {
			assertEquals("Http status code 400", ioe.getMessage());
			assertTrue(response.contains("\"email\":"));
			throw ioe;
		}
	}

	@Test(expected = IOException.class)
	public void testMultipleInvalidFiels() throws Exception {
		try {
			post("/component-validation/validate", "{\"email\":\"uz@domain.com.\",\"numeric\":1}");
		}
		catch(IOException ioe) {
			assertEquals("Http status code 400", ioe.getMessage());
			assertTrue(response.contains("\"email\":"));
			assertTrue(response.contains("\"numeric\":"));
			throw ioe;
		}
	}

	@Test
	public void testValidEmailAndValidMissingNumeric() throws Exception {
		assertTrue(post("/component-validation/validate", "{\"email\":\"uz@domain.com\"}").contains("\"numeric\":6"));
	}

	@Test
	public void testNoValidation() throws Exception {
		assertTrue(post("/component-validation/no-validation", "{\"email\":\"uz@domain.com.\"}").contains("\"numeric\":12"));
	}

	@Test(expected = IOException.class)
	public void testNumericTooSmall() throws Exception {
		post("/component-validation/validate", "{\"email\":\"uz@domain.com\",\"numeric\":1}");
	}

	@Test(expected = IOException.class)
	public void testNumericNegative() throws Exception {
		post("/component-validation/validate", "{\"email\":\"uz@domain.com\",\"numeric\":-3}");
	}

	@Test(expected = IOException.class)
	public void testValidNumericTooBig() throws Exception {
		post("/component-validation/validate", "{\"email\":\"uz@domain.com\",\"numeric\":12}");
	}

	@Test
	public void testValidNumeric() throws Exception {
		assertTrue(post("/component-validation/validate", "{\"email\":\"uz@domain.com\",\"numeric\":2}").contains("\"numeric\":6"));
	}

	@Test(expected = IOException.class)
	public void testMinLen() throws Exception {
		post("/component-validation/validate", "{\"email\":\"u@d.c\"}");
	}

	@Test(expected = IOException.class)
	public void testMaxLen() throws Exception {
		post("/component-validation/validate", "{\"email\":\"aaaaaaaaaaaaaaaaaaa@domain.com\"}");
	}

	@Test(expected = IOException.class)
	public void testCustomError() throws Exception {
		post("/component-with-custom-validator", "{\"str\":\"aaaaa\"}");
	}

	@Test
	public void testCustomNoError1() throws Exception {
		assertTrue(post("/component-with-custom-validator", "{\"str\":\"abcxyz\"}").contains("\"str\":\"abc\""));
	}

	@Test
	public void testCustomNoError2() throws Exception {
		assertTrue(post("/component-with-custom-validator", "{}").contains("\"str\":\"abc\""));
	}
	
	@Test
	public void testCustomNoValidation() throws Exception {
		assertTrue(post("/component-with-custom-validator/2", "{\"str\":\"aaaaa\"}").contains("\"str\":\"def\""));
	}
}
