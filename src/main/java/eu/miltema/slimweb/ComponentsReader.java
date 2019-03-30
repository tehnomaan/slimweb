package eu.miltema.slimweb;

import java.util.*;
import java.util.stream.Collectors;

import eu.miltema.slimweb.annot.Component;

public class ComponentsReader {
	static Collection<Class<?>> classesCache;

	private void init() throws Exception {
		if (classesCache == null)
			classesCache = new ClassFileScanner().findClasses();
	}

	public Collection<Class<?>> getComponents() throws Exception {
		init();
		return classesCache.stream().filter(c -> c.isAnnotationPresent(Component.class)).collect(Collectors.toList());
	}

}
