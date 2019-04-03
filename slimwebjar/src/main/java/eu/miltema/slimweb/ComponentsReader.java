package eu.miltema.slimweb;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import eu.miltema.slimweb.annot.Component;
import eu.miltema.slimweb.rcscanner.*;

public class ComponentsReader {
	private static Collection<Class<?>> cachedComponents;
	private Consumer<String> logger = s -> {};
	private ApplicationInitializer initializer;

	private class InitializerFoundException extends RuntimeException {
	}

	public Stream<Class<?>> getComponentsAsStream() throws Exception {
		if (cachedComponents != null)
			return cachedComponents.stream();
		try {
			new ClassScanner("SlimwebInitializer") {
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
			}.setLogger(logger).scan();
			throw new Exception("Could not find class SlimwebInitializer; unable to initialize Slimweb");
		}
		catch(InitializerFoundException ife) {}//initializer was found, let's continue
		cachedComponents = new ArrayList<>();
		return new ClassScanner("@Component classes").
				setLogger(logger).
				scan(initializer.getComponentPackages()).
				filter(c -> c.isAnnotationPresent(Component.class)).
				peek(c -> cachedComponents.add(c));
	}

	public ComponentsReader setLogger(Consumer<String> logger) {
		this.logger = logger;
		return this;
	}

	public ApplicationInitializer getInitializer() {
		return initializer;
	}
}
