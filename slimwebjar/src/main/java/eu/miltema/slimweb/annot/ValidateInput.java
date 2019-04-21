package eu.miltema.slimweb.annot;

import java.lang.annotation.*;

/**
 * Declares that validation must be performed on input data before invoking this method
 * @author Margus
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ValidateInput {

}
