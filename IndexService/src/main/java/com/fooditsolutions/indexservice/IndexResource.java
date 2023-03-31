package com.fooditsolutions.indexservice;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.indexservice.controller.IndexController;
import com.fooditsolutions.indexservice.enums.Source;
import com.fooditsolutions.indexservice.model.Facts;
import com.fooditsolutions.indexservice.model.Index;
import com.fooditsolutions.indexservice.model.IndexTemp;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Path("/index")
public class IndexResource {
    @GET
    @Produces("application/json")
    public List<Index> getIndex(@QueryParam("base") String base) throws IOException {

        List<Index> fileIndexList = IndexController.readIndexFromCSV();
        if(base != null) {
            if (!base.toLowerCase().trim().replace(" ", "").contains("=100")) {
                base += "=100";
            }
        }
        List<Index> indexList = new ArrayList<>();
        List<Index> result = new ArrayList<>();
        String responseString = HttpController.httpGet("https://bestat.statbel.fgov.be/bestat/api/views/1e33c9bc-20f4-4699-adff-f800da946ed9/result/JSON");
        byte[] jsonData = responseString.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Facts facts  = mapper.readValue(jsonData, Facts.class);
        for(IndexTemp it:facts.getIndexTempList()){

            /*if(base != null && base != ""){
                String bj = it.getBasisjaar();
                if(bj.trim().toLowerCase().replace(" ","").equals(base.trim().toLowerCase().replace(" ",""))){
                    Index ind = new Index();
                    ind.setYear(Integer.parseInt(it.getJaar()));
                    ind.setMonth(it.getMaand());
                    ind.setBase(it.getBasisjaar());
                    ind.setCI(it.getConsumptieprijsindex());
                    indexList.add(ind);
                }
            }else{*/
                Index ind = new Index();
                ind.setYear(Integer.parseInt(it.getJaar()));
                ind.setMonth(it.getMaand());
                ind.setBase(it.getBasisjaar());
                ind.setCI(it.getConsumptieprijsindex().setScale(2, BigDecimal.ROUND_HALF_EVEN));
                ind.setSource(Source.STABEL);
                indexList.add(ind);
            //}

        }
        List<Index> indexList2 = IndexController.mergeIndex(fileIndexList,indexList);
        IndexController.updateLocFile(indexList2);
        if(base != null && base != "") {
            for (Index indtmp : indexList2) {
                String bj = indtmp.getBase();
                if(bj.trim().toLowerCase().replace(" ","").equals(base.trim().toLowerCase().replace(" ",""))){
                    result.add(indtmp);
                }
            }
        }else{
            result = indexList2;
        }

        return result;
    }

    @GET
    @Produces("application/json")
    @Path("/base")
    public List<String> getIndexBase() throws IOException {
        List<String> base = new ArrayList<>();
        String responseString = HttpController.httpGet("https://bestat.statbel.fgov.be/bestat/api/views/1e33c9bc-20f4-4699-adff-f800da946ed9/result/JSON");
        byte[] jsonData = responseString.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Facts facts  = mapper.readValue(jsonData, Facts.class);
        for(IndexTemp it:facts.getIndexTempList()){
                if(!base.contains(it.getBasisjaar())) {
                    base.add(it.getBasisjaar());
                }


        }
        return base;


    }
}
