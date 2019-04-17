package eu.miltema.slimweb.testcomponents;

import eu.miltema.slimweb.annot.*;
import eu.miltema.slimweb.common.HttpAccessor;

@Component(urlName = "c2")
public class Component2 {

	public String get() {
		return "ZikZak";
	}

	public Object getWithSession(HttpAccessor ht) {
		return ht.getSessionObject();
	}

	public void postSomething() {
	}
}
