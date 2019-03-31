package eu.miltema.slimweb;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

@FunctionalInterface
interface FileContentSupplier {
	byte[] readFile() throws Exception;
}

abstract class FileScanner<T> {

	private String[] includedPackageNames;
	private Consumer<String> logger = s -> {};
	private String searchSubject;
	private Builder<T> streamBuilder = Stream.builder();

	public FileScanner(String searchSubject) {
		this.searchSubject = searchSubject;
	}

	/**
	 * Find classes in specific packages
	 * @param includedPackageNames packages to scan; if null or empty, then search in all packages
	 * @return collection of classes
	 * @throws Exception if something goes wrong
	 */
	public Stream<T> scan(String ... includedPackageNames) throws Exception {
		this.includedPackageNames = includedPackageNames;
		ArrayList<URL> list = Collections.list(Thread.currentThread().getContextClassLoader().getResources(""));
		for(URL url : list) {
			String path = url.toString();
			logger.accept("Scanning for " + searchSubject + " in " + path);
			if (!path.startsWith("jar:")) {
				File root = new File(URLDecoder.decode(url.getFile(), "utf8"));
				path = root.toString();
				scanDir(root, path.length() + (path.endsWith("\\") || path.endsWith("/") ? 0 : 1));
			}
			else scanJar(((JarURLConnection) url.openConnection()).getJarFile());
		}
		return streamBuilder.build().filter(s -> s != null);
	}

	private void scanJar(JarFile jarFile) {
		jarFile.stream().filter(j -> !j.isDirectory()).filter(j -> j.getName().endsWith(".class")).forEach(jarEntry -> {
			String name = jarEntry.getName().split("\\.class")[0].replaceAll("/", ".");
			boolean included = includedPackageNames.length == 0;//if package name list was not specified, then scan all packages
			for(String pkg : includedPackageNames)
				if (name.startsWith(pkg + ".")) {
					included = true;
					break;
				}
			if (included)
				streamBuilder.add(entryFound(jarEntry.getName(), () -> loadFile(jarFile.getInputStream(jarEntry))));
		});
	}

	private void scanDir(File dir, int rootPathLength) throws IOException, ClassNotFoundException {
		for(File file : dir.listFiles((d, name) -> !name.startsWith(".") && (d.isDirectory() || name.endsWith(".class")))) {
			String path = file.getPath().replaceAll("\\" + File.separatorChar + "", ".");
			if (!file.isDirectory()) {
				boolean included = includedPackageNames.length == 0;//if package name list was not specified, then scan all packages
				for(String pkg : includedPackageNames)
					if (path.indexOf(pkg) >= 0) {
						included = true;
						break;
					}
				if (included)
					streamBuilder.add(entryFound(file.getPath().substring(rootPathLength), () -> loadFile(new FileInputStream(file))));
			}
			else scanDir(file, rootPathLength);
		}
	}

	private byte[] loadFile(InputStream fileInputStream) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(InputStream is = fileInputStream) {
			int nRead;
			byte[] data = new byte[16384];
			while ((nRead = is.read(data, 0, data.length)) != -1)
				baos.write(data, 0, nRead);
			return baos.toByteArray();
		}
	}

	public FileScanner<T> setLogger(Consumer<String> logger) {
		this.logger = logger;
		return this;
	}

	protected String toClassName(String relativePath) {
		String path = relativePath.replaceAll("\\" + File.separatorChar + "", ".").replaceAll("/", ".");
		return path.substring(0, path.length() - 6);//drop .class suffix
	}

	abstract protected T entryFound(String relativePath, FileContentSupplier fileContentSupplier);
}
