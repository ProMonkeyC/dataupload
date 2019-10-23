/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.uploadFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author chenlong
 * Created on 2018/9/11
 */
public class EsUploadFile {


  /**
   * 模板:IMSI/ICCID/PIN1/PIN2/PUK1/PUK2/KI/ACC/ADM/MSISDN
   * @param args
   */
  public static void main(String[] args) {
    startToWriteFile(
            "3329020520", //ICCID前10位
            "1000000000", //填写10位数字
            "332902052000000", //填写15位数字
            "33290205200", //填写11位数字，0表示生成的文件不含MSISDN字段
            "/Users/chenlong/Desktop/", //文件存放路径
            "Mysql20000000", //文件名
            "20000000" //生成的数据量
    );
  }

  /**
   * write file.
   * @param dataMap
   * @throws Exception
   */
  private static void writeFile(Map dataMap) throws Exception {
    String fileName = dataMap.get("fileName").toString();
    String path = dataMap.get("path").toString();
    StringBuffer pathBuffer = new StringBuffer().append(path);
    pathBuffer.append(fileName);
    pathBuffer.append(".json");
    FileWriter fileWriter = new FileWriter(pathBuffer.toString());
    BufferedWriter bf = new BufferedWriter(fileWriter);
    long iccidHead = Long.valueOf(dataMap.get("iccidHead").toString());
    long iccid = Long.valueOf(dataMap.get("iccid").toString());
    long imsi = Long.valueOf(dataMap.get("imsi").toString());
    long msisdn = Long.valueOf(dataMap.get("msisdn").toString());
    long pin = Long.valueOf(dataMap.get("pin").toString());

    long acc = Long.valueOf(dataMap.get("acc").toString());
    long adm = Long.valueOf(dataMap.get("adm").toString());
    long puk = Long.valueOf(dataMap.get("puk").toString());

    int times = Integer.valueOf(dataMap.get("times").toString());
    for (int i = 0; i < times; i++) {
      imsi = getLastNumber(imsi + i, 15); //IMSI 15
      iccid = getLastNumber(iccid + i, 10); //ICCID 20
      if (0 != msisdn) {
        msisdn = getLastNumber(msisdn + 1, 15); //MSISDN 11,13,15
      }

      String eachData = getSimUploadData(iccidHead,iccid, imsi, msisdn, pin, puk, acc, adm);
      bf.write("{\"index\":{\"_id\":\""+ iccid +"\"}}");
      bf.newLine();
      bf.flush();
      bf.write(eachData);
      bf.newLine();
      bf.flush();
    }
    bf.close();
  }

  /**
   * 生成一行.
   * IMSI/ICCID/PIN1/PIN2/PUK1/PUK2/KI/ACC/ADM/MSISDN
   *
   * @param iccid
   * @param imsi
   * @param msisdn
   * @param pin
   * @param puk
   * @param acc
   * @param adm
   * @return
   */
  private static String getSimUploadData(long iccidHead, long iccid, long imsi, long msisdn, long pin, long puk, long acc, long adm) {
    String line = "";
    StringBuffer lineBuffer = new StringBuffer();
    lineBuffer.append("{");
    lineBuffer.append("\"IMSI\":\"").append(imsi).append("\",");
    lineBuffer.append("\"ICCID\":\"").append(iccidHead).append(iccid).append("\",");
    lineBuffer.append("\"PIN1\":").append(pin).append(",");
    lineBuffer.append("\"PIN2\":").append(pin).append(",");
    lineBuffer.append("\"PUK1\":").append(puk).append(",");
    lineBuffer.append("\"PUK2\":").append(puk).append(",");

    //KI

    DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    String nowDate = format.format(new Date()); //14
    StringBuffer kiBuffer = new StringBuffer();
    long currentTime = System.currentTimeMillis() % 10000;
    if (currentTime < 10000) {
      currentTime = 10000 + currentTime;
    }
    String nowMillis = String.valueOf(currentTime).substring(0, 4); //4
    kiBuffer.append(nowDate);
    kiBuffer.append(nowMillis);
    for (int i = 0; i < 14; i++) {
      kiBuffer.append(Integer.toHexString(new Random().nextInt(16)));
    }
    String ki = kiBuffer.toString();

    lineBuffer.append("\"KI\":\"").append(ki).append("\",");

    lineBuffer.append("\"ACC\":").append(acc).append(",");
    lineBuffer.append("\"ADM\":").append(adm); //ACC ADM
    if (0 != msisdn) {
      lineBuffer.append(",").append("\"MSISDN\":").append(msisdn);
    }
    lineBuffer.append("}");
    line = lineBuffer.toString();
    System.out.println("line : " + line);
    return line;
  }

  /**
   * 获取len位整数.
   * @param target
   * @param len
   * @return
   */
  private static long getLastNumber(long target, int len) {

    StringBuffer indexBuffer = new StringBuffer().append("1");
    long index = 0;
    for (int i = 0; i < len; i++) {
      indexBuffer.append("0");
    }
    index = Long.valueOf(indexBuffer.toString());
    target = target % index;
    return target;
  }

  /**
   * @param iccid
   * @param imsi
   * @param msisdn
   * @param path
   * @param fileName
   * @param nums
   */
  private static void startToWriteFile(String iccidHead, String iccid, String imsi, String msisdn, String path, String fileName, String nums) {
    long iccids = Long.valueOf(iccid); //10位数字
    long imsis = Long.valueOf(imsi); //15位数字
    long msisdns = Long.valueOf(msisdn); //11位数字  0表示不带MSISDN
//    long msisdn = Long.valueOf("0"); //11位数字  0表示不带MSISDN
    long pin = 12345;
    long puk = 12345;
    long acc = 54321;
    long adm = 54321;
    int dataNums = Integer.valueOf(nums); //上传数量
    try {
      System.out.println("Start write ... ");
      Map dataMap = new HashMap();
      dataMap.put("iccid", iccids);
      dataMap.put("iccidHead", iccidHead);
      dataMap.put("imsi", imsis);
      dataMap.put("msisdn", msisdns);
      dataMap.put("pin", pin);
      dataMap.put("puk", puk);
      dataMap.put("acc", acc);
      dataMap.put("adm", adm);
      dataMap.put("fileName", fileName);
      dataMap.put("times", dataNums);
      dataMap.put("path", path);
      writeFile(dataMap);
      System.out.println("End write ... ");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }



}
