package eu.miltema.slimweb.test;

import org.junit.Test;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

public class TestController {

	@Test
	public void testJee() {
		open("http://localhost:8080/slimweb/controller");
		$("#zz").shouldHave(text("Ahoi"));
	}
}
