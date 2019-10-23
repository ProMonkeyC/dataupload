/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.syncInitialize;

import com.asiainfo.utils.Constants;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author chenlong
 * Created on 2018/9/10
 */
public class TestThread implements Runnable {

  Integer num;

  String url;

  Map map;

  public TestThread(Integer num, String url, Map map) {
    this.num = num;
    this.url = url;
    this.map = map;
  }

  @Override
  public void run() {
    for (int i = num; i < (625000 + num); i++) {

      map.put("ROUTE_ID", i);
      Operate.sendData(Constants.ES_ID, map, url);

    }
  }
}
