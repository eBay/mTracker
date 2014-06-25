package com.ccoe.build.service.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.ccoe.build.core.model.Session;

public class PostSessionClient {

	public void queue(Session session) {
		Client client = ClientBuilder.newClient(new ClientConfig());
		client.register(JacksonFeature.class);

		//String target = "http://hostname/build-service/webapi/";
		String target = "http://D-SHC-00436998:7070/myapp";
		String path = "/queue/build/" + session.getAppName();
		Response response = client.target(target).path(path).request().post(Entity.entity(session, 
				MediaType.APPLICATION_JSON));
		
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			     + response.getStatus() + " Response: " + response.readEntity(String.class));
		} else {
			System.out.println(response.readEntity(String.class));
		}
	}
}
