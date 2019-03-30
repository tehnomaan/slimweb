package eu.miltema.slimweb;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarFile;

class ClassFileScanner {

	private String[] includedPackageNames;
	private String[][] packageDirs;
	private Consumer<String> logger = s -> {};

	/**
	 * Find classes in specific packages
	 * @param includedPackageNames packages to scan; if null or empty, then search in all packages
	 * @return collection of classes
	 * @throws Exception if something goes wrong
	 */
	public Collection<Class<?>> findClasses(String ... includedPackageNames) throws Exception {
		this.includedPackageNames = includedPackageNames;
		if (includedPackageNames != null && includedPackageNames.length > 0)
			packageDirs = Arrays.stream(includedPackageNames).map(name -> name.split("\\.")).toArray(String[][]::new);
		Enumeration<URL> roots = Thread.currentThread().getContextClassLoader().getResources("");
		ArrayList<Class<?>> ret = new ArrayList<>();
		ArrayList<URL> list = Collections.list(roots);
		for(URL url : list) {
			String path = url.toString();
			logger.accept("Scanning for classes in " + path);
			if (!path.startsWith("jar:")) {
				File root = new File(url.getFile());
				scanDir(0, root.getPath().length(), false, ret, root);
			}
			else scanJar(((JarURLConnection) url.openConnection()).getJarFile(), ret);
		}
		return ret;
	}

	private void scanJar(JarFile jarFile, ArrayList<Class<?>> ret) {
		jarFile.stream().filter(j -> !j.isDirectory()).filter(j -> j.getName().endsWith(".class")).forEach(jarEntry -> {
			String name = jarEntry.getName().split("\\.class")[0].replaceAll("/", ".");
			boolean included = false;
			for(String pkg : includedPackageNames)
				if (name.startsWith(pkg + ".")) {
					included = true;
					break;
				}
			if (!included)
				return;
			try {
				ret.add(Class.forName(name));
			}
			catch(Exception x) {}
		});
	}

	private void scanDir(int depth, int trimLeft, boolean scanFiles, ArrayList<Class<?>> ret, File dir) throws IOException, ClassNotFoundException {
		for(File file : dir.listFiles((d, name) -> !name.startsWith(".") && (d.isDirectory() || name.endsWith(".class")))) {
			if (file.isDirectory()) {
				String dirname = file.getName();
				if (packageDirs != null) {
					for(String[] pkg : packageDirs)
						if (depth >= pkg.length || pkg[depth].equals(dirname)) {
							scanDir(depth + 1, trimLeft, depth + 1 >= pkg.length, ret, file);
							break;
						}
				}
				else scanDir(depth + 1, trimLeft, true, ret, file);
			}
			else if (scanFiles) {
				String path = file.getAbsolutePath();
				path = path.substring(trimLeft, path.length() - 6);
				path = path.replaceAll("\\" + File.separatorChar + "", ".");
				if (path.charAt(0) == '.')
					path = path.substring(1);
				try {
					ret.add(Class.forName(path));
				}
				catch(Exception x) {}
			}
		}
	}

	public ClassFileScanner setLogger(Consumer<String> logger) {
		this.logger = logger;
		return this;
	}
}
