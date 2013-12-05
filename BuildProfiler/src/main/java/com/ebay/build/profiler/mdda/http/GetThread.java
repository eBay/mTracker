package com.ebay.build.profiler.mdda.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import com.ebay.build.profiler.mdda.bean.DArtifact;
import com.ebay.build.profiler.utils.FileUtils;
import com.ebay.build.profiler.utils.MD5Generator;

public class GetThread extends Thread {

	private final CloseableHttpClient httpClient;
	private final HttpContext context;
	private final HttpGet httpget;
	private final DArtifact artifact;
	private final boolean debug;
	private final String url;

	public GetThread(CloseableHttpClient httpClient, DArtifact a, boolean d) {
		this.httpClient = httpClient;
		this.context = HttpClientContext.create();
		this.artifact = a;
		this.debug = d;
		
		this.url = artifact.getQuick_url();
 	   
		if (debug) {
			System.out.println("[MDDA] Downloading : " + url);
		}
		
		this.httpget = new HttpGet(url);
	}

	@Override
	public void run() {
		try {
			CloseableHttpResponse response = httpClient.execute(httpget, context);
			
			File localfile = artifact.generateFilePath();
			FileOutputStream out = new FileOutputStream(localfile);
			
			try {
				HttpEntity entity = response.getEntity();
				
				IOUtils.copy(entity.getContent(), out);
				
				
				long size = localfile.length();
		         
	            if(!artifact.artifactOK(size)){
	            	localfile.delete();
	            	System.out.println("[MDDA] Downloading failed due to size not match,  : " + url);
	            } else if (debug) {
					System.out.println("[MDDA] Downloaded : " + url);
				}
	            
	            if (localfile.exists()) {
	            	File sha1File = new File(localfile.getParent(), localfile.getName() + ".sha1");
	            	if (!sha1File.exists()) {
	            		String jarMD5 = MD5Generator.createMessageDisgestChecksum(localfile, "SHA1");
	            		FileUtils.writeToFile(sha1File, jarMD5);
	            	}
	            	if (debug && sha1File.exists()) {
	            		System.out.println("[MDDA] SHA1 generated for artifacts: " + localfile);
	            	}
	            }
			} finally {
				response.close();
				out.close();
			}
		} catch (ClientProtocolException ex) {
			// Handle protocol errors
		} catch (IOException ex) {
			// Handle I/O errors
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

}
