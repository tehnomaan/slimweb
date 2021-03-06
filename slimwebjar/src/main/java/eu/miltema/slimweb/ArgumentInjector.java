package eu.miltema.slimweb;

import java.util.function.Function;

import eu.miltema.slimweb.common.HttpAccessor;

/**
 * A function, which injects a single method argument
 * @author Margus
 */
public interface ArgumentInjector extends Function<HttpAccessor, Object> {
}
