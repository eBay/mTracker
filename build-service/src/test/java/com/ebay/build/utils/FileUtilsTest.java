package com.ebay.build.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

public class FileUtilsTest {
	
	@Test
	public void testLoadFiles() {
		File resourceFolder = new File(this.getClass().getResource("/").getFile());
		File[] files = FileUtils.loadFiles(resourceFolder, ".xml");
		assertNotNull(files);
		assertEquals(1, files.length);
		assertEquals("a.xml", files[0].getName());
		
		files = FileUtils.loadFiles(resourceFolder, ".DONE");
		assertNotNull(files);
		assertEquals(0, files.length);
	}
}
