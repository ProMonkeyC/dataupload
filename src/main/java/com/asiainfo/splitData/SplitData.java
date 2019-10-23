/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.splitData;

import com.asiainfo.utils.Constants;
import com.google.common.collect.Maps;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenlong
 * Created on 2018/9/12
 */
public class SplitData {

  public static void main(String[] args) throws Exception{

    String filePath = args[0];
    String path = args[1];

    Map dataMap = new HashMap();
//    dataMap.put("filePath", "/iot/dataDump/mysqlData.txt");
    dataMap.put("filePath", filePath);
    dataMap.put("fileName", "mysqlData");
//    dataMap.put("path", "/iot/dataDump/");
    dataMap.put("path", path + "/");
    splitDataBegin(dataMap);

  }

  private static void splitDataBegin(Map dataMap) throws Exception{
    String[] arr = Constants.SPLIT_DATA_FIELDS.split(",");
    List<String> fields = Arrays.asList(arr);

    File file = new File(dataMap.get("filePath").toString());
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

    String fileName = dataMap.get("fileName").toString();
    String path = dataMap.get("path").toString();
    StringBuffer pathBuffer = new StringBuffer().append(path);
    pathBuffer.append(fileName);
    pathBuffer.append(".json");
    FileWriter fileWriter = new FileWriter(pathBuffer.toString());
    BufferedWriter bf = new BufferedWriter(fileWriter);

    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      if (line.equals("")) {
        continue;
      }
      Map<String, String> map = handleFields(line, fields);
      bf.write(map.get("index"));
      bf.newLine();
      bf.flush();
      bf.write(map.get("data"));
      bf.newLine();
      bf.flush();

    }

    reader.close();
    bf.close();

  }

  private static Map<String, String> handleFields(String line, List fields){
    String[] fs = line.split(",");
    List fsL = Arrays.asList(fs);
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("{");
    for (int i = 0; i < fields.size(); i++){
      if (i > 0){
        stringBuffer.append(",\"");
      } else {
        stringBuffer.append("\"");
      }
      stringBuffer.append(fields.get(i)).append("\":\"");
      if (fsL.get(i).toString().equals("\\N")){
        stringBuffer.append("\"");
      } else {
        stringBuffer.append(fsL.get(i)).append("\"");
      }
    }
    stringBuffer.append("}");
    Map<String, String> map = Maps.newHashMap();
    map.put("data", stringBuffer.toString());
    map.put("index", "{\"index\":{\"_id\":\""+ fsL.get(0) +"\"}}");
    return map;
  }



}
