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

package com.ccoe.build.service.errorfilter;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ccoe.build.core.filter.FilterFactory;
import com.ccoe.build.core.filter.MavenBuildFilterFactory;
import com.ccoe.build.core.filter.model.Filter;

@Path("")
public class ErrorCodeFilterService {
	
	List<ErrorCodeRes> filters = new ArrayList<ErrorCodeRes>();
	
	public ErrorCodeFilterService() {
		FilterFactory factory = new MavenBuildFilterFactory();
		
		for (Filter filter : factory.getFilters()) {
			ErrorCodeRes res = new ErrorCodeRes();
			res.setName(filter.getName());
			if (filter.getDescription() == null) {
				res.setDescription("N/A");
			} else {
				res.setDescription(filter.getDescription());
			}
			filters.add(res);
		}
	}

    @GET @Path("/errorfilter/{errorcode}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getErrorDescription(@PathParam("errorcode") String errorcode) {
    	for (ErrorCodeRes code : filters) {
    		if (code.getName().equals(errorcode)) {
    			return code.getDescription();
    		}
    	}
    	return "[ERROR] error code:" + errorcode + " not exists.";
    }
    
    @GET @Path("/errorfilters")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ErrorCodeRes> getAll() {
    	return this.filters;
    }
}
