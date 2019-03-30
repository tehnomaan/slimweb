package eu.miltema.slimweb;

public class ComponentSimple {

	public String fString;
	public int fInt = 123;

	public ComponentSimple get() {
		fString = (fString == null ? null : fString + fInt);
		fInt++;
		return this;
	}
}
