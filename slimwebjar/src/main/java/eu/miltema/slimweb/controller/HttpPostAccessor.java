package eu.miltema.slimweb.controller;

import java.io.BufferedReader;
import java.util.Map;

import com.google.gson.Gson;

class HttpPostAccessor extends HttpAccessor {

	private String json;
	private Map<String, String> parameters;

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
		return parameters.get(parameterName);
	}

}
