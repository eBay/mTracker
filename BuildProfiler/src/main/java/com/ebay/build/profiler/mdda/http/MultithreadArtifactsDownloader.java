package com.ebay.build.profiler.mdda.http;

import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.ebay.build.profiler.mdda.bean.DArtifact;

public class MultithreadArtifactsDownloader {
	
	private final static int MAX_THREAD_COUNT = 10;
	private final List<DArtifact> artifacts;
	private final boolean debug;
	
	public MultithreadArtifactsDownloader(List<DArtifact> artifacts,boolean debug) {
		this.artifacts = artifacts;
		this.debug = debug;
	}
	
	public void run() {
		
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		CloseableHttpClient httpClient = HttpClients.custom()
		        .setConnectionManager(cm)
		        .build();

		for (int rounds = 0; rounds < artifacts.size() / MAX_THREAD_COUNT + 1; rounds++) {
			
			List<DArtifact> subList;
			if (rounds != artifacts.size() / MAX_THREAD_COUNT) {
				subList = artifacts.subList(rounds * MAX_THREAD_COUNT, (rounds + 1) * MAX_THREAD_COUNT);
			} else {
				subList = artifacts.subList(rounds * MAX_THREAD_COUNT, artifacts.size());
			}
			
			if (debug) {
				System.out.println("[MDDA] Downloading Round " + rounds + ", downloading " + subList.size() + " artifacts.");
			}
			
			// create a thread for each URI
			GetThread[] threads = new GetThread[subList.size()];
			for (int i = 0; i < threads.length; i++) {
				threads[i] = new GetThread(httpClient, subList.get(i), debug);
			}

			// start the threads
			for (int j = 0; j < threads.length; j++) {
				threads[j].start();
			}

			// join the threads
			for (int j = 0; j < threads.length; j++) {
				try {
					threads[j].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
