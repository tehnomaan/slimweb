package eu.miltema.slimweb.testcomponents;

import javax.servlet.http.HttpServletRequest;

import eu.miltema.slimweb.annot.Component;
import eu.miltema.slimweb.annot.SessionNotRequired;
import eu.miltema.slimweb.controller.HttpAccessor;
import eu.miltema.slimweb.controller.Redirect;

@Component
@SessionNotRequired
public class ComponentSimple {

	public String fString;
	public int fInt = 123;

	public ComponentSimple get() {
		fString = (fString == null ? null : fString + fInt);
		fInt++;
		return this;
	}

	public ComponentSimple getDoubleInt() {
		fInt *= 2;
		return this;
	}

	public String[] getRequest(HttpServletRequest request) {
		return new String[] {"abc", "xyz"};
	}

	public ComponentSimple getSession(HttpAccessor htAccessor, DemoSession session) {
		if (session == null)
			htAccessor.setSessionObject(new DemoSession());
		return this;
	}

	public int delete() {
		return ++fInt;
	}

	public int postInteger() {
		return fInt + 1;
	}

	public int putInteger() {
		return fInt + 200;
	}

	public void getRedirect() {
		throw new Redirect(Component2.class);
	}
}
