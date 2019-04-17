package eu.miltema.slimweb.testcomponents;

import eu.miltema.slimweb.annot.Component;

@Component(requireSession = false, urlName = "c3")
public class Component3 {

	public String get() {
		return "zetuu";
	}
}
