/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.jdbc;

import com.asiainfo.utils.DBTool;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author chenlong
 * Created on 2018/7/6
 */
public class InsertTask implements Runnable {

  //ICCID前10位
  private Long iccidHead = 2018070000L;
  //ICCID递增开始
  private Long iccid;
  //填写15位数字
  private Long imsi;
  //填写11位数字
  private Long msisdn;
  //生成的数据量
  private Long nums = 9000L;
  //设备ID
  private Long devId;

  private Long acctId;

  private Long rateId = 18524412L;
  private Long commplanId = 18524424L;
  private Long rateVersion = 18524413L;
  private String serviceProviderCode = "1";
  private String provinceCode = "-1";

  private final static DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

  private final static String endDate = "2099-12-31 23:59:59";

  private static String driver = "com.mysql.jdbc.Driver";

  private static ComboPooledDataSource dataSource;

  static {
    try {
      Class.forName(driver);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    dataSource = new ComboPooledDataSource();
  }

  public InsertTask(Long iccid, Long imsi, Long msisdn, Long devId, long acctId) {
    this.iccid = iccid;
    this.imsi = imsi;
    this.msisdn = msisdn;
    this.devId = devId;
    this.acctId = acctId;
  }

  @Override
  public void run() {

    try(Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {

    long pin = 12345;
    long puk = 12345;
    long acc = 54321;
    long adm = 54321;

    Map statusMap = Maps.newHashMap();
    Map commplanMap = Maps.newHashMap();

    Map deviceMap = getDeviceDefaultParam();
    Map simMap = getSimDefaultParam();
    Map usageMap = getUsageDefaultParam();
    Map resMap = getResDefaultParam();

    String now = format.format(System.currentTimeMillis());

    commplanMap.put("STATE_CODE", 0);
    commplanMap.put("END_DATE", endDate);
    commplanMap.put("START_DATE", now);

    statusMap.put("STATE_CODE", 0);
    statusMap.put("END_DATE", endDate);
    statusMap.put("START_DATE", now);

    resMap.put("END_DATE", endDate);
    resMap.put("START_DATE", now);

    deviceMap.put("CREATE_TIME", now);
    deviceMap.put("TRANS_ACCOUNT_TIME", now);
    simMap.put("CREATE_TIME", now);
    simMap.put("UPDATE_TIME", now);

    List<String> sqlList = Lists.newArrayList();
//    connection.setAutoCommit(false);
    for (int i = 1; i <= nums; i++){

      String ki = getKI();

      imsi +=1;
      iccid += 1;
      msisdn += 1;

      Map<String, Object> devM = Maps.newHashMap();
      Map<String, Object> simM = Maps.newHashMap();
      Map<String, Object> usageM = Maps.newHashMap();
      Map<String, Object> resMIccid = Maps.newHashMap();
      Map<String, Object> resMMsisdn = Maps.newHashMap();
      Map<String, Object> stateM = Maps.newHashMap();
      Map<String, Object> commplanM = Maps.newHashMap();

      devM.putAll(deviceMap);
      devM.put("DEVICE_ID", devId);
      devM.put("ICCID", ("" + iccidHead + iccid).trim());
      devM.put("MSISDN", msisdn);
      devM.put("ACCT_ID", acctId);
      devM.put("RATE_PLAN_ID", rateId);
      devM.put("PLAN_VERSION_ID", rateVersion);
      devM.put("COMM_PLAN_ID", commplanId);
      devM.put("REAL_NAME_STATUS", 0);
      devM.put("SERVICE_PROVIDER_CODE", serviceProviderCode);
      devM.put("PROVINCE_CODE", provinceCode);
      devM.put("DEPART_CODE", -1);
      devM.put("TRANS_ACCOUNT_TIME", now);
      devM.put("CREATE_TIME", now);
      devM.put("PARTITION_ID", devId % 10000);
      devM.put("UPDATE_TIME", now);

      sqlList.add(getSql(devM, "TF_F_DEVICE"));

      simM.putAll(simMap);
      simM.put("ICCID", ("" + iccidHead + iccid).trim());
      simM.put("IMSI", imsi);
      simM.put("KI", ki);
      simM.put("ACC", acc);
      simM.put("ADM", adm);
      simM.put("PIN1", pin);
      simM.put("PIN2", pin);
      simM.put("PUK1", puk);
      simM.put("PUK2", puk);
      simM.put("UPDATE_USER_NAME", "admin");
      simM.put("DELEGATER_USER_NAME", null);
      simM.put("CREATE_TIME", now);
      simM.put("ACCT_ID", acctId);
      simM.put("HLR", null);
      simM.put("MIGRATED_SIM", 0);
      simM.put("CONFIG_ID", 2);
      simM.put("FILE_TEMPLETE", 2);
      simM.put("FILE_ID", 18523530);
      simM.put("DEVICE_ID", devId);

      sqlList.add(getSql(simM, "TF_R_SIM_INFO"));

      usageM.putAll(usageMap);
      usageM.put("DEVICE_ID", devId);
      usageM.put("ACCT_ID", acctId);
      usageM.put("PARTITION_ID", devId % 10000);
      usageM.put("UPDATE_TIME", now);

      sqlList.add(getSql(usageM, "TF_F_DEVICE_USAGE"));

      resMIccid.putAll(resMap);
      resMIccid.put("DEVICE_ID", devId);
      resMIccid.put("RES_TYPE_CODE", 0); //0-iccid  1-msisdn
      resMIccid.put("RES_CODE", ("" + iccidHead + iccid).trim());
      resMIccid.put("RES_INFO1", imsi);
      resMIccid.put("ACCT_ID", acctId);
      resMIccid.put("PARTITION_ID", devId % 10000);

      sqlList.add(getSql(resMIccid, "TF_F_DEVICE_RES"));

      resMMsisdn.putAll(resMap);
      resMMsisdn.put("DEVICE_ID", devId);
      resMMsisdn.put("RES_TYPE_CODE", 1); //0-iccid  1-msisdn
      resMMsisdn.put("RES_CODE", msisdn);
      resMMsisdn.put("RES_INFO1", imsi);
      resMMsisdn.put("ACCT_ID", acctId);
      resMMsisdn.put("PARTITION_ID", devId % 10000);

      sqlList.add(getSql(resMMsisdn, "TF_F_DEVICE_RES"));

      stateM.putAll(statusMap);
      stateM.put("DEVICE_ID", devId);
      stateM.put("ACCT_ID", acctId);
      stateM.put("PARTITION_ID", devId % 10000);

      sqlList.add(getSql(stateM, "TF_F_DEVICE_STATE"));

      commplanM.putAll(commplanMap);
      commplanM.put("DEVICE_ID", devId);
      commplanM.put("COMM_PLAN_ID", commplanId);
      commplanM.put("ACCT_ID", acctId);
      commplanM.put("PARTITION_ID", devId % 10000);
      String planSql = getSql(commplanM, "TF_F_DEVICE_COMM_PLAN");
      sqlList.add(planSql);


      if (i % 1000 == 0){ //1000次提交一次

//        int ii = 0;
//        for (String sql: sqlList) {
//          ii++;
//          statement.addBatch(sql);
//          if (ii >= 99) {
//            statement.executeBatch();
//            statement.clearBatch();
//            ii = 0;
//          }
//        }
        DBTool.batchInsert(sqlList);
//        statement.executeBatch();
//        connection.commit();
        System.out.println("Thread_name : " + Thread.currentThread().getName() + ", 完成第"+ (i/1000) +"次提交数据...");
//          writeFile(sqlList);
          sqlList.clear();
      }

      devId++;

    }


    } catch (Exception e) {
      e.printStackTrace();
    }


  }

  private static void writeFile(List<String> list) throws Exception {
    String fileName = "160705175253000002_160705175253000001";
    String path = "/Users/chenlong/Desktop/";
    StringBuffer pathBuffer = new StringBuffer().append(path);
    pathBuffer.append(fileName);
    pathBuffer.append(".txt");
    FileWriter fileWriter = new FileWriter(pathBuffer.toString(), true);
    BufferedWriter bf = new BufferedWriter(fileWriter);


    long times = list.size();
    for (int i = 0; i < times; i++) {

      String eachData = list.get(i);
      bf.write(eachData);
      bf.newLine();
      bf.flush();
    }
    bf.close();
  }


  /**
   * 获取KI.
   * @return
   */
  private static String getKI(){
    StringBuffer kiBuffer = new StringBuffer();
    String nowDate = format.format(new Date()); //14
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

    return ki;
  }

  /**
   * 获取设备默认字段.
   * @return
   */
  private static Map getDeviceDefaultParam(){
    String defaultStr = "SECURE_NAME:1,SECURE_PWD:1,COPY_RULE_SSID:1,COPY_RULE_PWD:1,STATUS:0," +
            "OVERAGE_LIMIT_REACHED:197010,RECEIVING_CONFIRM_DATE:NULL,OPER_ACCTID:NULL,SUB_ACCT_ID:NULL," +
            "REPLACED_ICCID:NULL,REPLACEMENT_ICCID:NULL,IMEI:NULL,ACTIVATION_DATE:NULL,SHIPPED_DATE:NULL," +
            "OVERAGE_LIMIT_OVERRIDE:0,USER_DEVICE_ID:NULL,SUSPENDED:NULL,SIM_BARRED:0,MODEM_ID:NULL,SECURE_SIM_ID:NULL," +
            "IS_LOCKED:0,FIXED_IP_ADDRESS:NULL,UPDATE_USER_NAME:NULL,DELEGATER_USER_NAME:NULL,CUSTOMER_CUSTOM1:NULL," +
            "CUSTOMER_CUSTOM2:NULL,CUSTOMER_CUSTOM3:NULL,CUSTOMER_CUSTOM4:NULL,CUSTOMER_CUSTOM5:NULL," +
            "ACCOUNT_CUSTOM1:NULL,ACCOUNT_CUSTOM2:NULL,ACCOUNT_CUSTOM3:NULL,ACCOUNT_CUSTOM4:NULL,ACCOUNT_CUSTOM5:NULL," +
            "ACCOUNT_CUSTOM6:NULL,ACCOUNT_CUSTOM7:NULL,ACCOUNT_CUSTOM8:NULL,ACCOUNT_CUSTOM9:NULL,ACCOUNT_CUSTOM10:NULL," +
            "PROVINCE_CUSTOM1:NULL,PROVINCE_CUSTOM2:NULL,PROVINCE_CUSTOM3:NULL,PROVINCE_CUSTOM4:NULL," +
            "PROVINCE_CUSTOM5:NULL,PROVIDER_CUSTOM1:NULL,PROVIDER_CUSTOM2:NULL,PROVIDER_CUSTOM3:NULL," +
            "PROVIDER_CUSTOM4:NULL,PROVIDER_CUSTOM5:NULL,RSRV_STR1:NULL,RSRV_STR2:NULL,RSRV_STR3:NULL,RSRV_STR4:NULL," +
            "RSRV_STR5:NULL,UPDATE_TIME:NULL";
    Map map = getMap(defaultStr);
    return map;
  }

  /**
   * SIM默认字段.
   * @return
   */
  private static Map getSimDefaultParam(){

    String defaultStr = "PAY_STATUS:0,SYS:1,TECHNOLOGY_TYPE:0,ORDER_ID:NULL,IMSI2:NULL,IMSI3:NULL,IMSI4:NULL," +
            "ESIM_ID:NULL,POLICY:NULL,SIM_NOTES:NULL,SALE_DATE:NULL,SIM_TYPE:0,ADM1:NULL,ADM2:NULL,ADM3:NULL," +
            "ADM4:NULL";

    Map map = getMap(defaultStr);
    return map;

  }

  /**
   * 用量表默认字段.
   * @return
   */
  private static Map getUsageDefaultParam(){

    String defaultStr = "CYCLE_ID:201807,IS_ONLINE:0,DATA_USAGE:0,VOICE_USAGE:0,SMS_USAGE:0,NETWORK_TYPE:NULL";

    Map map = getMap(defaultStr);
    return map;

  }

  /**
   * 获取sql.
   * @param param
   * @param tabName
   * @return
   */
  private static String getSql(Map<String, Object> param, String tabName){

    StringBuffer buffer = new StringBuffer();
    buffer.append("INSERT INTO ")
            .append(tabName)
            .append(" (");

    StringBuffer valueBuffer = new StringBuffer();

    for (String key : param.keySet()){
      buffer.append(" ")
              .append(key)
              .append(",");
      if (param.get(key) == null || param.get(key).equals("null")){
        valueBuffer.append(param.get(key));
      } else {
        valueBuffer.append("'" + param.get(key) + "'");
      }

      valueBuffer.append(" ,");
    }

    String prefix = buffer.substring(0, buffer.toString().length()-1);
    String valueStr = valueBuffer.substring(0, valueBuffer.toString().length()-1);

    String sql = prefix + ") VALUES (" + valueStr + ");";

    System.out.println(sql);

    return sql;

  }

  /**
   * 资源表默认字段.
   * @return
   */
  private static Map getResDefaultParam(){

    String defaultStr = "RES_INFO2:NULL,RES_INFO3:NULL,RES_INFO4:NULL,RES_INFO5:NULL,RES_INFO6:NULL,RES_INFO7:NULL,RES_INFO8:NULL";

    Map map = getMap(defaultStr);
    return map;

  }


  private static Map getMap(String defaultStr){
    String[] arr = defaultStr.split(",");
    Map map = Maps.newHashMap();
    for(int i = 0; i < arr.length; i++){
      String str = arr[i];
      String[] temp = str.split(":");
      map.put(temp[0].trim(), temp[1].trim().equals("NULL") ? null : temp[1].trim());
    }
    return map;
  }

}
