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

package com.ccoe.build.service.config;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;


public class BuildServiceConfig {

	public BuildServiceConfigBean get(String configName) {
		Client client = ClientBuilder.newClient(new ClientConfig());
		client.register(JacksonFeature.class);

		String target = "http://${HOSTNAME}/build-service/webapi/";
		String path = "/config/" + configName;
		BuildServiceConfigBean configBean = client.target(target).path(path).request().get(BuildServiceConfigBean.class);
		
		return configBean;
	}
	
	public static void main(String[] args) {
		//System.out.println(new BuildServiceConfig().get("${CONFIG.NAME}").isGlobalSwitch());
	}
}
