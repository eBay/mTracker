package com.ebay.build.profiler.mdda.http;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.ebay.build.profiler.mdda.bean.DArtifact;

public class DownloadExecutor {
	
	private final static int MAX_THREAD_COUNT = 20;
	private final List<DArtifact> artifacts;
	private final boolean debug;
	
	public DownloadExecutor(List<DArtifact> artifacts,boolean debug) {
		this.artifacts = artifacts;
		this.debug = debug;
	}
	
	public void run() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		CloseableHttpClient httpClient = HttpClients.custom()
		        .setConnectionManager(cm)
		        .build();
		
		ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
		
		List<Future<Integer>> list = new ArrayList<Future<Integer>>();
		for (DArtifact artifact : artifacts) {
			Callable<Integer> worker = new ArtifactGet(httpClient, artifact, debug);
			Future<Integer> submit = executor.submit(worker);
			list.add(submit);
		}
		
		
		int totalDownloads = 0;
		// now retrieve the result
	    for (Future<Integer> future : list) {
	      try {
	    	  totalDownloads += future.get();
	      } catch (InterruptedException e) {
	        e.printStackTrace();
	      } catch (ExecutionException e) {
	        e.printStackTrace();
	      }
	    }
		// This will make the executor accept no new threads
	    // and finish all existing threads in the queue
	    executor.shutdown();
	    if (debug) {
	    	System.out.println("Finished all threads. Downloaded " + totalDownloads + " artifacts.");
	    }
	}
}
