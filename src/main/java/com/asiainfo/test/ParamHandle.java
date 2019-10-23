/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.test;

import com.asiainfo.utils.DBTool;
import com.asiainfo.utils.IdGenerator;
import com.google.common.collect.Maps;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author chenlong
 * Created on 2018/9/4
 */
public class ParamHandle {

  private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * TS_FIELD2
   * @return
   */
  public static Map handleFiled2Tab(long field2Id){
    Map map = Maps.newHashMap();
    map.put("FIELD2_ID", field2Id);
    map.put("PARENT_ID", field2Id);
    map.put("FIELD2_NAME", field2Id + "_Test");
    map.put("DESC", field2Id + "_Desc");
    return map;
  }

  /**
   * TS_FIELD3
   * @return
   */
  public static Map handleFiled3Tab(long field3Id){
    Map map = Maps.newHashMap();
    map.put("FIELD3_ID", field3Id);
    map.put("FIELD3_NAME", field3Id + "_Test");
    map.put("DESC", field3Id + "_Desc");
    return map;
  }

  /**
   * TF_ROUTE_INFO
   * @return
   */
  public static Map handleRouteInfoTab(Long routeId){
    Map map = Maps.newHashMap();
    map.put("ROUTE_ID", routeId);
    map.put("INFO1", routeId + "_INFO1");
    map.put("INFO2", routeId.toString().substring(0,8));
    map.put("INFO3", routeId.toString().substring(1,9));
    map.put("INFO4", "INFO4");
    map.put("INFO5", "INFO5");
    map.put("INFO6", routeId.toString().substring(2));
    map.put("INFO7", "INFO7");
    map.put("INFO8", "INFO8");
    map.put("INFO9", "A");
    map.put("INFO10", "B");
    map.put("INFO11", "C");
    map.put("INFO12", routeId.toString().substring(0,12));
    map.put("INFO13", routeId.toString().substring(0,12));
    map.put("INFO14", routeId.toString().substring(0,16));
    map.put("INFO15", routeId.toString().substring(1,17));
    return map;
  }

  public static String handleRouteInfoTabStr(Long routeId){
    StringBuffer buffer = new StringBuffer();
    buffer.append(routeId).append(",")
            .append((IdGenerator.getInfo1() + DBTool.getuuid()).toString().substring(0,49)).append(",")
            .append(routeId.toString().substring(10))
            .append(",")
            .append(routeId.toString().substring(9,17))
            .append(",")
            .append("INFO4").append(",").append("INFO5")
            .append(",")
            .append(routeId.toString().substring(2))
            .append(",")
            .append("INFO7").append(",")
            .append("INFO8").append(",")
            .append("A").append(",")
            .append("B").append(",")
            .append("C").append(",")
            .append(routeId.toString().substring(6))
            .append(",")
            .append(routeId.toString().substring(6))
            .append(",")
            .append(routeId.toString().substring(2))
            .append(",")
            .append(routeId.toString().substring(1,17));

    return buffer.toString();
  }

  /**
   * TAB_MAIN
   * @return
   */
  public static Map handleMainTab(Long id, Long field2Id, Long field3Id){
    String time = format.format(System.currentTimeMillis());
    Map map = Maps.newHashMap();
    map.put("ID", id);
    map.put("ROUTE_ID", id);
    map.put("FIELD1", id + "_FIELD1");
    map.put("FIELD2", field2Id);
    map.put("FIELD3", field3Id);
    map.put("FIELD4", "FIELD4");
    map.put("FIELD8", DBTool.getRandomId() + DBTool.getuuid());
    map.put("FIELD14", DBTool.getRandomId());
    map.put("FIELD15", DBTool.getRandomId());
    return map;
  }

  public static String handleMainTabStr(Long id, Long field2Id, Long field3Id, Long routeId){
    StringBuffer buffer = new StringBuffer();
    buffer.append(id).append(",")
            .append(routeId).append(",")
            .append(id + "_FIELD1").append(",")
            .append(field2Id).append(",")
            .append(field3Id).append(",")
            .append("FIELD4").append(",")
            .append(id + DBTool.getuuid()).append(",")
            .append(id.toString().substring(2)).append(",")
            .append(IdGenerator.getF15()).append(",")
            .append(IdGenerator.getF51()).append(",")
            .append(id.toString().substring(4) + "_FIELD5").append(",")
            .append(id.toString().substring(3)).append(",")
            .append(id.toString().substring(5) + "_FIELD7");
    return buffer.toString();
  }

  /**
   * TF_MAIN_ATTR
   * @return
   */
  public static Map handleMainAttrTab(long id, String attrCode, String attrValue){
    Map map = Maps.newHashMap();
    map.put("ID", id);
    map.put("ROUTE_ID", id);
    map.put("ATTR_CODE", attrCode);
    map.put("ATTR_VALUE", attrValue);
    return map;
  }

  /**
   * TS_ATTR_CODE
   * @return
   */
  public static Map handleAttrCodeTab(String attrCode, String attrValue){
    Map map = Maps.newHashMap();
    map.put("ATTR_CODE", attrCode);
    map.put("ATTR_NAME", attrValue);
    return map;
  }




}
