package eu.miltema.slimweb.testcomponents;

import eu.miltema.slimweb.annot.*;
import eu.miltema.slimweb.controller.HttpAccessor;

@Component(urlName = "session")
@SessionNotRequired
public class ComponentSession {

	public void post(HttpAccessor ht) {
		ht.setSessionObject("custom session object");
	}

	public void delete(HttpAccessor ht) {
		ht.terminateSession();
	}

	public void postLang(HttpAccessor ht) {
		ht.setLanguage(ht.getParameter("language"));
	}
}
