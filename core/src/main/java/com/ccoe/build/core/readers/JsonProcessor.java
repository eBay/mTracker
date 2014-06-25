package com.ccoe.build.core.readers;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.ccoe.build.core.model.Session;

public class JsonProcessor implements ReaderProcessor {
	public Session process(String payload) {
		try {
			return fromJson(payload);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Session fromJson(String json) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(json, Session.class);
	}
}
