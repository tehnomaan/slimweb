package eu.miltema.slimweb.testcomponents;

import eu.miltema.slimweb.annot.*;

@Component(urlName = "c2")
public class Component2 {

	@SessionNotRequired
	public String get() {
		return "ZikZak";
	}

	@SessionNotRequired
	public String getNoSession() {
		return "nono";
	}

	public String getWithSession() {
		return "mmm";
	}
}
