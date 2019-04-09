package eu.miltema.slimweb.view;

import java.util.*;
import java.util.function.Function;
import java.util.regex.*;

/**
 * Template resolver
 * @author Margus
 */
class TemplateResolver {

	private Map<String, Function<String, String>> replacers = new HashMap<>();

	/**
	 * Do replacement in template
	 * @param template input template
	 * @param replaceValues replace values
	 * @return template with replacements
	 */
	String replace(String template, Map<String, String> replaceValues, String defaultPrefix) {
		Pattern pattern = Pattern.compile("(\\{-)([^-]+)(-\\})");
		Matcher m = pattern.matcher(template);
		int pos = 0;
		StringBuilder sb = new StringBuilder();
		while(m.find()) {
			if (m.start() > pos)
				sb.append(template.substring(pos, m.start()));
			String id = m.group(2);
			if (id.length() > 0 && id.charAt(0) == '.')
				id = defaultPrefix + id;
			String replacement = replaceValues.get(id);
			if (replacement == null) {
				String[] idsplit = id.split(":");
				Function<String, String> replacer = replacers.get(idsplit[0] + ":");
				if (replacer != null)
					replacement = replacer.apply(idsplit.length > 1 ? idsplit[1] : null);
			}
			if (replacement == null)
				replacement = "!!!" + id + "!!!";
			sb.append(replacement);
			pos = m.end();
		}
		int len = template.length();
		if (pos < len)
			sb.append(template.substring(pos));
		return sb.toString();
	}

	/**
	 * Set custom replacer according to placeholder prefix
	 * @param prefix placeholder prefix, for example "file:"
	 * @param replacer function, which performs the replacing according to placeholder suffix
	 * @return
	 */
	TemplateResolver customReplacer(String prefix, Function<String, String> replacer) {
		replacers.put(prefix, replacer);
		return this;
	}
}
