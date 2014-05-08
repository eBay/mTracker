package com.ebay.build.service.errorfilter;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ebay.build.profiler.filter.FilterFactory;
import com.ebay.build.profiler.filter.MavenBuildFilterFactory;
import com.ebay.build.profiler.filter.model.Filter;

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
