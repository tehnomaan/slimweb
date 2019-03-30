package eu.miltema.slimweb.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import static java.util.stream.Collectors.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.slf4j.*;

import com.google.gson.Gson;

import eu.miltema.slimweb.ComponentsReader;

@WebServlet(urlPatterns={"/controller/*"})
public class ControllerServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(ControllerServlet.class);
	private Map<String, ComponentDef> mapComponents;

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			mapComponents = new ComponentsReader().setLogger(s -> log.info(s)).getComponents().stream().map(c -> new ComponentDef(c)).collect(toMap(c -> c.url, c -> c));
			if (mapComponents.isEmpty())
				log.warn("No component definitions were found");
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serviceRequest(new HttpGetAccessor().init(req, resp, "get"));
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serviceRequest(new HttpGetAccessor().init(req, resp, "delete"));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serviceRequest(new HttpPostAccessor().init(req, resp, "post"));
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serviceRequest(new HttpPostAccessor().init(req, resp, "put"));
	}

	private void serviceRequest(HttpAccessor htAccessor) throws IOException {
		String requestName = htAccessor.getUrl();
		log.info("Request " + requestName);
		try {
			try {
				String componentName = htAccessor.getComponentName();
				String actionName = htAccessor.getActionName();
				ComponentDef cdef = mapComponents.get(componentName);
				if (cdef == null)
					throw new HttpException(404, "Cannot map /{0} to component", componentName);
				MethodDef mdef = cdef.methods.get(htAccessor.getMethod() + ":" + (actionName == null ? "" : actionName));
				if (mdef == null)
					throw new HttpException(404, "Cannot map /{0} to action", actionName);
				Gson gson = new Gson();
				String json = htAccessor.getParametersAsJson();
				Object component = gson.fromJson(json, cdef.clazz);
				Object returnValue = mdef.method.invoke(component);
				if (returnValue != null)
					htAccessor.response.getWriter().write(gson.toJson(returnValue));
			}
			catch(IllegalAccessException iae) {
				log.error("", iae);
				throw new HttpException(500, "Service internal error");
			}
			catch(InvocationTargetException ite) {
				Throwable t = ite.getCause();
				if (t instanceof HttpException)
					throw (HttpException) t;
				log.error("", t);
				throw new HttpException(500, "Service internal error");
			}
			log.debug("Finished " + requestName);
		}
		catch(HttpException he) {
			htAccessor.response.sendError(he.getHttpCode(), he.getMessage());
			log.debug("Error " + he.getHttpCode() + "[" + he.getMessage() + "] in " + requestName);
		}
		finally {
			htAccessor.response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
			long tm = System.currentTimeMillis();
			htAccessor.response.setDateHeader("Expires", tm);
			htAccessor.response.setDateHeader("Last-Modified", tm);
			htAccessor.response.flushBuffer();
		}
	}
}
