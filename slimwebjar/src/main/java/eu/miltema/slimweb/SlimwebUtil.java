package eu.miltema.slimweb;

public class SlimwebUtil {
	public static String hyphenate(String s) {
		return s.replaceAll("([a-z]|[0-9])([A-Z]+)", "$1-$2").toLowerCase();
	}
}
