/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.syncInitialize;

import com.asiainfo.utils.Constants;
import com.asiainfo.utils.HttpClient;
import com.google.common.collect.Maps;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Map;

/**
 * @author chenlong
 * Created on 2018/9/6
 */
public class Operate {

  /**
   * http send.
   * @param id
   */
  public static void sendData(String id, Map data){
    StringBuffer url = new StringBuffer();
    Map sendMap = Maps.newHashMap();
    String result = null;

    url.append(Constants.ES_URL)
            .append(Constants.ES_INDEX)
            .append("/")
            .append(Constants.ES_TYPE)
            .append("/")
            .append(data.get("ROUTE_ID"))
            .append("/_update");

    System.out.println("************** url = " + url +" ***************");
    sendMap.put("doc", data);
    sendMap.put("doc_as_upsert", true);
//    System.out.println("************** sendMap = " + sendMap +" ***************");

    try {
      result = HttpClient.post(url.toString(), sendMap);

      if (result != null){
        Map<String, Object> rsMap = new ObjectMapper().readValue(result, Map.class);
        if (rsMap.get("result") == null || rsMap.get("result").toString().equals("") ||
                !Constants.ELASTICSEARCH_API_OPERATE_SUCCESS.contains(rsMap.get("result").toString())) {
//          System.out.println("ElasticSearch Api Operate FAIL : {" + rsMap + " }");
        } else {
//          System.out.println("ElasticSearch Api Operate SUCCESS !");
        }
      }

    } catch (Exception e){
      e.printStackTrace();
    }
  }

  public static void sendData(String id, Map data, String esUrl){
    StringBuffer url = new StringBuffer();
    Map sendMap = Maps.newHashMap();
    String result = null;

    url.append(esUrl)
            .append(Constants.ES_INDEX)
            .append("/")
            .append(Constants.ES_TYPE)
            .append("/")
            .append(data.get("ROUTE_ID"))
            .append("/_update");

//    System.out.println("************** url = " + url +" ***************");
    sendMap.put("doc", data);
    sendMap.put("doc_as_upsert", true);
//    System.out.println("************** sendMap = " + sendMap +" ***************");

    try {
      result = HttpClient.post(url.toString(), sendMap);

      if (result != null){
        Map<String, Object> rsMap = new ObjectMapper().readValue(result, Map.class);
        if (rsMap.get("result") == null || rsMap.get("result").toString().equals("") ||
                !Constants.ELASTICSEARCH_API_OPERATE_SUCCESS.contains(rsMap.get("result").toString())) {
          System.out.println("ElasticSearch Api Operate FAIL : {" + rsMap + " }");
        } else {
//          System.out.println("ElasticSearch Api Operate SUCCESS !");
        }
      }

    } catch (Exception e){
      e.printStackTrace();
    }
  }





}
