package eu.miltema.slimweb;

import java.util.Map;

public interface ApplicationInitializer {

	/**
	 * @return names of root packages, where slimweb components are found. For example return new String[] {"com.mypackage", "com.yourpackage.components"}
	 */
	String[] getComponentPackages();

	/**
	 * Register injectors for method parameters
	 * @param mapInjectors map of injectors
	 */
	void registerInjectors(Map<Class<?>, ArgumentInjector> mapInjectors);

	/**
	 * @return path to view, which requires user login
	 */
	String getLoginView();

	/**
	 * Provides a list of valid origins for CSRF attack detection.
	 * When null is returned, all origins are valid and CSRF attack is possible 
	 * @return a list of valid origins, for example ["http://myhost.com", "https://myhost.com"]; these origins are used in CSRF attack detection algorithm
	 */
	String[] getValidOrigins();
}
