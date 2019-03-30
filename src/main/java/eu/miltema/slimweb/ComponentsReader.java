package eu.miltema.slimweb;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import eu.miltema.slimweb.annot.Component;

public class ComponentsReader {
	private Consumer<String> logger = s -> {};

	public Collection<Class<?>> getComponents() throws Exception {
		Collection<Class<?>> classes = new ArrayList<>();
		new ClassScanner("@Component classes").setLogger(logger).scan("eu.miltema");
		return classes.stream().filter(c -> c.isAnnotationPresent(Component.class)).collect(Collectors.toList());
	}

	public ComponentsReader setLogger(Consumer<String> logger) {
		this.logger = logger;
		return this;
	}
}
