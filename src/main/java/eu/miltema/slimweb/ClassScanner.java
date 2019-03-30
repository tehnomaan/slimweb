package eu.miltema.slimweb;

public class ClassScanner extends FileScanner<Class<?>> {
	public ClassScanner(String searchSubject) {
		super(searchSubject);
	}

	@Override
	protected Class<?> entryFound(String relativePath, FileContentSupplier fileContentSupplier) {
		try {
			return Class.forName(toClassName(relativePath));
		}
		catch(Exception x) {
			return null;
		}
	}
}
