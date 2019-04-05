package eu.miltema.slimweb.testcomponents;

import java.util.Map;

import eu.miltema.slimweb.*;
import eu.miltema.slimweb.common.HttpAccessor;

public class SlimwebInitializer implements ApplicationInitializer {

	@Override
	public String[] getComponentPackages() {
		return new String[] {"eu.miltema"};
	}

	@Override
	public void registerInjectors(Map<Class<?>, ArgumentInjector> mapInjectors) {
		mapInjectors.put(DemoSession.class, HttpAccessor::getSessionObject);
	}

	@Override
	public String getLoginView() {
		return "login.html";
	}

	@Override
	public String[] getValidOrigins() {
		return null;
	}

}
