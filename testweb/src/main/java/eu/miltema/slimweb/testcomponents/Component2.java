package eu.miltema.slimweb.testcomponents;

import eu.miltema.slimweb.annot.*;
import eu.miltema.slimweb.controller.HttpAccessor;

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

	public Object getWithSession(HttpAccessor ht) {
		return ht.getSessionObject();
	}

	public void postSomething() {
	}
}
