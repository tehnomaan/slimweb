package eu.miltema.slimweb.rcscanner;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.*;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

abstract public class ResourceScanner<T> {

	private String[] includedFolderNames;
	private Consumer<String> logger = s -> {};
	private String searchSubject;
	private Builder<T> streamBuilder = Stream.builder();
	private Predicate<String> filenameFilter;

	public ResourceScanner(String searchSubject, Predicate<String> filenameFilter) {
		this.searchSubject = searchSubject;
		this.filenameFilter = filenameFilter;
	}

	/**
	 * Find classes in specific packages
	 * @param includedFolders folders to scan; if null or empty, then search in all folders
	 * @return collection of classes
	 * @throws Exception if something goes wrong
	 */
	public Stream<T> scan(String ... includedFolders) throws Exception {
		this.includedFolderNames = includedFolders;
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
		jarFile.stream().filter(j -> !j.isDirectory()).filter(j -> filenameFilter.test(j.getName())).forEach(jarEntry -> {
			String name = jarEntry.getName().replaceAll("/", ".");
			boolean included = includedFolderNames.length == 0;//if package name list was not specified, then scan all packages
			for(String pkg : includedFolderNames)
				if (name.startsWith(pkg + ".")) {
					included = true;
					break;
				}
			if (included)
				streamBuilder.add(entryFound(jarEntry.getName(), () -> jarFile.getInputStream(jarEntry).readAllBytes()));
		});
	}

	private void scanDir(File dir, int rootPathLength) throws IOException, ClassNotFoundException {
		for(File file : dir.listFiles((d, name) -> !name.startsWith(".") && (d.isDirectory() || filenameFilter.test(name)))) {
			String path = file.getPath().replaceAll("\\" + File.separatorChar + "", ".");
			if (!file.isDirectory()) {
				boolean included = includedFolderNames.length == 0;//if package name list was not specified, then scan all packages
				for(String pkg : includedFolderNames)
					if (path.indexOf(pkg) >= 0) {
						included = true;
						break;
					}
				if (included)
					streamBuilder.add(entryFound(file.getPath().substring(rootPathLength), () -> new FileInputStream(file).readAllBytes()));
			}
			else scanDir(file, rootPathLength);
		}
	}

	public ResourceScanner<T> setLogger(Consumer<String> logger) {
		this.logger = logger;
		return this;
	}

	abstract T entryFound(String relativePath, FileContentSupplier fileContentSupplier);
}
