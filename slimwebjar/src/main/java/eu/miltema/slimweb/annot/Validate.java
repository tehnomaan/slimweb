package eu.miltema.slimweb.annot;

import java.lang.annotation.*;

/**
 * Perform validation on this field
 * @author Margus
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Validate {

	public enum V {MANDATORY, EMAIL, MINLEN, MAXLEN, MINVAL, MAXVAL}

	/**
	 * @return list of validators
	 */
	V[] value();

	/**
	 * @return minval, maxval; if both, MIVAL and MAXVAL are used, return {minval, maxval}
	 */
	double[] limiter() default 0;
}
