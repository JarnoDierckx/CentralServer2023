package com.fooditsolutions.indexservice;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.indexservice.model.Facts;
import com.fooditsolutions.indexservice.model.IndexTemp;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/index")
public class IndexResource {
    @GET
    @Produces("application/json")
    public String getIndex(@QueryParam("Base") String base) throws IOException {

        String responseString = HttpController.httpGet("https://bestat.statbel.fgov.be/bestat/api/views/876acb9d-4eae-408e-93d9-88eae4ad1eaf/result/JSON");
        byte[] jsonData = responseString.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Facts facts  = mapper.readValue(jsonData, Facts.class);

        return responseString;
    }

    @GET
    @Produces("application/json")
    @Path("/Base")
    public String getIndexBase() {

        String responseString = HttpController.httpGet("https://bestat.statbel.fgov.be/bestat/api/views/876acb9d-4eae-408e-93d9-88eae4ad1eaf/result/JSON");


        return responseString;
    }
}
