package eu.miltema.slimweb;

import java.util.Map;

import eu.miltema.slimweb.common.HttpAccessor;

public interface ApplicationConfiguration {

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

	/**
	 * @param templateFile template file name, including extension, but excluding folder names
	 * @param htAccessor http request/response details
	 * @return frame template file name (without folders and .html extension)
	 */
	public String getFrameForTemplate(String templateFile, HttpAccessor htAccessor);

	/**
	 * This preprocessor is invoked for all components immediately prior get-, post-, put- or delete-method.
	 * @param component component
	 * @param htAccessor http request/response details
	 */
	public void preprocessComponent(Object component, HttpAccessor htAccessor);

	/**
	 * This postprocessor is invoked for all components immediately after get-, post-, put- or delete-method has finished.
	 * @param component component
	 * @param htAccessor http request/response details
	 */
	public void postprocessComponent(Object component, HttpAccessor htAccessor);
}
