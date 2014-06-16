package com.ebay.build.profile.mdda.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ebay.build.profiler.mdda.bean.DArtifact;
import com.ebay.build.profiler.mdda.util.FormatTransform;


public class FormatTransformTest {

	@Test
	public void testParseRepository() {
		String source = "central (http://nxraptor/content/repositories/releases, release)";
		DArtifact artifact = new DArtifact();
		FormatTransform.parseRepository(source, artifact);
		assertEquals("http://nxraptor/content/repositories/releases", artifact.getRepositoryURL());
		assertEquals("release", artifact.getRepositoryId());
	}
}
