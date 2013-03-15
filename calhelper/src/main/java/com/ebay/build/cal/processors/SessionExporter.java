package com.ebay.build.cal.processors;

import java.io.File;

import com.ebay.build.cal.model.Session;
import com.ebay.build.cal.query.utils.DateUtils;

public class SessionExporter {

	public void process(Session session, File targetFile) {
	}
	
	protected String getModelAsString(Session session) {
		StringBuffer sBuffer = new StringBuffer();
		
		sBuffer.append("SQLLog for " + session.getPool().getName() + "-MavenBuild:" + session.getPool().getMachine().getName());
		sBuffer.append("Environment: raptor-build-tracking");
		sBuffer.append("Start: " + DateUtils.getCALDateTimeString(session.getStartTime()));
		sBuffer.append(session.toString());
		return sBuffer.toString();
	}
	
}
