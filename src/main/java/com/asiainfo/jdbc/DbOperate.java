/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.jdbc;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chenlong
 * Created on 2018/7/6
 */
public class DbOperate {

  private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private final static ExecutorService service = Executors.newFixedThreadPool(1600);

  public static void main(String[] args) throws Exception{
    String beginTime = format.format(System.currentTimeMillis());

    File file = new File("/iot/server/createdata/account.txt");

    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

    Long actId = 0L;
    int lineNum = 0;
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {

      if (line.equals("")) {
        continue;
      }

      actId = Long.valueOf(line.toString());

      Long iccid = 1068000000L + ( 10000L * lineNum);
      Long imsi = 666600068000000L + ( 10000L * lineNum);
      Long msisdn = 66068000000L + ( 10000L * lineNum);
      Long deviceId = 666600000068000000L + ( 10000L * lineNum);

      service.execute(new InsertTask(iccid, imsi, msisdn, deviceId, actId));

//      new Thread(new InsertTask(iccid, imsi, msisdn, deviceId, actId)).start();

      lineNum++;
    }

    String endTime = format.format(System.currentTimeMillis());

    System.out.println("********** beginTime : " + beginTime + " **********");
    System.out.println("********** endTime : " + endTime + " **********");
  }

  public static void main11(String[] args) {

    new Thread(new InsertTask(1000040000L, 666600000040000L, 66000040000L, 666600000000040000L, 160705175253000998L)).start();

  }




}
