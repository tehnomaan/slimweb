package eu.miltema.slimweb.testcomponents;

import java.util.Map;

import eu.miltema.slimweb.ApplicationInitializer;
import eu.miltema.slimweb.ArgumentInjector;
import eu.miltema.slimweb.controller.HttpAccessor;

public class SlimwebInitializer implements ApplicationInitializer {

	@Override
	public String[] getComponentPackages() {
		return new String[] {"eu.miltema"};
	}

	@Override
	public void registerInjectors(Map<Class<?>, ArgumentInjector> mapInjectors) {
		mapInjectors.put(DemoSession.class, HttpAccessor::getSessionObject);
	}

}
