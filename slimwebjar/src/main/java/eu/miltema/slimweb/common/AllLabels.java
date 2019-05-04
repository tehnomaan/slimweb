package eu.miltema.slimweb.common;

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
public class AllLabels {

	private static final Logger log = LoggerFactory.getLogger(AllLabels.class);

	private static Map<String, LanguageLabels> map;

	public AllLabels() throws Exception {
		if (map == null) {
			map = load();
			log.debug("Found " + map.size() + " label file(s)");
		}
	}

	private Map<String, LanguageLabels> load() throws Exception {
		final String LBLFILE_PATTERN = "(.*[/|\\\\])?([^/|\\\\]+)(\\.)lbl$";//separates file name from directories and extension
		log.info("Looking for label files");
		return new FileScanner(s -> log.info(s), filename -> filename.endsWith(".lbl")).
			scan("labels").
			collect(toMap(t -> t.path.replaceAll(LBLFILE_PATTERN, "$2"), t -> getLabelsMap(t.path, t.content), (f1, f2) -> labelsConflict(f1, f2)));
	}

	private LanguageLabels labelsConflict(LanguageLabels labels1, LanguageLabels labels2) {
		log.warn("Multiple files for the same locale");
		return labels1;
	}

	private LanguageLabels getLabelsMap(String filename, String labelFile) {
		log.debug("Processing file " + filename);
		Map<String, String> map = labelFile.lines().
				filter(not(String::isBlank)).
				map(line -> splitLine(line)).
				filter(ls -> ls.length >= 2).
				collect(toMap(ls -> ls[0].trim(), ls -> ls[1].trim()));
		return new LanguageLabels(map);
	}

	private String[] splitLine(String line) {
		int idx = line.indexOf('=');
		return new String[] {line.substring(0, idx).trim(), line.substring(idx + 1).trim().split("\\t")[0].trim()};
	}

	public Stream<Entry<String, LanguageLabels>> streamLanguages() {
		return map.entrySet().stream();
	}

	public Map<String, String> getLabels(String language) {
		return Optional.ofNullable(map.get(language)).
				orElseGet(() -> Optional.ofNullable(map.get("en")).
				orElseGet(() -> map.values().iterator().next()));
	}
}
