package eu.miltema.slimweb.rcscanner;

import java.util.function.Predicate;
import org.slf4j.*;

public class FileScanner extends ClasspathScanner<FileTuple> {

	private static final Logger log = LoggerFactory.getLogger(FileScanner.class);

	public FileScanner(String searchSubject, Predicate<String> filenameFilter) {
		super(searchSubject, filenameFilter);
	}

	@Override
	protected FileTuple entryFound(String relativePath, FileContentSupplier fileContentSupplier) {
		try {
			FileTuple tuple = new FileTuple();
			tuple.path = relativePath;
			byte[] bytes = fileContentSupplier.readFile();
			if (bytes.length >= 3 && bytes[0] == -17 && bytes[1] == -69 && bytes[2] == -65)//UTF8 BOM
				tuple.content = new String(bytes, 3, bytes.length - 3);
			else tuple.content = new String(bytes);
			return tuple;
		}
		catch(Throwable t) {
			log.warn("Unable to load template file " + relativePath);
			return null;
		}
	}

}
