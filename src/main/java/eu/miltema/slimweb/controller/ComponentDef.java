package eu.miltema.slimweb.controller;

import java.lang.reflect.Method;
import java.util.*;

import eu.miltema.slimweb.Util;
import eu.miltema.slimweb.annot.Component;

public class ComponentDef {
	String url;
	Class<?> clazz;
	Map<String, MethodDef> methods = new HashMap<>();

	public ComponentDef(Class<?> clazz) {
		url = clazz.getAnnotation(Component.class).url();
		if (url.isEmpty())
			url = Util.hyphenate(clazz.getSimpleName());

		for(Method method : clazz.getMethods()) {
			MethodDef mdef = new MethodDef(method);
			methods.put(Util.hyphenate(method.getName()), mdef);
		}
	}
}
