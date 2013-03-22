package com.ebay.build.jenkins.LogPublisherJenkins;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.AbstractProject;
import hudson.model.listeners.ItemListener;

import java.io.IOException;

@Extension
public class JobListener extends ItemListener {
	@Override
	public void onCreated(Item item) {
		AbstractProject<?, ?> project = (AbstractProject) item;
		System.out.println("Project created: " + project.getName());
		if (!LogPublisherTask.isPublisherExist(project)) {
			try {
				System.out.println("Register the publisher to project: : " + project.getName());
				project.getPublishersList().add(new LogPublisherTask());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
