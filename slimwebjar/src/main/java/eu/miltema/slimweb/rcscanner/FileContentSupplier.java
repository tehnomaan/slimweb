package eu.miltema.slimweb.rcscanner;

@FunctionalInterface
public interface FileContentSupplier {
	byte[] readFile() throws Exception;
}
