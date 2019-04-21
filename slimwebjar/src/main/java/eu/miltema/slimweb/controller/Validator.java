package eu.miltema.slimweb.controller;

import java.util.Map;

/**
 * Interface for input validation logic
 * @author Margus
 */
public interface Validator {

	/**
	 * Validate input and return validation results
	 * @param object object to validate
	 * @param labels language specific labels
	 * @return null when everything is fine; in case of error return a map with field-message pairs
	 * @throws Exception when something goes wrong
	 */
	Map<String, String> validate(Object object, Map<String, String> labels) throws Exception;
}
