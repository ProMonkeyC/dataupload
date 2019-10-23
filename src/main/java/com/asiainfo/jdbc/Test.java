/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.jdbc;

import com.asiainfo.utils.DBTool;
import com.google.common.collect.Maps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chenlong
 * Created on 2018/7/7
 */
public class Test {

  private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public static void main1(String[] args) throws Exception{
    File file = new File("E:\\160705175253000978_160705175253000969.txt");

    String beginTime = format.format(System.currentTimeMillis());

    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

    ExecutorService service = Executors.newFixedThreadPool(30);
    final  Map<String, String> map = Maps.newConcurrentMap();
    Long actId = 0L;
    int count = 0;
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {


      final String str = new String(line);
      service.execute(new Runnable() {
        @Override
        public void run() {
          DBTool.singleInsert(str);
        }
      });


      count++;

      String endTime = format.format(System.currentTimeMillis());

      System.out.println("beginTime = " + beginTime);
      System.out.println("endTime = " + endTime);

    }

    reader.close();
  }
}
