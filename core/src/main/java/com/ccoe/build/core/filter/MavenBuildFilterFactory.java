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

package com.ccoe.build.core.filter;

import java.net.MalformedURLException;
import java.net.URL;

public class MavenBuildFilterFactory extends FilterFactory {

	public final static String FILTER_LIST_FILE = "/default-filters.xml";
	public final static String FILTER_LIST_IN_GIT = BASE_FILTER_LIST_IN_GIT + FILTER_LIST_FILE;
	
	@Override
	public URL getRemoteFilterURL() {
		try {
			return new URL(FILTER_LIST_IN_GIT);
		} catch (MalformedURLException e) {
			System.err.println("[WARNING] can not load the filter list from remote " + FILTER_LIST_IN_GIT);
		}
		return null;
	}

	@Override
	public URL getLocalFilterURL() {
		 return this.getClass().getResource(FILTER_LIST_FILE);
	}
}
