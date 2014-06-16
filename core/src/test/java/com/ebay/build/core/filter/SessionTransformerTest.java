package com.ebay.build.core.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ebay.build.core.filter.SessionTransformer;
import com.ebay.build.core.model.Session;

public class SessionTransformerTest {
	@Test
	public void testTransform() {
		SessionTransformer transformer = new SessionTransformer();
		Session session = new Session();
		session.setFullStackTrace("org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:2.5:compile (default-compile) on project SRPDomain: Compilation failure");
		transformer.tranform(session);
		
		assertEquals("CompilationFailure", session.getFilter());
		assertEquals("exclude", session.getCategory());
	}
}
