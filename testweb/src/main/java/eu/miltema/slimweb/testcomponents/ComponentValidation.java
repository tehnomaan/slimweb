package eu.miltema.slimweb.testcomponents;

import eu.miltema.slimweb.annot.*;
import eu.miltema.slimweb.annot.Validate.V;

@Component(requireSession = false)
public class ComponentValidation {

	@Validate(value = {V.MANDATORY, V.EMAIL, V.MINLEN, V.MAXLEN}, limiter = {6, 20})
	public String email;

	@Validate(value = {V.MINVAL, V.MAXVAL}, limiter = {2, 9})
	public Integer numeric;

	@ValidateInput
	public ComponentValidation postValidate() {
		numeric = 6;
		return this;
	}

	public ComponentValidation postNoValidation() {
		numeric = 12;
		return this;
	}
}
