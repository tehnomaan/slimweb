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
		this.clazz = clazz;
		url = clazz.getAnnotation(Component.class).urlName();
		if (url.isEmpty())
			url = Util.hyphenate(clazz.getSimpleName());

		for(Method method : clazz.getMethods()) {
			MethodDef mdef = new MethodDef(method);
			String name = method.getName();
			if (name.startsWith("get"))
				name = "get:" + Util.hyphenate(name.substring(3));
			else if (name.startsWith("delete"))
				name = "delete:" + Util.hyphenate(name.substring(6));
			else if (name.startsWith("post"))
				name = "post:" + Util.hyphenate(name.substring(4));
			else if (name.startsWith("put"))
				name = "put:" + Util.hyphenate(name.substring(3));
			else continue;
			methods.put(name, mdef);
		}
	}
}
