package com.ebay.build.profile.mdda.bean;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.ebay.build.profiler.mdda.bean.DArtifact;

public class DArtifactTest {
	@Test
	public void testAddDuplicate() {
		DArtifact artifact1 = new DArtifact();
		
		artifact1.setGroup_id("com.ebay");
		artifact1.setArtifact_id("resources");
		artifact1.setVersion("1.2.44");
		artifact1.setExtension("jar");
		artifact1.setQuick_url("url_4_artifact1");
		
		DArtifact artifact2 = new DArtifact();
		
		artifact2.setGroup_id("com.ebay");
		artifact2.setArtifact_id("resources");
		artifact2.setVersion("1.2.44");
		artifact2.setExtension("jar");
		artifact2.setQuick_url("url_4_artifact2");
		
		Set<DArtifact> artifacts = new HashSet<DArtifact>();
		
		artifacts.add(artifact1);
		artifacts.add(artifact2);
		
		DArtifact result = artifacts.iterator().next();
		assertEquals(1, artifacts.size());
		assertEquals("url_4_artifact1", result.getQuick_url());
	}
}
