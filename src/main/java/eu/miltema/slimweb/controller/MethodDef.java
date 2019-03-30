package eu.miltema.slimweb.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import eu.miltema.slimweb.ApplicationInitializer;
import eu.miltema.slimweb.ArgumentInjector;

public class MethodDef {
	public Method method;
	private ArgumentInjector[] injectors;

	public MethodDef(Method method) {
		this.method = method;
	}

	public void init(Map<Class<?>, ArgumentInjector> mapInjectors) {
		Class<?>[] types = method.getParameterTypes();
		injectors = new ArgumentInjector[types.length];
		for(int i = 0; i < types.length; i++) {
			ArgumentInjector injector = mapInjectors.get(types[i]);
			if (injector == null)
				throw new IllegalArgumentException("Method " + method.toString() + " declares parameter of type " + types[i].getSimpleName() + ", which was not registered in " + ApplicationInitializer.class.getSimpleName());
			injectors[i] = injector;
		}
	}

	public Object invoke(Object component, HttpAccessor htAccessor) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] args = new Object[injectors.length];
		for(int i = 0; i < args.length; i++)
			args[i] = injectors[i].apply(htAccessor);
		return method.invoke(component, args);
	}
}
