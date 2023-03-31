package com.fooditsolutions.CentralServer2023API;

import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/module")
public class ModuleResource {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public String getModuleIds() {
        return HttpController.httpGet(PropertiesController.getProperty().getBase_url_moduleservice()+"/moduleid");
    }

}
