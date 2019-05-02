package eu.miltema.slimweb.common;

import eu.miltema.slimweb.annot.Component;

public class SlimwebUtil {
	public static String hyphenate(String s) {
		return s.replaceAll("([a-z]|[0-9])([A-Z]+)", "$1-$2").toLowerCase();
	}

	public static String urlName(Class<?> componentClass) {
		Component ca = componentClass.getAnnotation(Component.class);
		if (ca != null && !ca.urlName().isEmpty())
			return ca.urlName();
		else return SlimwebUtil.hyphenate(componentClass.getSimpleName());
	}
}
