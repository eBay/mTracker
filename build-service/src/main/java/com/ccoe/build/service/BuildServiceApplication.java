/*
Copyright [2013-2014] eBay Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.ccoe.build.service;

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
        packages("com.ccoe.build.service");
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

