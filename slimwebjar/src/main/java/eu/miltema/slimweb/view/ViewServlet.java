package eu.miltema.slimweb.view;

import java.io.IOException;
import java.util.*;

import static java.util.function.Predicate.*;
import static java.util.stream.Collectors.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.slf4j.*;
import eu.miltema.cpscan.FileScanner;
import eu.miltema.slimweb.ApplicationConfiguration;
import eu.miltema.slimweb.ComponentsReader;
import eu.miltema.slimweb.HttpException;
import eu.miltema.slimweb.common.HttpAccessor;

@WebServlet(urlPatterns={"/view/*"})
public class ViewServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(ViewServlet.class);
	private Map<String, Map<String, String>> localeLabels;
	private Map<String, Map<String, String>> localetemplateFiles;
	private ApplicationConfiguration configuration;

	private class ViewHtAccessor extends HttpAccessor {
		@Override
		public String getParametersAsJson() {
			return null;
		}
		@Override
		public String getParameter(String parameterName) {
			return null;
		}
	};

	@Override
	public void init() throws ServletException {
		try {
			configuration = new ComponentsReader(s -> log.debug(s)).getInitializer();
			initLocaleLabels();
			initLocaleTemplateFiles();
			log.debug("ViewServlet initialization complete");
		} catch (Exception e) {
			log.error("", e);
			throw new ServletException(e);
		}
	}

	private void initLocaleLabels() throws Exception {
		final String LBLFILE_PATTERN = "(.*[/|\\\\])?([^/|\\\\]+)(\\.)lbl$";//separates file name from directories and extension
		log.info("Looking for label files");
		localeLabels = new FileScanner(s -> log.info(s), filename -> filename.endsWith(".lbl")).
				scan("labels").
				collect(toMap(t -> t.path.replaceAll(LBLFILE_PATTERN, "$2"), t -> getLabelsMap(t.path, t.content), (f1, f2) -> labelsConflict(f1, f2)));
	}

	private Map<String, String> labelsConflict(Map<String, String> labels1, Map<String, String> labels2) {
		log.warn("Multiple files for the same locale");
		return labels1;
	}

	private Map<String, String> getLabelsMap(String filename, String labelFile) {
		log.debug("Processing file " + filename);
		Map<String, String> ret = labelFile.lines().
				filter(not(String::isBlank)).
				map(line -> line.split("=|\\t")).
				filter(ls -> ls.length >= 2).
				collect(toMap(ls -> ls[0].trim(), ls -> ls[1].trim()));
		return ret;
	}

	private void initLocaleTemplateFiles() throws Exception {
		final String TPTFILE_PATTERN = "(.*[/|\\\\])?([^/|\\\\]+)(\\.)(html|htm|js)$";//separates file name from directories and extension
		log.info("Looking for template files");
		Map<String, String> templateFiles = new FileScanner(s -> log.info(s), filename -> filename.endsWith(".html") || filename.endsWith(".htm") || filename.endsWith(".js")).
				scan("templates").
				collect(toMap(t -> t.path.replaceAll(TPTFILE_PATTERN, "$2$3$4"), t -> t.content));//drop folder name from key
		localetemplateFiles = localeLabels.entrySet().stream().
				collect(toMap(locale -> locale.getKey(), locale -> resolveLanguageTemplates(locale.getValue(), templateFiles)));
	}

	private Map<String, String> resolveLanguageTemplates(Map<String, String> labels, Map<String, String> templateFiles) {
		TemplateResolver resolver = new TemplateResolver();
		resolver.customReplacer("file:", filename -> resolver.replace(templateFiles.getOrDefault(filename, "!!!file:" + filename + "!!!"), labels, viewName(filename)));//use this same resolver to resolve {-file:xyzfilename-} elements
		resolver.customReplacer("template:", filename -> "{-template:-}");//preserve template placeholder
		return templateFiles.entrySet().stream().
				collect(toMap(e -> viewName(e.getKey()),//key: name without extension
						e -> resolver.replace(e.getValue(), labels, viewName(e.getKey())),
						(t1, t2) -> t1));//multiple files with same name: use the first of the files
	}

	private String viewName(String filename) {
		final String TPTFILE_PATTERN = "(.+)(\\.)(html|htm|js)";//separates file name from directories and extension
		return filename.replaceAll(TPTFILE_PATTERN, "$1");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String view = req.getPathInfo();
		HttpAccessor htAccessor = new ViewHtAccessor().init(req, resp);
		String lang = htAccessor.getLanguage();
		Map<String, String> localeTemplates = Optional.ofNullable(localetemplateFiles.get(lang)).
				orElseGet(() -> Optional.ofNullable(localetemplateFiles.get("en")).
				orElseGet(() -> localetemplateFiles.values().iterator().next()));
		try {
			if (view == null || view.length() < 2)
				throw new HttpException(404, "View name missing");
			String templateName = view.substring(1);
			String template = localeTemplates.get(templateName);
			if (template == null)
				throw new HttpException(404, "Template " + templateName + " not found");
			String frameName = configuration.getFrameForTemplate(template, htAccessor);
			String frame = localeTemplates.get(frameName);
			if (frame == null)
				throw new HttpException(404, "Frame template " + frameName + " not found");
			String html = new TemplateResolver().customReplacer("template:", filename -> template).replace(frame, null, templateName);
			resp.getWriter().print(html);
		}
		catch(HttpException he) {
			resp.sendError(he.getHttpCode(), he.getMessage());
			log.info(he.getHttpCode() + ": " + he.getMessage());
		}
	}
}
