package eu.miltema.slimweb;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import eu.miltema.cpscan.*;
import eu.miltema.slimweb.annot.Component;

public class ComponentsReader {
	private static Collection<Class<?>> cachedComponents;
	private Consumer<String> logger;
	private ApplicationInitializer initializer;

	private class InitializerFoundException extends RuntimeException {
	}

	public ComponentsReader(Consumer<String> logger) {
		this.logger = logger;
	}

	public Stream<Class<?>> getComponentsAsStream() throws Exception {
		if (cachedComponents != null)
			return cachedComponents.stream();
		try {
			logger.accept("Looking for SlimwebInitializer");
			new ClassScanner(logger) {
				@Override
				protected Class<?> entryFound(String relativePath, FileContentSupplier fileContentSupplier) {
					if (!relativePath.endsWith("SlimwebInitializer.class"))
						return null;
					Class<?> initializerClass = super.entryFound(relativePath, fileContentSupplier);
					try {
						initializer = (ApplicationInitializer) initializerClass.getDeclaredConstructor().newInstance();
						throw new InitializerFoundException();
					} catch (InstantiationException | IllegalAccessException | ClassCastException | NoSuchMethodException | InvocationTargetException e) {
						throw new RuntimeException("Unable to instantiate initializer class " + initializerClass.getName() + ", which must implement interface ApplicationInitializer", e);
					}
				}
			}.scan();
			throw new Exception("Could not find class SlimwebInitializer; unable to initialize Slimweb");
		}
		catch(InitializerFoundException ife) {}//initializer was found, let's continue
		cachedComponents = new ArrayList<>();
		logger.accept("Looking for @Component classes");
		return new ClassScanner(logger).
				scan(initializer.getComponentPackages()).
				filter(c -> c.isAnnotationPresent(Component.class)).
				peek(c -> cachedComponents.add(c));
	}

	public ApplicationInitializer getInitializer() {
		return initializer;
	}
}
