/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.syncInitialize;

import com.asiainfo.utils.Constants;
import com.asiainfo.utils.DBTool;

import java.util.List;
import java.util.Map;

/**
 * @author chenlong
 * Created on 2018/9/6
 */
public class HandleData implements Runnable {

  private String tabName;

  public HandleData(String tabName) {
    this.tabName = tabName;
  }

  public String getTabName() {
    return tabName;
  }

  public void setTabName(String tabName) {
    this.tabName = tabName;
  }

  /**
   * rsM.put("MAX_ROUTE_ID",MAX_ROUTE_ID);
   rsM.put("LIST_RS",list);
   */

  @Override
  public void run() {
    int startIdx = 0;
    Long maxRouteId = 1l;
    while (true) {

      Map<String, Object> mapRs = DBTool.querySyncData(tabName, startIdx,maxRouteId);
      if (mapRs.size() < 1) {
        System.out.println("****************** mapRs < 1 *******************");
        break;
      }

      System.out.println("************** table = "+ tabName +" , startIdx = "+startIdx+" ******* mapRs.size = " + mapRs.size() + " *************");

      List<Map<String, Object>> list = (List<Map<String,Object>>) mapRs.get("LIST_RS");

      if (list.size() < 1) {
        System.out.println("****************** list < 1 *******************");
        break;
      }

      for (int j = 0; j < list.size(); j++){
        Map<String, Object> tmp = list.get(j);
        Operate.sendData(Constants.ES_ID, tmp);
      }

      maxRouteId = Long.valueOf(mapRs.get("MAX_ROUTE_ID").toString());

      startIdx += 2000000;



    }

  }
}
