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
	Validator validator;

	ComponentDef(Class<?> clazz) {
		this.clazz = clazz;
		this.requiresSession = clazz.getAnnotation(Component.class).requireSession();
		url = clazz.getAnnotation(Component.class).urlName();
		if (url.isEmpty())
			url = SlimwebUtil.hyphenate(clazz.getSimpleName());

		try {
			Class<? extends Validator> validatorClass = clazz.getAnnotation(Component.class).validator();
			if (validatorClass == ValidatorAdapter.class)
				validator = new ValidatorAdapter(clazz);
			else validator = validatorClass.getConstructor().newInstance();
		}
		catch(Exception x) {
			throw new RuntimeException(x);
		}

		for(Method method : clazz.getMethods()) {
			if (method.getDeclaringClass() == Object.class)
				continue;
			if ("validate".equals(method.getName()) && Validator.class.isAssignableFrom(clazz))
				continue;
			MethodDef mdef = new MethodDef(method);
			String name = method.getName();
			if (name.startsWith("get"))
				name = "get:" + SlimwebUtil.hyphenate(name.substring(3));
			else if (name.startsWith("delete"))
				name = "delete:" + SlimwebUtil.hyphenate(name.substring(6));
			else if (name.startsWith("put"))
				name = "put:" + SlimwebUtil.hyphenate(name.substring(3));
			else if (name.startsWith("post"))
				name = "post:" + SlimwebUtil.hyphenate(name.substring(4));
			else name = "post:" + SlimwebUtil.hyphenate(name);
			methods.put(name, mdef);
		}

	}
}
