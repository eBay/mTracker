package com.ebay.build.profiler.readers;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.ebay.build.profiler.model.Session;

public class JsonProcessor implements ReaderProcessor {
	public Session process(String payload) {
		try {
			return fromJson(payload);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Session fromJson(String json) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(json, Session.class);
	}
}
