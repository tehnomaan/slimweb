package eu.miltema.slimweb.controller;

import static java.util.stream.Collectors.toMap;

import java.util.Map;

import com.google.gson.Gson;

public class HttpGetAccessor extends HttpAccessor {

	@Override
	public String getParametersAsJson() {
		Map<Object, Object> parameters = request.getParameterMap().entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue()[0]));
		return new Gson().toJson(parameters);
	}

	@Override
	public String getParameter(String parameterName) {
		String[] val = request.getParameterMap().get(parameterName);
		return (val == null || val.length == 0 ? null : val[0]);
	}
}
