package eu.miltema.slimweb.controller;

import java.lang.reflect.Method;
import java.util.*;

import eu.miltema.slimweb.annot.Component;
import eu.miltema.slimweb.common.SlimwebUtil;

class ComponentDef {
	Class<?> clazz;
	boolean requiresSession;
	String url;
	Map<String, MethodDef> methods = new HashMap<>();

	ComponentDef(Class<?> clazz) {
		this.clazz = clazz;
		this.requiresSession = clazz.getAnnotation(Component.class).requireSession();
		url = clazz.getAnnotation(Component.class).urlName();
		if (url.isEmpty())
			url = SlimwebUtil.hyphenate(clazz.getSimpleName());

		for(Method method : clazz.getMethods()) {
			MethodDef mdef = new MethodDef(method);
			String name = method.getName();
			if (name.startsWith("get"))
				name = "get:" + SlimwebUtil.hyphenate(name.substring(3));
			else if (name.startsWith("delete"))
				name = "delete:" + SlimwebUtil.hyphenate(name.substring(6));
			else if (name.startsWith("post"))
				name = "post:" + SlimwebUtil.hyphenate(name.substring(4));
			else if (name.startsWith("put"))
				name = "put:" + SlimwebUtil.hyphenate(name.substring(3));
			else continue;
			methods.put(name, mdef);
		}
	}
}
