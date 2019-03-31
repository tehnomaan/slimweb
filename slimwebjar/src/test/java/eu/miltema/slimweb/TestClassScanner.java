package eu.miltema.slimweb;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Test;

import eu.miltema.slimweb.FileScanner;
import eu.miltema.slimweb.annot.Component;
import eu.miltema.slimweb.controller.ControllerServlet;

public class TestClassScanner {

	@Test
	public void testScannerFindInDir() throws Exception {
		Collection<Class<?>> classes = new ClassScanner(null).scan("eu.miltema.slimweb.controller").collect(Collectors.toList());
		assertTrue(classes.contains(ControllerServlet.class));
		assertFalse(classes.contains(Component.class));
		assertFalse(classes.contains(FileScanner.class));
	}

//	@Test
//	public void testScannerFindInSubDir() throws Exception {
//		Collection<Class<?>> classes = new ClassScanner(null).scan("eu.miltema.slimweb").collect(Collectors.toList());
//		assertTrue(classes.contains(ControllerServlet.class));
//	}
//
//	@Test
//	public void testScannerFindInAllPackages() throws Exception {
//		Collection<Class<?>> classes = new ClassScanner(null).scan().collect(Collectors.toList());
//		assertTrue(classes.contains(ControllerServlet.class));
//	}
}
