package com.ebay.build.service.track;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ebay.build.profiler.model.Session;
import com.ebay.build.profiler.readers.LineProcessor;

public class QueueServiceTest {
	 private HttpServer server;
	    private WebTarget target;

	    @Before
	    public void setUp() throws Exception {
	        // start the server
	        server = Main.startServer();
	        // create the client
	        Client c = ClientBuilder.newClient();
	        
	        c.register(MoxyJsonFeature.class);
	        c.register(MultiPartFeature.class);

	        target = c.target(Main.BASE_URI);
	    }

	    @After
	    public void tearDown() throws Exception {
	        server.stop();
	    }

	    /**
	     * Test to see that the message "Got it!" is sent in the response.
	     * @throws IOException 
	     */
	    @Test
	    public void testGetIt() throws IOException {
	    	Session sessions = getSessions("exception.txt");
	    	
	    	Session session = new Session();
	    	session.setEnvironment("RIDE");
	    	
	    	session.getProjects().putAll(sessions.getProjects());
	    	session.setFullStackTrace(null);
	    	
	    	Response response = target.path("queue/build/job1").request().post(Entity.entity(session, MediaType.APPLICATION_JSON));
	    	assertTrue(response.readEntity(String.class).contains("build/job1 queued"));
	    }
	    
	    
	@Test
	public void testUploadArchive() throws IOException {
		File zip = new File(getClass().getClassLoader()
				.getResource("upload.zip").getFile());

		final FormDataMultiPart multipart = new FormDataMultiPart();
		FileDataBodyPart filePart = new FileDataBodyPart("udc_archive_"
				+ zip.getName(), zip);
		multipart.bodyPart(filePart);
		

		final Response response = target.path("queue/myApp").request()
				.post(Entity.entity(multipart, multipart.getMediaType()));

		String result=response.readEntity(String.class);
		assertTrue(result.contains("Files: [upload.zip] queued")
				|| result.contains("Files: [] queued"));
	}
	    
	    
	    @SuppressWarnings("resource")
		private Session getSessions(String fileName) throws IOException {
			BufferedReader br;
			br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource(fileName).getFile()));
			
			String sCurrentLine = null;
			StringBuffer sb = new StringBuffer();
			while ((sCurrentLine = br.readLine()) != null) {
				sb.append(sCurrentLine);
				sb.append("\n");
			}
			
			LineProcessor pro = new LineProcessor();
			return pro.process(sb.toString());
		}
}
