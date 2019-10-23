package com.asiainfo.jdbc;

import com.asiainfo.utils.DBTool;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcManager {

  private static String url = "jdbc:mysql://172.18.50.150:8066/info_dev?useUnicode=true&characterEncoding=UTF-8&useSSL=false";

  private static String driver = "com.mysql.jdbc.Driver";

  private static String userName = "fw_crm";

  private static String password = "X^o$P*!Gwl";

  private static ComboPooledDataSource dataSource;

  static {
    try {
      Class.forName(driver);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    dataSource = new ComboPooledDataSource();
  }

  public static int exec (String sql) throws SQLException {
    Connection connection = dataSource.getConnection();
    Statement statement = connection.createStatement();
    statement.execute(sql);
    ResultSet resultSet = statement.getResultSet();
    return 0;
  }

  public static void batchExec (List<String> sqlList) {
    int size = sqlList.size();

  }

//  public

  public static void execWithSingleConn(List<String> sqlList) {
    try (Connection connection = dataSource.getConnection()) {

      PreparedStatement pst = null;
//      Statement statement = connection.createStatement();

      pst = connection.prepareStatement("");
      connection.setAutoCommit(false);
      for (String sql: sqlList) {
//        statement.addBatch(sql);
        pst.addBatch(sql);
      }
//      statement.executeBatch();
      pst.executeBatch();
      connection.commit();
//      ResultSet set = statement.getResultSet();
//      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void main1(String[] args) throws SQLException {
    String sql = "UPDATE TF_F_DEVICE SET USER_DEVICE_ID = 123678 WHERE DEVICE_ID = '2100000002930010'";
    String sql1 = "UPDATE TF_F_DEVICE SET USER_DEVICE_ID = 876543 WHERE DEVICE_ID = '2100000002930011'";
    List<String> list = new ArrayList<>();
    list.add(sql);
    list.add(sql1);
//    execWithSingleConn(list);
//    DBTool.batchInsert(list);
    DBTool.singleInsert(sql);
  }

}
