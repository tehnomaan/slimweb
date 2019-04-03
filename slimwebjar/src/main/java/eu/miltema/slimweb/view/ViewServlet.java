package eu.miltema.slimweb.view;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Predicate.*;

import static java.util.stream.Collectors.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.slf4j.*;

import eu.miltema.slimweb.controller.HttpAccessor;
import eu.miltema.slimweb.rcscanner.FileScanner;

@WebServlet(urlPatterns={"/view/*"})
public class ViewServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(ViewServlet.class);
	private Map<String, Map<String, String>> localeLabels;
	private Map<String, Map<String, String>> localetemplateFiles;

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
			initLocaleLabels();
			initLocaleTemplateFiles();
		} catch (Exception e) {
			log.error("", e);
			throw new ServletException(e);
		}
	}

	private void initLocaleLabels() throws Exception {
		final String LBLFILE_PATTERN = "(.*[/|\\\\])?([^/|\\\\]+)(\\.)lbl";//separates file name from directories and extension
		localeLabels = new FileScanner("label files", filename -> filename.endsWith(".lbl")).
				scan("labels").
				collect(toMap(t -> t.path.replaceAll(LBLFILE_PATTERN, "$2"), t -> getLabelsMap(t.content)));
	}

	private Map<String, String> getLabelsMap(String labelFile) {
		return labelFile.lines().
				filter(not(String::isBlank)).
				map(line -> line.split("=")).
				collect(toMap(ls -> ls[0].trim(), ls -> ls[1].trim()));
	}

	private void initLocaleTemplateFiles() throws Exception {
		final String TPTFILE_PATTERN = "(.*[/|\\\\])?([^/|\\\\]+)(\\.)(html|htm|js)";//separates file name from directories and extension
		Map<String, String> templateFiles = new FileScanner("templates", filename -> filename.endsWith(".html") || filename.endsWith(".htm") || filename.endsWith(".js")).
				scan("templates").
				collect(toMap(t -> t.path.replaceAll(TPTFILE_PATTERN, "$2$3$4"), t -> t.content));//drop folder name from key
		localetemplateFiles = localeLabels.entrySet().stream().
				collect(toMap(locale -> locale.getKey(), locale -> resolvedTemplates(locale.getValue(), templateFiles)));
	}

	private Map<String, String> resolvedTemplates(Map<String, String> labels, Map<String, String> templateFiles) {
		final String TPTFILE_PATTERN = "(.+)(\\.)(html|htm|js)";//separates file name from directories and extension
		TemplateResolver resolver = new TemplateResolver();
		resolver.customReplacer("file:", filename -> resolver.replace(templateFiles.getOrDefault(filename, "!!!file:" + filename + "!!!"), labels));//use this same resolver to resolve {-file:xyzfilename-} elements
		return templateFiles.entrySet().stream().collect(toMap(e -> e.getKey().replaceAll(TPTFILE_PATTERN, "$1"), e -> resolver.replace(e.getValue(), labels)));//key: name without extension
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String templateName = req.getPathInfo();
		String lang = new ViewHtAccessor().init(req).getLanguage();
		Map<String, String> localeTemplates = Optional.ofNullable(localetemplateFiles.get(lang)).
				orElseGet(() -> Optional.ofNullable(localetemplateFiles.get("en")).
				orElseGet(() -> localetemplateFiles.values().iterator().next()));
		String template = localeTemplates.get(templateName == null || templateName.length() == 1 ? null : templateName.substring(1));
		resp.getWriter().print(template);
	}
}
