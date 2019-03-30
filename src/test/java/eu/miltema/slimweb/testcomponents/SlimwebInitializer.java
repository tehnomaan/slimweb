package eu.miltema.slimweb.testcomponents;

import eu.miltema.slimweb.ApplicationInitializer;

public class SlimwebInitializer implements ApplicationInitializer {

	@Override
	public String[] getComponentPackages() {
		return new String[] {"eu.miltema"};
	}

}
