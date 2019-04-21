package eu.miltema.slimweb.annot;

import java.lang.annotation.*;
import eu.miltema.slimweb.controller.*;

/**
 * Declares a component class. For example, if component name is MyComponent then it is accessible via http://[host]/controller/my-component
 * @author Margus
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {

	/**
	 * @return component name in URL path, for example "mycomponent"
	 */
	String urlName() default "";

	/**
	 * @return true, when component access requires session existence
	 */
	boolean requireSession() default true;

	/**
	 * @return true, if this component is subject to web template generation. This is a directive to dedicated template generator, Slimweb itself does not generate web templates
	 */
	boolean generateTemplate() default true;

	/**
	 * @return validator class, performing the validation of incoming data
	 */
	Class<? extends Validator> validator() default ValidatorAdapter.class;
}
