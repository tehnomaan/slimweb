package eu.miltema.slimweb.rcscanner;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassScanner extends ClasspathScanner<Class<?>> {

	private static final Logger log = LoggerFactory.getLogger(ClassScanner.class);

	public ClassScanner(String searchSubject) {
		super(searchSubject, name -> name.endsWith(".class"));
	}

	private static String toClassName(String relativePath) {
		String path = relativePath.replaceAll("\\" + File.separatorChar + "", ".").replaceAll("/", ".");
		return path.substring(0, path.length() - 6);//drop .class suffix
	}

	@Override
	protected Class<?> entryFound(String relativePath, FileContentSupplier fileContentSupplier) {
		try {
			return Class.forName(toClassName(relativePath));
		}
		catch(Throwable t) {
			log.warn("Unable to load class " + relativePath);
			return null;
		}
	}
}
