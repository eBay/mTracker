package com.ebay.build.profiler.publisher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ebay.build.profiler.model.Session;
import com.ebay.build.profiler.utils.FileUtils;

public abstract class AbstractPublisher implements Publisher {
	private PublisherConfig config;
	private SessionTransformer transformer = new SessionTransformer();
	protected final LoaderProcessor loaderProcessor = new LoaderProcessor();
	
	 
	public AbstractPublisher() {
		this.config = new PublisherConfig();
	}
	
	public AbstractPublisher(PublisherConfig config) {
		this.config = config;
	}
	
	public void preProcess() {
		System.out.println("[INFO] ------------------");
		System.out.println("[INFO] " + getClass().getSimpleName() + " Preprocess");
		System.out.println("[INFO] ------------------");
		try {
			FileUtils.diskClean(config.getTargetFolder(), config.getRetensionDays());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void postProcess(List<File> files) {
		System.out.println("[INFO] ------------------");
		System.out.println("[INFO] " + getClass().getSimpleName() + " Postprocess");
		System.out.println("[INFO] ------------------");
		for (File file : files) {
			FileUtils.renameDoneFile(file);
		}
	}
	
	public void publish() {
		System.out.println("[INFO] ------------------");
		System.out.println("[INFO] " + getClass().getSimpleName() + " Publishing...");
		System.out.println("[INFO] ------------------");

		preProcess();
		
		File[] files = loadSessions();
		if (files == null || files.length == 0) {
			System.out.println("[INFO] Found 0 log files");
		}
		
		List<File> filesDone = new ArrayList<File>();
		
		for (File file : files) {
			Session session = process(file);
			if (null == session) {
				continue;
			}
			transformer.tranform(session);

			System.out.println("[INFO] Store Session -- "
					+ session.getEnvironment() + " " + session.getAppName()
					+ " " + session.getStartTime());

			try {
				loaderProcessor.process(session);
			} catch(Exception e) {
				System.out.println("[ERROR] store session failed " + session.getAppName());
				continue;
			} finally {
				System.out.println("[INFO] Store Session -- "
						+ session.getEnvironment() + " " + session.getStartTime()
						+ " DONE!");
			}
			
			filesDone.add(file);
		}		
		
		postProcess(filesDone);
	}
	
	public Session process(File file) {
		System.out.println("[INFO] ------------------");
		System.out.println("[INFO] " + getClass().getSimpleName() + " processing file " + file);
		System.out.println("[INFO] ------------------");
		String content = FileUtils.readFile(file);
		if (!isValidSession(content)) {
			System.out.println("[INFO] Skipped " + file + " is not a complete session.");
			return null;
		}
		return getProcessor().process(content);
	}
	
	public PublisherConfig getConfig() {
		return this.config;
	}
	
	public File[] loadSessions() {
		System.out.println("[INFO] Looking into target folder: " + config.getTargetFolder());
		return FileUtils.loadFiles(config.getTargetFolder(), this.getTargetFileExtension());
	}
}
