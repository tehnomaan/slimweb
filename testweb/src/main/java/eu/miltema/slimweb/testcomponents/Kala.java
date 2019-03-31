package eu.miltema.slimweb.testcomponents;

import java.io.IOException;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

//import eu.miltema.slimweb.ClassScanner;

@WebServlet(urlPatterns={"/kala/*"})
public class Kala extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		try {
//			Stream<Class<?>> list = new ClassScanner("ggg").scan("eu.miltema");
//			list = null;
//		} catch (Exception e) {
//			throw new ServletException(e);
//		}
		resp.getWriter().println("kala!");
	}

}
