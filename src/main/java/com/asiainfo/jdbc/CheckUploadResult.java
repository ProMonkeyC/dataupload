/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.jdbc;

import com.asiainfo.utils.DBTool;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chenlong
 * Created on 2018/7/7
 */
public class CheckUploadResult {

  private final static ExecutorService service = Executors.newFixedThreadPool(30);

  public static void main(String[] args) throws Exception {
    File file = new File("/Users/chenlong/Desktop/1000.txt");

    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

    String actId = "";
    int lineNum = 1;
    List<Map<String, Object>> list = Lists.newArrayList();

   final Map<String, String> map1 = Maps.newConcurrentMap();
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {

      if (line.equals("")) {
        continue;
      }

      actId = line.trim().toString();


//      map1.put("actId", actId);

//      service.execute(new Runnable() {
//        @Override
//        public void run() {
//          DBTool.del(map1.get("actId"));
//        }
//      });

      Map<String, Object> map = Maps.newHashMap();

      map.put("actId", actId);
      map.putAll(DBTool.querySum(actId));

      list.add(map);

      if (list.size() % 10 == 0){
        writeFile(list);
        list.clear();
      }


    }

    reader.close();

  }

  private static void writeFile(List<Map<String, Object>> list) throws Exception {
    String fileName = "result";
    String path = "/Users/chenlong/Desktop/";
    StringBuffer pathBuffer = new StringBuffer().append(path);
    pathBuffer.append(fileName);
    pathBuffer.append(".txt");
    FileWriter fileWriter = new FileWriter(pathBuffer.toString(), true);
    BufferedWriter bf = new BufferedWriter(fileWriter);


    for (int i = 0; i < list.size(); i++){

      Map dataMap = list.get(i);

      String eachData = "ACCT_ID : " + dataMap.get("actId") + " , TOTAL : " + dataMap.get("SUM") + " , MAX_DEVICE_ID :"
              + dataMap.get("MAX") + " , MIN_DEVICE_ID : " + dataMap.get("MIN");

      bf.write(eachData);
      bf.newLine();
      bf.flush();
      dataMap.clear();
    }

    bf.close();
  }

}
