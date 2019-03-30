package eu.miltema.slimweb;

import static org.junit.Assert.*;

import org.junit.Test;
import static com.codeborne.selenide.WebDriverRunner.*;
import static com.codeborne.selenide.Selenide.*;

public class TestBasicComponents {

	@Test
	public void testComponentAccess() throws Exception {
		open("http://localhost:8080/slimweb/controller/component-simple/get");
		assertTrue(source().contains("124"));
	}

	@Test
	public void testInOutParameters() throws Exception {
		open("http://localhost:8080/slimweb/controller/component-simple/get?fString=abc&fInt=777");
		assertTrue(source().contains("abc777"));
		assertTrue(source().contains("778"));
	}
}
