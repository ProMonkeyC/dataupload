/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * @author chenlong
 * Created on 2018/9/4
 */
public class Test {

  private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public static void main(String[] args) {
    List list = DBTool.queryRouteIds(0l);
    System.out.println("list.size = " + list.size());
  }




}
