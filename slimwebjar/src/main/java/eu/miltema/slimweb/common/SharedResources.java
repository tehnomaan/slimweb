package eu.miltema.slimweb.common;

import static java.util.stream.Collectors.toMap;
import java.util.Map;
import org.slf4j.*;

public class SharedResources {

	private static SharedResources instance;
	private static final Logger log = LoggerFactory.getLogger(SharedResources.class);

	synchronized public static SharedResources instance() throws Exception {
		if (instance == null)
			instance = new SharedResources();
		return instance;
	}

	public Map<String, ComponentDef> mapComponents;//urlName->component
	public ApplicationConfiguration configuration;
	public AllLabels labels;

	private SharedResources() throws Exception {
		ComponentsReader cr = new ComponentsReader(s -> log.debug(s));
		configuration = cr.getInitializer();

		mapComponents = cr.getComponentsAsStream().
				map(c -> new ComponentDef(c)).
				collect(toMap(c -> c.url, c -> c));
		if (mapComponents.isEmpty())
			log.warn("No component definitions were found");
		else log.debug("Found " + mapComponents.size() + " components");

		labels = new AllLabels();
	}
}
