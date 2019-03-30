package eu.miltema.slimweb;

import static org.junit.Assert.*;

import org.junit.Test;
import static com.codeborne.selenide.WebDriverRunner.*;
import static com.codeborne.selenide.Selenide.*;

public class TestBasicComponents {

	@Test
	public void testDefaultGet() throws Exception {
		open("http://localhost:8080/slimweb/controller/component-simple");
		assertTrue(source().contains("124"));
	}

	@Test
	public void testParameters() throws Exception {
		open("http://localhost:8080/slimweb/controller/component-simple?fString=abc&fInt=777");
		assertTrue(source().contains("abc777"));
		assertTrue(source().contains("778"));
	}

	@Test
	public void testNamedGet() throws Exception {
		open("http://localhost:8080/slimweb/controller/component-simple/double-int?fInt=11");
		assertTrue(source().contains("22"));
	}
}
