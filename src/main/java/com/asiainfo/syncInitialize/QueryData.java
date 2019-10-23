/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.syncInitialize;

import com.asiainfo.utils.DBTool;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @author chenlong
 * Created on 2018/9/6
 */
public class QueryData {
  private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public static void main(String[] args) {
//    int startIdx = 0;
//    while(true){
//      System.out.println("startIdx = " + startIdx + ", beginTime = " + format.format(System.currentTimeMillis()));
//      List<Map<String, Object>> list = DBTool.querySyncData("TF_MAIN", 1l);
//      System.out.println("startIdx = " + startIdx + ", endTime = " + format.format(System.currentTimeMillis()));
//      if (list.size() < 1) {
//        System.out.println("****************** list < 1 *******************");
//        break;
//      }
//      startIdx += 10000;
//    }
  }
}
