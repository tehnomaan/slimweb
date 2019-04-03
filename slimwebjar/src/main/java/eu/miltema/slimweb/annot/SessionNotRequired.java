package eu.miltema.slimweb.annot;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * Indicates that this method does not require session existence.
 * When used ass class-level annotation, none of the class methods require session existence.
 * @author Margus
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface SessionNotRequired {

}
