package eu.miltema.slimweb.view;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.miltema.cpscan.FileScanner;

/**
 * A loader+container for language-labelMap pairs
 * @author Margus
 */
public class Labels {

	private static final Logger log = LoggerFactory.getLogger(Labels.class);

	private static Map<String, Map<String, String>> map;

	public Labels() throws Exception {
		if (map == null)
			map = load();
	}

	private Map<String, Map<String, String>> load() throws Exception {
		final String LBLFILE_PATTERN = "(.*[/|\\\\])?([^/|\\\\]+)(\\.)lbl$";//separates file name from directories and extension
		log.info("Looking for label files");
		return new FileScanner(s -> log.info(s), filename -> filename.endsWith(".lbl")).
			scan("labels").
			collect(toMap(t -> t.path.replaceAll(LBLFILE_PATTERN, "$2"), t -> getLabelsMap(t.path, t.content), (f1, f2) -> labelsConflict(f1, f2)));
	}

	private Map<String, String> labelsConflict(Map<String, String> labels1, Map<String, String> labels2) {
		log.warn("Multiple files for the same locale");
		return labels1;
	}

	private Map<String, String> getLabelsMap(String filename, String labelFile) {
		log.debug("Processing file " + filename);
		Map<String, String> ret = labelFile.lines().
				filter(not(String::isBlank)).
				map(line -> line.split("=|\\t")).
				filter(ls -> ls.length >= 2).
				collect(toMap(ls -> ls[0].trim(), ls -> ls[1].trim()));
		return ret;
	}

	public Stream<Entry<String, Map<String, String>>> streamLanguages() {
		return map.entrySet().stream();
	}

	public Map<String, String> getLabels(String language) {
		return Optional.ofNullable(map.get(language)).
				orElseGet(() -> Optional.ofNullable(map.get("en")).
				orElseGet(() -> map.values().iterator().next()));
	}
}
