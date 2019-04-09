package eu.miltema.slimweb;

import java.util.Map;

import eu.miltema.slimweb.common.HttpAccessor;

abstract public class ApplicationInitializerAdapter implements ApplicationInitializer {

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
}
