package eu.miltema.slimweb.controller;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.slf4j.*;
import com.google.gson.Gson;
import eu.miltema.slimweb.*;
import eu.miltema.slimweb.common.HttpAccessor;
import eu.miltema.slimweb.view.Labels;

@WebServlet(urlPatterns={"/controller/*"})
public class ControllerServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(ControllerServlet.class);
	private Map<String, ComponentDef> mapComponents;//urlName->component
	private Map<Class<?>, ComponentDef> mapComponentClasses = new HashMap<Class<?>, ComponentDef>();//class->component
	private Map<Class<?>, ArgumentInjector> mapInjectors = new HashMap<>();
	private ApplicationConfiguration configuration;
	private String[] validOrigins;
	private Labels labels;

	private static Predicate<MethodDef> excludePush = mdef -> !mdef.method.getName().equals("pushStarted") && !mdef.method.getName().equals("pushTerminated");

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			ComponentsReader cr = new ComponentsReader(s -> log.debug(s));
			mapComponents = cr.getComponentsAsStream().
					map(c -> new ComponentDef(c)).
					peek(c -> mapComponentClasses.put(c.clazz, c)).
					collect(toMap(c -> c.url, c -> c));
			if (mapComponents.isEmpty())
				log.warn("No component definitions were found");

			mapInjectors.put(HttpAccessor.class, a -> a);
			mapInjectors.put(HttpServletRequest.class, a -> a.request);
			mapInjectors.put(HttpServletResponse.class, a -> a.response);
			mapInjectors.put(HttpSession.class, a -> a.request.getSession(false));
			configuration = cr.getInitializer();
			configuration.registerInjectors(mapInjectors);
			validOrigins = configuration.getValidOrigins();
			mapComponents.values().forEach(cdef -> cdef.methods.values().stream().filter(excludePush).forEach(mdef -> mdef.init(mapInjectors)));
			labels = new Labels();
		} catch (Exception e) {
			log.error("", e);
			throw new ServletException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serviceRequest(new HttpGetAccessor().init(req, resp, "get"));
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serviceRequest(new HttpGetAccessor().init(req, resp, "delete").detectCsrf(validOrigins));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serviceRequest(new HttpPostAccessor().init(req, resp, "post").detectCsrf(validOrigins));
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serviceRequest(new HttpPostAccessor().init(req, resp, "put").detectCsrf(validOrigins));
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
				if (mdef == null) {
					if (actionName != null)
						throw new HttpException(404, "Cannot map /{0} to action", actionName);
					else throw new HttpException(405, "Component {0} does not support method {1}", componentName, htAccessor.getMethod());
				}
				if (htAccessor.request.getSession(false) == null && cdef.requiresSession)
					throw new Redirect(configuration.getLoginView());
				Gson gson = new WebJsonBuilder().build();
				String json = htAccessor.getParametersAsJson();
				Object component = gson.fromJson(json, cdef.clazz);
				if (component == null)
					component = cdef.clazz.getConstructor().newInstance();

				if (mdef.validateInput) {
					Map<String, String> vResult = cdef.validator.validate(component, labels.getLabels(htAccessor.getLanguage()));
					if (vResult != null) {
						log.debug("Error 400 [Validation failed] in " + requestName);
						htAccessor.response.setStatus(400);
						htAccessor.response.getWriter().write(gson.toJson(vResult));
						return;
					}
				}

				Object returnValue = mdef.invoke(component, htAccessor);
				if (returnValue != null)
					htAccessor.response.getWriter().write(gson.toJson(returnValue));
			}
			catch(Redirect redirect) {
				redirect(htAccessor, redirect);
			}
			catch(HttpException he) {
				throw he;
			}
			catch(Throwable t) {
				log.error("", t);
				throw new HttpException(500, "Service internal error");
			}
			log.debug("Finished " + requestName);
		}
		catch(HttpException he) {
			htAccessor.response.sendError(he.getHttpCode(), he.getMessage());
			log.debug("Error " + he.getHttpCode() + " [" + he.getMessage() + "] in " + requestName);
		}
		finally {
			htAccessor.response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
			long tm = System.currentTimeMillis();
			htAccessor.response.setDateHeader("Expires", tm);
			htAccessor.response.setDateHeader("Last-Modified", tm);
			htAccessor.response.flushBuffer();
		}
	}

	private void redirect(HttpAccessor htAccessor, Redirect redirect) throws IOException {
		String targetPath = redirect.pathToView;
		if (targetPath == null) {
			ComponentDef cdef = mapComponentClasses.get(redirect.targetComponent);
			if (cdef == null)
				throw new HttpException(500, "Redirecting to non-@Component " + redirect.targetComponent.getName() + " is not allowed");
			targetPath = "/view/" + cdef.url;
		}
		else {
			if (targetPath.indexOf("/controller/") < 0)
				targetPath = "/view/" + targetPath.replaceAll("(.+)\\.(html|htm|js)", "$1");
		}
		log.info("Redirecting to " + targetPath);
		htAccessor.response.setHeader("Location", targetPath);
		String accept = htAccessor.request.getHeader("Accept");
		boolean acceptsJson = (accept != null && accept.toLowerCase().contains("application/json"));
		htAccessor.response.setStatus(acceptsJson ? 250 : 303);
	}
}
