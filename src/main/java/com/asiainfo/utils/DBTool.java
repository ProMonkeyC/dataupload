/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.utils;

import com.asiainfo.bpc.service.SqlCodeService;
import com.asiainfo.bpc.utils.ToolFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chenlong
 * Created on 2018/7/6
 */
public class DBTool {

  private static String driver = "com.mysql.jdbc.Driver";

  private static ComboPooledDataSource dataSource;

  private static ComboPooledDataSource cenDataSource;

  private final static ExecutorService service = Executors.newFixedThreadPool(100);

  private static SqlCodeService sqlCodeService;

  static {
    try {
      Class.forName(driver);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    dataSource = new ComboPooledDataSource();
    try {
      cenDataSource = new ComboPooledDataSource();
      cenDataSource.setDriverClass("com.mysql.jdbc.Driver");
      cenDataSource.setJdbcUrl("jdbc:mysql://172.18.50.107:8066/cen_dev?useUnicode=true&characterEncoding=UTF-8&useSSL=false");
      cenDataSource.setUser("crm");
      cenDataSource.setPassword("crm");
    } catch (PropertyVetoException e){
      e.printStackTrace();
    }

//    ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring-bpchain.xml");

//    sqlCodeService = (SqlCodeService) context.getBean("info");


  }

  public static void batchExec (List<String> sqlList) {
    int size = sqlList.size();

  }

  public static void batchInsertMAIN(List<String> list){
    try (Connection connection = dataSource.getConnection()){

      connection.setAutoCommit(false);

      PreparedStatement psts = connection.prepareStatement(Constants.TAB_MAIN_SQL);
      int count = 0;
      try {
        for (String ele : list) {
          String[] arr = ele.toString().split(",");

          count++;
          psts.setLong(1, Long.valueOf(arr[0])); //ID
          psts.setLong(2, Long.valueOf(arr[1])); //ROUTE_ID
          psts.setString(3, arr[2]); //FIELD1
          psts.setLong(4, Long.valueOf(arr[3])); //FIELD2
          psts.setLong(5, Long.valueOf(arr[4])); //FIELD3
          psts.setString(6, arr[5]); //FIELD4
          psts.setString(7, arr[6]); //FIELD8
          psts.setLong(8, Long.valueOf(arr[7])); //FIELD14
          psts.setLong(9, Long.valueOf(arr[8])); //FIELD15
          psts.setString(10, arr[9]);//FIELD51
          psts.setString(11, arr[10]);//FIELD5
          psts.setLong(12, Long.valueOf(arr[11])); //FIELD6
          psts.setString(13, arr[12]);//FIELD7
          psts.addBatch();

        }

        psts.executeBatch();
        connection.commit();

      } catch (SQLException e){
        e.printStackTrace();
      }



    } catch (Exception e){
      e.printStackTrace();
    }
  }

  public static void batchInsertROUTE(List<String> list){
    try (Connection connection = dataSource.getConnection()){

      connection.setAutoCommit(false);

      PreparedStatement psts = connection.prepareStatement(Constants.TF_ROUTE_INFO_SQL);
      int count = 0;
      try {
        for (String ele : list) {
          String[] arr = ele.toString().split(",");

          count++;
          psts.setLong(1, Long.valueOf(arr[0])); //ROUTE_ID
          psts.setString(2, arr[1]); //INFO1
          psts.setLong(3, Long.valueOf(arr[2])); //INFO2
          psts.setLong(4, Long.valueOf(arr[3])); //INFO3
          psts.setString(5, arr[4]); //INFO4
          psts.setString(6, arr[5]); //INFO5
          psts.setLong(7, Long.valueOf(arr[6])); //INFO6
          psts.setString(8, arr[7]); //INFO7
          psts.setString(9, arr[8]); //INFO8
          psts.setString(10, arr[9]); //INFO9
          psts.setString(11, arr[10]); //INFO10
          psts.setString(12, arr[11]); //INFO11
          psts.setLong(13, Long.valueOf(arr[12])); //INFO12
          psts.setLong(14, Long.valueOf(arr[13])); //INFO13
          psts.setLong(15, Long.valueOf(arr[14])); //INFO14
          psts.setLong(16, Long.valueOf(arr[15])); //INFO15
          psts.addBatch();
        }

        psts.executeBatch();
        connection.commit();

      } catch (SQLException e){
        e.printStackTrace();
      }



    } catch (Exception e){
      e.printStackTrace();
    }
  }

  public static void singleInsert(String sql){

    try (Connection connection = dataSource.getConnection()){

      Statement statement = connection.createStatement();

      statement.execute(sql);

//      statement.close();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void batchInsert(List<String> sqlList) {
    final int maxSize = 100;
    final int size = sqlList.size();
    System.out.println("size = " + size);
    if (size < maxSize) {
      batchInsertWithSingleConnection(sqlList);
      return;
    }
    int numberOfList = size / maxSize + 1;
    List<List<String>> lists = new ArrayList<>();
    int index = 0;
    for(int i = 0; i < numberOfList; i++) {
      List<String> list = new ArrayList<>();
      for (int ii = 0; ii < maxSize && index < size; ii++, index++) {
        list.add(sqlList.get(index));
      }
      lists.add(list);
    }
    for (List<String> list: lists) {

      batchInsertWithSingleConnection(list);

    }
  }

  public static void batchInsertWithSingleConnection(List<String> sqlList) {

    try (Connection connection = dataSource.getConnection()) {

      Statement statement = connection.createStatement();

      for (String sql: sqlList) {
        statement.addBatch(sql);
      }
      statement.executeBatch();
//      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static Map querySum(String acctId){

    Map map = Maps.newHashMap();
    try (Connection connection = dataSource.getConnection()) {

      Statement statement = connection.createStatement();

      String sql = "SELECT MAX(DEVICE_ID) AS MAX, MIN(DEVICE_ID) AS MIN , count(1)  AS SUM from tf_f_device WHERE acct_id = '" + acctId + "'";

      ResultSet rs = statement.executeQuery(sql);
      Integer sum = 0;
      String max = "";
      String min = "";

      while (rs.next()){
        sum = rs.getInt("SUM");
        max = rs.getString("MAX");
        min = rs.getString("MIN");
      }

      map.put("MAX", max);
      map.put("MIN", min);
      map.put("SUM", sum);

//      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return map;

  }

  public static void del111(String acctId){
    try (Connection connection = dataSource.getConnection()) {

      Statement statement = connection.createStatement();

      String devSql = "DELETE FROM TF_F_DEVICE WHERE ACCT_ID = '" + acctId + "'";
      statement.execute(devSql);

      String simSql = "DELETE FROM TF_R_SIM_INFO WHERE ACCT_ID = '" + acctId + "'";
      statement.execute(simSql);

      String usageSql = "DELETE FROM TF_F_DEVICE_USAGE WHERE ACCT_ID = '" + acctId + "'";
      statement.execute(usageSql);

      String resSql = "DELETE FROM TF_F_DEVICE_RES WHERE ACCT_ID = '" + acctId + "'";
      statement.execute(resSql);

      String stateSql = "DELETE FROM TF_F_DEVICE_STATE WHERE ACCT_ID = '" + acctId + "'";
      statement.execute(stateSql);

      String commPlanSql = "DELETE FROM TF_F_DEVICE_COMM_PLAN WHERE ACCT_ID = '" + acctId + "'";
      statement.execute(commPlanSql);


//      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }


  /**
   * 获取sql.
   * @param param
   * @param tabName
   * @return
   */
  public static String getSql(Map<String, Object> param, String tabName){

    StringBuffer buffer = new StringBuffer();
    buffer.append("INSERT INTO ")
            .append(tabName)
            .append(" (");

    StringBuffer valueBuffer = new StringBuffer();

    for (String key : param.keySet()){
      buffer.append(" ")
              .append(key)
              .append(",");
      if (param.get(key).equals("null") || param.get(key) == null){
        valueBuffer.append(param.get(key));
      } else {
        valueBuffer.append("'" + param.get(key) + "'");
      }

      valueBuffer.append(" ,");
    }

    String prefix = buffer.substring(0, buffer.toString().length()-1);
    String valueStr = valueBuffer.substring(0, valueBuffer.toString().length()-1);

    String sql = prefix + ") VALUES (" + valueStr + ");";

//    System.out.println(sql);
    return sql;

  }

  /**
   *
   * @param sql
   * @param tab
   * @return
   */
  public static List quertFieldTab(String sql, String tab){

    List list = Lists.newArrayList();

    try (Connection connection = cenDataSource.getConnection()) {

      Statement statement = connection.createStatement();

      ResultSet rs = statement.executeQuery(sql);

      while (rs.next()){
        Map map = Maps.newHashMap();

        switch (tab){
          case "TS_FIELD2" : {
            map.put("FIELD2_ID", rs.getString("FIELD2_ID"));
            map.put("PARENT_ID", rs.getString("PARENT_ID"));
            map.put("FIELD2_NAME", rs.getString("FIELD2_NAME"));
            map.put("DESC", rs.getString("DESC"));
            break;
          }
          case "TS_FIELD3" : {
            map.put("FIELD3_ID", rs.getString("FIELD3_ID"));
            map.put("FIELD3_NAME", rs.getString("FIELD3_NAME"));
            map.put("DESC", rs.getString("DESC"));
            break;
          }
          case "TS_ATTR_CODE" : {
            map.put("ATTR_CODE", rs.getString("ATTR_CODE"));
            map.put("ATTR_NAME", rs.getString("ATTR_NAME"));
            break;
          }
        }

        list.add(map);
      }



    } catch (Exception e){
      e.printStackTrace();
    }

    return list;


  }

  public static List<Long> queryRouteIds(Long start){
    List list = Lists.newArrayList();

    StringBuffer buffer = new StringBuffer();
    buffer.append("SELECT ROUTE_ID FROM TAB_BACKUP LIMIT ").append(start).append(" , 500");

    System.out.println("********** start = " + start);
    System.out.println("select sql = " + buffer.toString());

    try (Connection connection = dataSource.getConnection()) {

      Statement statement = connection.createStatement();

      ResultSet rs = statement.executeQuery(buffer.toString());

      while (rs.next()){
        list.add(Long.valueOf(rs.getString(1)));
      }

    } catch (Exception e){
      e.printStackTrace();
    }

    return list;
  }

  /**
   * select data from table.
   * @param tabName
   * @param routeId
   * @return
   */
  public static Map querySyncData11(String tabName, Long routeId){
    List list = Lists.newArrayList();
    Long MAX_ROUTE_ID = 1l;

    String sql = "SELECT * FROM " + tabName + " WHERE ROUTE_ID > " + routeId + " ORDER BY ROUTE_ID DESC LIMIT 10000;";

    List rsL = sqlCodeService.select(sql, new HashMap<>());

    if (rsL.size() < 1){
      System.out.println("*************** rsL.size() < 1 ***************");
      return null;
    }

    Map map = (Map) rsL.get(0);

    MAX_ROUTE_ID = Long.valueOf(map.get("ROUTE_ID").toString());

    Map rsM = Maps.newHashMap();
    rsM.put("MAX_ROUTE_ID",MAX_ROUTE_ID);
    rsM.put("LIST_RS",list);

    return rsM;

  }


  public static Map querySyncData(String tabName, int startIdx, Long routrId){
    List list = Lists.newArrayList();
    Long MAX_ROUTE_ID = 1l;

    String sql = "SELECT * FROM " + tabName +  " WHERE ROUTE_ID > "+ routrId +" ORDER BY ROUTE_ID DESC LIMIT "+startIdx+", 2000000;";

    try(Connection connection = dataSource.getConnection()){

      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(sql);

      while (rs.next()){
        Map map = Maps.newHashMap();

        switch (tabName){
          case "TF_MAIN" : {
            List<String> mainL = Arrays.asList(Constants.TF_MAIN_FIELDS.split(","));
            for (int i = 0; i < mainL.size(); i++) {
              map.put(mainL.get(i), rs.getString(mainL.get(i)));
            }
            if (null != map.get("ROUTE_ID") && "" != map.get("ROUTE_ID") &&
                    (MAX_ROUTE_ID < Long.valueOf(map.get("ROUTE_ID").toString()))){
              MAX_ROUTE_ID = Long.valueOf(map.get("ROUTE_ID").toString());
            }
            break;
          }
          case "TF_MAIN_ATTR" : {
            List<String> mainL = Arrays.asList(Constants.TF_MAIN_ATTR_FIELDS.split(","));
            for (int i = 0; i < mainL.size(); i++) {
              map.put(mainL.get(i), rs.getString(mainL.get(i)));
            }
            if (null != map.get("ROUTE_ID") && "" != map.get("ROUTE_ID") &&
                    (MAX_ROUTE_ID < Long.valueOf(map.get("ROUTE_ID").toString()))){
              MAX_ROUTE_ID = Long.valueOf(map.get("ROUTE_ID").toString());
            }
            break;
          }
          case "TF_ROUTE_INFO" : {
            List<String> mainL = Arrays.asList(Constants.TF_ROUTE_INFO_FIELDS.split(","));
            for (int i = 0; i < mainL.size(); i++) {
              map.put(mainL.get(i), rs.getString(mainL.get(i)));
            }
            if (null != map.get("ROUTE_ID") && "" != map.get("ROUTE_ID") &&
                    (MAX_ROUTE_ID < Long.valueOf(map.get("ROUTE_ID").toString()))){
              MAX_ROUTE_ID = Long.valueOf(map.get("ROUTE_ID").toString());
            }
            break;
          }
        }

        list.add(map);
      }


    } catch (SQLException e){
      e.printStackTrace();
    }

    Map rsM = Maps.newHashMap();
    rsM.put("MAX_ROUTE_ID",MAX_ROUTE_ID);
    rsM.put("LIST_RS",list);

    return rsM;

  }


  public static String getuuid(){
    UUID uuid= UUID.randomUUID();
    String str = uuid.toString();
    String uuidStr=str.replace("-", "");
    return uuidStr;
  }

  public static Long getRandomId(){
    //随机生成一位整数
    int random = (int) (Math.random()*9+1);
    String valueOf = String.valueOf(random);
    //生成uuid的hashCode值
    int hashCode = UUID.randomUUID().toString().hashCode();
    //可能为负数
    if(hashCode<0){
      hashCode = -hashCode;
    }
    String value = valueOf + String.format("%015d", hashCode);
    return Long.valueOf(value);
  }

  public static synchronized Long getOrderIdByUUId(String machineId) {

    int hashCodeV = UUID.randomUUID().toString().hashCode();
    if (hashCodeV < 0) {//有可能是负数
      hashCodeV = -hashCodeV;
    }
    // 0 代表前面补充0
    // 4 代表长度为4
    // d 代表参数为正数型
    String orderId=machineId + String.format("%015d", hashCodeV);
    return Long.valueOf(orderId);
  }

}
