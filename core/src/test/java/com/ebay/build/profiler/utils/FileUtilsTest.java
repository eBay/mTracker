package com.ebay.build.profiler.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class FileUtilsTest {
	
	File resourceFolder = new File(this.getClass().getResource("/").getFile());
	
	@Test
	public void testLoadFiles() {
		File[] files = FileUtils.loadFiles(resourceFolder, ".xml");
		assertNotNull(files);
		assertEquals(1, files.length);
		assertEquals("a.xml", files[0].getName());
		
		files = FileUtils.loadFiles(resourceFolder, ".DONE");
		assertNotNull(files);
		assertEquals(0, files.length);
	}
	
	@Test
	public void testReadFile() {
		File[] files = FileUtils.loadFiles(resourceFolder, ".xml");
		String contents = FileUtils.readFile(files[0]);
		assertEquals("<a>test</a>", contents.trim());
	}
	
	@Test
	public void testDiskClean() {
		File dc = new File(resourceFolder, "diskclean");
		
		assertTrue(dc.exists());
		
		FileUtils.renameDoneFile(new File(dc, "filestodelete.txt"));
		
		File[] files = FileUtils.loadDoneFiles(dc);
		assertEquals(2, files.length);
		FileUtils.diskClean(dc, 0);
		files = FileUtils.loadDoneFiles(dc);
		assertEquals(0, files.length);
	}
	
	@Test
	public void testWrite() {
		File dc = new File(resourceFolder, "diskclean");
		File file = new File(dc, "test.done");
		assertFalse(file.exists());
		FileUtils.writeToFile(file, "some contents");
		assertTrue(file.exists());
	}
}
