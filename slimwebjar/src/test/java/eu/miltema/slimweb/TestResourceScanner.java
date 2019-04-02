package eu.miltema.slimweb;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.*;

import org.junit.Test;

import eu.miltema.slimweb.annot.Component;
import eu.miltema.slimweb.controller.ComponentDef;
import eu.miltema.slimweb.rcscanner.*;

public class TestResourceScanner {

	@Test
	public void testScannerFindInDir() throws Exception {
		Collection<Class<?>> classes = new ClassScanner(null).scan("eu.miltema.slimweb.controller").collect(toList());
		assertTrue(classes.contains(ComponentDef.class));
		assertFalse(classes.contains(Component.class));
		assertFalse(classes.contains(FileScanner.class));
	}

	@Test
	public void testScannerFindInSubDir() throws Exception {
		Collection<Class<?>> classes = new ClassScanner(null).scan("eu.miltema.slimweb").collect(toList());
		assertTrue(classes.contains(ComponentDef.class));
	}

	@Test
	public void testScannerFindInAllPackages() throws Exception {
		Collection<Class<?>> classes = new ClassScanner(null).scan().collect(toList());
		assertTrue(classes.contains(ComponentDef.class));
	}

	@Test
	public void testFileScanner() throws Exception {
		List<FileTuple> list = new FileScanner(null, name -> name.endsWith(".txt")).scan("testfolder").collect(toList());
		assertEquals(1, list.size());
		assertEquals("testfolder" + File.separator + "file1.txt", list.get(0).path);
		assertEquals("This file must be loaded by TestResourceScanner", list.get(0).content.strip());
	}
}
