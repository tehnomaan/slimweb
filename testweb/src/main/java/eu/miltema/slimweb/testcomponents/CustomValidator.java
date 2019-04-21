package eu.miltema.slimweb.testcomponents;

import java.util.Map;

import eu.miltema.slimweb.controller.ValidatorAdapter;

public class CustomValidator extends ValidatorAdapter {

	public CustomValidator() {
		super(ComponentWithCustomValidator.class);
	}

	@Override
	public Map<String, String> validate(Object object, Map<String, String> labels) throws Exception {
		Map<String, String> errors = super.validate(object, labels);
		ComponentWithCustomValidator component = (ComponentWithCustomValidator) object;
		if (component.str != null && !component.str.contains("xyz"))
			errors = addError(errors, "str", "invalid str");
		return errors;
	}

}
