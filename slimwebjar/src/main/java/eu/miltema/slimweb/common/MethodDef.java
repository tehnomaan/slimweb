package eu.miltema.slimweb.common;

import java.lang.reflect.*;
import java.util.Map;

import eu.miltema.slimweb.*;
import eu.miltema.slimweb.annot.ValidateInput;

public class MethodDef {
	public Method method;
	private ArgumentInjector[] injectors;
	public boolean validateInput;

	public MethodDef(Method method) {
		this.method = method;
	}

	public void init(Map<Class<?>, ArgumentInjector> mapInjectors) {
		Class<?>[] types = method.getParameterTypes();
		injectors = new ArgumentInjector[types.length];
		for(int i = 0; i < types.length; i++) {
			ArgumentInjector injector = mapInjectors.get(types[i]);
			if (injector == null)
				throw new IllegalArgumentException("Method " + method.toString() + " declares parameter of type " + types[i].getSimpleName() + ", which was not registered in " + ApplicationConfiguration.class.getSimpleName());
			injectors[i] = injector;
		}
		validateInput = method.isAnnotationPresent(ValidateInput.class);
	}

	public Object invoke(Object component, HttpAccessor htAccessor) throws Throwable {
		try {
			Object[] args = new Object[injectors.length];
			for(int i = 0; i < args.length; i++)
				args[i] = injectors[i].apply(htAccessor);
			return method.invoke(component, args);
		}
		catch(InvocationTargetException ite) {
			throw ite.getCause();
		}
	}
}
