package eu.miltema.slimweb.comopnents;

import eu.miltema.slimweb.annot.Component;

@Component
public class ComponentSimple {

	public String fString;
	public int fInt = 123;

	public ComponentSimple get() {
		fString = (fString == null ? null : fString + fInt);
		fInt++;
		return this;
	}
}
