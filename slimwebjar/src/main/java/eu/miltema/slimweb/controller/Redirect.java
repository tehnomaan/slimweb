package eu.miltema.slimweb.controller;

/**
 * Sends response redirect (HTTP 303)
 * @author Margus
 */
public class Redirect extends RuntimeException {

	Class<?> targetComponent;

	public Redirect(Class<?> targetComponent) {
		this.targetComponent = targetComponent;
	}
}
