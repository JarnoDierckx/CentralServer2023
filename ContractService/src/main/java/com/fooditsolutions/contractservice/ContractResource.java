package com.fooditsolutions.contractservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/contract")
public class ContractResource {
    @GET
    @Produces("application/json")
    public String hello() {
        return "Hello, World!";
    }

    @GET
    @Produces("application/json")
    @Path("/{contractId}")
    public String hello(@PathParam("contractId") int contractId) {
        return "Hello, World!";
    }
}
