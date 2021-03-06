package eu.miltema.slimweb.common;

import java.util.Map;

import eu.miltema.slimweb.ArgumentInjector;

abstract public class ApplicationConfigurationAdapter implements ApplicationConfiguration {

	@Override
	public void registerInjectors(Map<Class<?>, ArgumentInjector> mapInjectors) {
	}

	@Override
	public String getLoginView() {
		return "login.html";
	}

	@Override
	public String[] getValidOrigins() {
		return new String[] {"http://localhost:8080", "https://localhost:8443"};
	}

	@Override
	public String getFrameForTemplate(String templateFile, HttpAccessor htAccessor) {
		return htAccessor.getSessionObject() == null ? "loginframe" : "frame";
	}

	@Override
	public void preprocessComponent(Object component, HttpAccessor htAccessor) {
	}

	@Override
	public void postprocessComponent(Object component, HttpAccessor htAccessor) {
	}
}
