package eu.miltema.slimweb;

import java.util.function.Consumer;
import java.util.stream.Stream;

import eu.miltema.slimweb.annot.Component;

public class ComponentsReader {
	private Consumer<String> logger = s -> {};
	private ApplicationInitializer initializer;

	private class InitializerFoundException extends RuntimeException {
	}

	public Stream<Class<?>> getComponentsAsStream() throws Exception {
		try {
			new ClassScanner("SlimwebInitializer") {
				@Override
				protected Class<?> entryFound(String relativePath, FileContentSupplier fileContentSupplier) {
					if (!relativePath.endsWith("SlimwebInitializer.class"))
						return null;
					Class<?> initializerClass = super.entryFound(relativePath, fileContentSupplier);
					try {
						initializer = (ApplicationInitializer) initializerClass.newInstance();
						throw new InitializerFoundException();
					} catch (InstantiationException | IllegalAccessException | ClassCastException e) {
						throw new RuntimeException("Unable to instantiate initializer class " + initializerClass.getName() + ", which must implement interface ApplicationInitializer", e);
					}
				}
			}.setLogger(logger).scan();
			throw new Exception("Could not find class SlimwebInitializer; unable to initialize Slimweb");
		}
		catch(InitializerFoundException ife) {}//initializer was found, let's continue
		return new ClassScanner("@Component classes").setLogger(logger).scan(initializer.getComponentPackages()).filter(c -> c.isAnnotationPresent(Component.class));
	}

	public ComponentsReader setLogger(Consumer<String> logger) {
		this.logger = logger;
		return this;
	}
}
