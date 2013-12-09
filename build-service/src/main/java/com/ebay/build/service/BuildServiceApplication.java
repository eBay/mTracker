package com.ebay.build.service;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import org.glassfish.jersey.moxy.json.MoxyJsonConfiguration;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class BuildServiceApplication extends ResourceConfig {

    public BuildServiceApplication() {
        packages("com.ebay.build.service");
        //register(LoggingFilter.class);
        register(MoxyJsonFeature.class);
        register(MultiPartFeature.class);
        register(JsonMoxyConfigurationContextResolver.class);
      //  property(JsonGenerator.PRETTY_PRINTING, true);
        System.out.println("Build Service Application Constructed.");
    }
    
    @Provider
    final static class JsonMoxyConfigurationContextResolver implements ContextResolver<MoxyJsonConfiguration> {

        @Override
        public MoxyJsonConfiguration getContext(Class<?> objectType) {
            final MoxyJsonConfiguration configuration = new MoxyJsonConfiguration();

            Map<String, String> namespacePrefixMapper = new HashMap<String, String>(1);
            namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");

            configuration.setNamespacePrefixMapper(namespacePrefixMapper);
            configuration.setNamespaceSeparator(':');

            return configuration;
        }
    }
}

