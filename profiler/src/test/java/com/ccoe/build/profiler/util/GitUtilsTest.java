package com.ccoe.build.profiler.util;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.ccoe.build.profiler.util.GitUtil;

public class GitUtilsTest {
	@Test
	public void testIsValidGitDir() {
		File currentFolder = new File(getClass().getResource("/").getFile());
		File gitBase = currentFolder.getParentFile().getParentFile().getParentFile();
		
		File gitMeta = GitUtil.findGitRepository(gitBase);
		assertEquals(gitBase.getName(), gitMeta.getName());
		assertEquals(gitBase.getName(), GitUtil.findGitRepository(currentFolder).getName());
		
		//File gitConfig = new File(new File(gitMeta,".git"), "config");
		//assertTrue(GitUtil.getRepoName(gitConfig).startsWith("https://hostname/DevExTech/maven-time-tracking")); 
	}
}

