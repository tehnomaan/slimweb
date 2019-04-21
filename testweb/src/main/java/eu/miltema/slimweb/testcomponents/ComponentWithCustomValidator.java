package eu.miltema.slimweb.testcomponents;

import eu.miltema.slimweb.annot.Component;
import eu.miltema.slimweb.annot.ValidateInput;

@Component(validator = CustomValidator.class, requireSession = false)
public class ComponentWithCustomValidator {

	public String str;

	@ValidateInput
	public ComponentWithCustomValidator post() {
		str = "abc";
		return this;
	}

	public ComponentWithCustomValidator post2() {
		str = "def";
		return this;
	}
}
