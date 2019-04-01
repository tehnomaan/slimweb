package eu.miltema.slimweb.annot;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * Declares a component class. For example, if component name is MyComponent then it is accessible via http://[host]/controller/my-component
 * @author Margus
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Component {

	/**
	 * @return component name in URL path, for example "mycomponent"
	 */
	String urlName() default "";
}
