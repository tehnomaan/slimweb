package eu.miltema.slimweb.controller;

import java.io.BufferedReader;
import java.util.Map;

import com.google.gson.Gson;

import eu.miltema.slimweb.HttpException;
import eu.miltema.slimweb.common.HttpAccessor;

class HttpPostAccessor extends HttpAccessor {

	private String json;
	private Map<String, Object> parameters;

	@Override
	public String getParametersAsJson() {
		if (json != null)
			return json;
		try {
			String line;
			StringBuilder sb = new StringBuilder();
			BufferedReader bfr = new BufferedReader(request.getReader());
			while((line = bfr.readLine()) != null)
				sb.append(line+"\n");
			return json = sb.toString();
		}
		catch(Exception x) {
			throw new HttpException(470, "Unable to read request body: {0}", x.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getParameter(String parameterName) {
		if (parameters == null)
			parameters = new Gson().fromJson(getParametersAsJson(), Map.class);
		Object parameter = parameters.get(parameterName);
		return (parameter == null ? null : parameter.toString());
	}

}
