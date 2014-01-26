package com.ebay.build.service.portal;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * return dynamic url according to the params. 
 * @author wecai
 *
 */
@Path("portal")
public class RaptorPortalService {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getPortal(@Context UriInfo uriInfo,
			@QueryParam("raptorVersion") @DefaultValue("2.0.0") String raptor,
			@QueryParam("rideVersion") String ride,
			@QueryParam("jvmVersion") String jvm) {
		return "http://go/ride/welcome";
	}
}
