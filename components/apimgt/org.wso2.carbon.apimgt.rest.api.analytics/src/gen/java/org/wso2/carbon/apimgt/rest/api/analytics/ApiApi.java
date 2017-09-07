package org.wso2.carbon.apimgt.rest.api.analytics;


import io.swagger.annotations.ApiParam;

import org.wso2.carbon.apimgt.rest.api.analytics.dto.APICountListDTO;
import org.wso2.carbon.apimgt.rest.api.analytics.dto.APIInfoListDTO;
import org.wso2.carbon.apimgt.rest.api.analytics.dto.APISubscriptionCountListDTO;
import org.wso2.carbon.apimgt.rest.api.analytics.factories.ApiApiServiceFactory;

import org.wso2.msf4j.Microservice;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.formparam.FormDataParam;
import org.osgi.service.component.annotations.Component;

import java.io.InputStream;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Component(
    name = "org.wso2.carbon.apimgt.rest.api.analytics.ApiApi",
    service = Microservice.class,
    immediate = true
)
@Path("/api/am/analytics/v1.[\\d]+/api")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@ApplicationPath("/api")
@io.swagger.annotations.Api(description = "the api API")
public class ApiApi implements Microservice  {
   private final ApiApiService delegate = ApiApiServiceFactory.getApiApi();

    @GET
    @Path("/api-info")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Retrieve APIs created over time details ", notes = "Get application created over time details from summarized data. ", response = APIInfoListDTO.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "OAuth2Security", scopes = {
            @io.swagger.annotations.AuthorizationScope(scope = "apim:api_graphs", description = "View Graphs Releated to APIs")
        })
    }, tags={  })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. Requested APIs created over time information is returned ", response = APIInfoListDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 406, message = "Not Acceptable. The requested media type is not supported ", response = APIInfoListDTO.class) })
    public Response apiApiInfoGet(@ApiParam(value = "Defines the starting timestamp of the interval ",required=true) @QueryParam("startTime") String startTime
,@ApiParam(value = "Defines the ending timestamp of the interval ",required=true) @QueryParam("endTime") String endTime
,@ApiParam(value = "application creator name. In case of any creator, the value shold be equal to 'all' ",required=true) @QueryParam("createdBy") String createdBy
, @Context Request request)
    throws NotFoundException {
        return delegate.apiApiInfoGet(startTime,endTime,createdBy, request);
    }
    @GET
    @Path("/count-over-time")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Retrieve APIs created over time details ", notes = "Get application created over time details from summarized data. ", response = APICountListDTO.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "OAuth2Security", scopes = {
            @io.swagger.annotations.AuthorizationScope(scope = "apim:api_graphs", description = "View Graphs Releated to APIs")
        })
    }, tags={  })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. Requested APIs created over time information is returned ", response = APICountListDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 406, message = "Not Acceptable. The requested media type is not supported ", response = APICountListDTO.class) })
    public Response apiCountOverTimeGet(@ApiParam(value = "Defines the starting timestamp of the interval ",required=true) @QueryParam("startTime") String startTime
,@ApiParam(value = "Defines the ending timestamp of the interval ",required=true) @QueryParam("endTime") String endTime
,@ApiParam(value = "application creator name. In case of any creator, the value shold be equal to 'all' ",required=true) @QueryParam("createdBy") String createdBy
, @Context Request request)
    throws NotFoundException {
        return delegate.apiCountOverTimeGet(startTime,endTime,createdBy, request);
    }
    @GET
    @Path("/subscriber-count-by-api")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Retrieve Subscriber count by APIs ", notes = "Get subscriber count by APIs from summarized data. ", response = APISubscriptionCountListDTO.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "OAuth2Security", scopes = {
            @io.swagger.annotations.AuthorizationScope(scope = "apim:api_graphs", description = "View Graphs Releated to APIs")
        })
    }, tags={  })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. Requested subscriber count by API information is returned ", response = APISubscriptionCountListDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 406, message = "Not Acceptable. The requested media type is not supported ", response = APISubscriptionCountListDTO.class) })
    public Response apiSubscriberCountByApiGet(@ApiParam(value = "application creator name. In case of any creator, the value shold be equal to 'all' ",required=true) @QueryParam("createdBy") String createdBy
, @Context Request request)
    throws NotFoundException {
        return delegate.apiSubscriberCountByApiGet(createdBy, request);
    }
}
