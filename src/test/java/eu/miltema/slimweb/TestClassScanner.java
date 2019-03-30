package eu.miltema.slimweb;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import eu.miltema.slimweb.ClassFileScanner;
import eu.miltema.slimweb.annot.Component;
import eu.miltema.slimweb.controller.ControllerServlet;

public class TestClassScanner {

	@Test
	public void testScannerFindInDir() throws Exception {
		Collection<Class<?>> classes = new ClassFileScanner().findClasses("eu.miltema.slimweb.controller");
		assertTrue(classes.contains(ControllerServlet.class));
		assertFalse(classes.contains(Component.class));
		assertFalse(classes.contains(ClassFileScanner.class));
	}

	@Test
	public void testScannerFindInSubDir() throws Exception {
		Collection<Class<?>> classes = new ClassFileScanner().findClasses("eu.miltema.slimweb");
		assertTrue(classes.contains(ControllerServlet.class));
	}

	@Test
	public void testScannerFindInAllPackages() throws Exception {
		Collection<Class<?>> classes = new ClassFileScanner().findClasses();
		assertTrue(classes.contains(ControllerServlet.class));
	}
}
