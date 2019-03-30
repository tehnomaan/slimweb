package eu.miltema.slimweb;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import eu.miltema.slimweb.annot.Component;

public class ComponentsReader {
	static Collection<Class<?>> classesCache;
	private Consumer<String> logger = s -> {};

	private void init() throws Exception {
		if (classesCache == null)
			classesCache = new ClassFileScanner().setLogger(logger).findClasses("eu.miltema");
	}

	public Collection<Class<?>> getComponents() throws Exception {
		init();
		return classesCache.stream().filter(c -> c.isAnnotationPresent(Component.class)).collect(Collectors.toList());
	}

	public ComponentsReader setLogger(Consumer<String> logger) {
		this.logger = logger;
		return this;
	}
}
