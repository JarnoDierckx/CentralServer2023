package com.fooditsolutions.indexservice.controller;

import com.fooditsolutions.indexservice.enums.Source;
import com.fooditsolutions.indexservice.model.Index;
import com.fooditsolutions.indexservice.model.IndexTemp;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IndexController {



    public static List<Index> readIndexFromCSV() throws IOException {
        List<Index> indexList = new ArrayList<>();
        InputStream is = IndexController.class.getClassLoader().getResourceAsStream("index.csv");

        if (is == null) {
            throw new IllegalArgumentException("file not found!");
        } else {

            String text = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));


            String[] a = text.split("\\n");

            String[] firsLine = a[0].split(";");
            for(int i=1; i<a.length; i++){
                String[] split = a[i].split(";");
                if(i==0) {

                }

                Index ind = new Index();
                ind.setMonth(split[1]+ " " +split[0]);
                ind.setYear(Integer.parseInt(split[0]));
                ind.setBase(firsLine[2]);
                ind.setCI(new BigDecimal(split[2].replace(",",".")).setScale(2));
                ind.setSource(Source.FILE);
                Index ind2 = new Index();
                ind2.setMonth(split[1]+ " " +split[0]);
                ind2.setYear(Integer.parseInt(split[0]));
                ind2.setBase(firsLine[3]);
                ind2.setCI(new BigDecimal(split[3].replace(",",".")).setScale(2));
                ind2.setSource(Source.FILE);
                indexList.add(ind);
                indexList.add(ind2);
            }
            //String s = "";
        }

        is.close();

        return  indexList;
    }

    public static List<Index> mergeIndex(List<Index> il1, List<Index> il2){
        List<Index> result = il1;
        HashMap<String, Index> indexHashMap1 = makeHashmap(il1);
        HashMap<String, Index> indexHashMap2 = makeHashmap(il2);

        for(Map.Entry<String, Index> ind : indexHashMap1.entrySet()){

            if(indexHashMap2.containsKey(ind.getKey())){
                indexHashMap2.remove(ind.getKey());
            }
        }

        for(Map.Entry<String, Index> ind : indexHashMap2.entrySet()){
            result.add(ind.getValue());
        }


        return result;
    }

    private static String getMD5(Index ind){
        return DigestUtils.md5Hex(ind.getBase()+ind.getMonth().toLowerCase()+ind.getYear()+ind.getCI());
    }

    private  static HashMap<String,Index> makeHashmap(List<Index> indexList){
        HashMap<String,Index> indexHashMap = new HashMap<>();
        for(Index ind : indexList) {
            indexHashMap.put(getMD5(ind), ind);
        }

        return  indexHashMap;
    }

    public static void updateLocFile(List<Index> indexList) throws IOException {
        HashMap<String,List<Index>> indexHashmap = new HashMap<>();

        for(Index ind : indexList) {
            if(ind.getSource()!=Source.FILE && (ind.getBase().contains("1996")||ind.getBase().contains("1988"))) {
                String yr = String.valueOf(ind.getYear());
                String key = ind.getYear() + ";" + ind.getMonth().replace(yr, "").trim();
                if (indexHashmap.containsKey(key)) {
                    List<Index> indexList1 = indexHashmap.get(key);
                    indexList1.add(ind);
                    indexHashmap.replace(key, indexList1);
                } else {
                    List<Index> indexList1 = new ArrayList<>();
                    indexList1.add(ind);
                    indexHashmap.put(key, indexList1);
                }
            }
        }

        FileWriter fw = new FileWriter(IndexController.class.getClassLoader().getResource("index.csv").getPath(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        for(Map.Entry<String, List<Index>> kv : indexHashmap.entrySet()){
                String s = kv.getKey()+";[1988];[1996]";
                for(Index ind : kv.getValue()) {
                    if(ind.getBase().contains("1988")) {
                        s = s.replace("[1988]",ind.getCI().toString());
                    } else if (ind.getBase().contains("1996")) {
                        s = s.replace("[1996]",ind.getCI().toString());
                    }

                }

                s=s.replace("[1988]","").replace("[1996]","");
                bw.write(s);
                bw.newLine();

        }

        bw.close();

    }
}
