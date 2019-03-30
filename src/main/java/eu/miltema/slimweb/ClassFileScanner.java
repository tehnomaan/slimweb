package eu.miltema.slimweb;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Stream;

class ClassFileScanner {

	private String[][] packageDirs;
	private String[] excludedDirs;

	/**
	 * Find classes in specific packages
	 * @param includedPackageNames packages to scan; if null or empty, then search in all packages
	 * @return collection of classes
	 * @throws Exception if something goes wrong
	 */
	public Collection<Class<?>> findClasses(String ... includedPackageNames) throws Exception {
		if (includedPackageNames != null && includedPackageNames.length > 0)
			packageDirs = Arrays.stream(includedPackageNames).map(name -> name.split("\\.")).toArray(String[][]::new);
		else excludedDirs = Stream.of("java", "javax", "com.google", "org.apache").map(s -> s.replaceAll("\\.", "\\" + File.separator)).toArray(String[]::new);
		Enumeration<URL> roots = Thread.currentThread().getContextClassLoader().getResources("");
		ArrayList<Class<?>> ret = new ArrayList<>();
		ArrayList<URL> list = Collections.list(roots);
		for(URL url : list) {
			File root = new File(url.toURI());
			scanDir(0, root.getPath().length(), false, ret, root);
		}
		return ret;
	}

	private void scanDir(int depth, int trimLeft, boolean scanFiles, ArrayList<Class<?>> ret, File dir) throws IOException, ClassNotFoundException {
		if (excludedDirs != null)
			for(String dirSuffix : excludedDirs)
				if (dir.getName().endsWith(dirSuffix))
					return;
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
				catch(Exception x) {
					path = null;
				}
			}
		}
	}
}
