package eu.miltema.slimweb.view;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.stream.*;

import org.junit.Test;

public class TestTemplateResolver {

	private Map<String, String> values = Stream.of("name:John", "job:driver", "home:Europe", "qualified.label:world").collect(Collectors.toMap(s -> s.split(":")[0], s -> s.split(":")[1]));

	@Test
	public void testReplaceStartMiddleEnd() {
		assertEquals("John is driver from Europe", new TemplateResolver().replace("{-name-} is {-job-} from {-home-}", values, null));
	}

	@Test
	public void testReplaceMiddle() {
		assertEquals("zzz driver yyy", new TemplateResolver().replace("zzz {-job-} yyy", values, null));
	}

	@Test
	public void testMissingReplacement() {
		assertEquals("zzz !!!xyz!!! yyy", new TemplateResolver().replace("zzz {-xyz-} yyy", values, null));
	}

	@Test
	public void testQualifiedKey() {
		assertEquals("zzz world !!!qualified.notfound!!! yyy", new TemplateResolver().replace("zzz {-.label-} {-.notfound-} yyy", values, "qualified"));
	}

	@Test
	public void testCustomReplacers() {
		TemplateResolver tr = new TemplateResolver().
				customReplacer("a:", suffix -> "#" + suffix + "#").
				customReplacer("b:", suffix -> "_" + suffix + "_");
		assertEquals("zzz driver, #mm#, _rr_, !!!c:tt!!! yyy", tr.replace("zzz {-job-}, {-a:mm-}, {-b:rr-}, {-c:tt-} yyy", values, null));
	}
}
