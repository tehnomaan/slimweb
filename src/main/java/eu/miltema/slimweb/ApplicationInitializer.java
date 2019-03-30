package eu.miltema.slimweb;

public interface ApplicationInitializer {

	/**
	 * @return names of root packages, where slimweb components are found. For example return new String[] {"com.mypackage", "com.yourpackage.components"}
	 */
	String[] getComponentPackages();
}
