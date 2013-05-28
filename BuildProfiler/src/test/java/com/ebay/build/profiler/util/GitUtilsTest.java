package com.ebay.build.profiler.util;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class GitUtilsTest {
	@Test
	public void testIsValidGitDir() {
		File currentFolder = new File(getClass().getResource("/").getFile());
		File gitBase = currentFolder.getParentFile().getParentFile().getParentFile();
		
		File gitMeta = GitUtil.findGitRepository(gitBase);
		assertEquals("maven-time-tracking", gitMeta.getName());
		
		
		File gitConfig = new File(new File(gitMeta,".git"), "config");
		
		
		assertEquals("https://github.scm.corp.ebay.com/DevExTech/maven-time-tracking", 
				GitUtil.getRepoName(gitConfig));
	}
}
