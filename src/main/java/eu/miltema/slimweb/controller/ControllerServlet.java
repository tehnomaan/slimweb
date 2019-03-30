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
			mapComponents = new ComponentsReader().getComponents().stream().map(c -> new ComponentDef(c)).collect(toMap(c -> c.url, c -> c));
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serviceRequest(new HttpGetAccessor().init(req, resp));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serviceRequest(new HttpPostAccessor().init(req, resp));
	}

	private void serviceRequest(HttpAccessor htAccessor) throws IOException {
		String componentName = htAccessor.getComponentName();
		String actionName = htAccessor.getActionName();
		String requestName = "/" + componentName + (actionName == null ? "" : "/" + actionName);
		log.info("Request " + requestName);
		try {
			try {
				ComponentDef cdef = mapComponents.get(componentName);
				if (cdef == null)
					throw new HttpException(404, "Unknown component reference /{0}", componentName);
				MethodDef mdef = cdef.methods.get(actionName);
				if (mdef == null)
					throw new HttpException(404, "Action /{0} not found", actionName);
				Gson gson = new Gson();
				Object component = gson.fromJson(htAccessor.getParametersAsJson(), cdef.clazz);
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
			log.debug("Error in " + requestName);
		}
		finally {
			htAccessor.response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
			long tm = System.currentTimeMillis();
			htAccessor.response.setDateHeader("Expires", tm);
			htAccessor.response.setDateHeader("Last-Modified", tm);
		}
	}
}
