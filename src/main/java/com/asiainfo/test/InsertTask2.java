/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.test;

import com.asiainfo.utils.DBTool;
import com.asiainfo.utils.IdGenerator;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author chenlong
 * Created on 2018/9/30
 */
public class InsertTask2 implements Runnable{

  private final Logger logger = LoggerFactory.getLogger(InsertTask2.class);

  private final List field2List = Lists.newArrayList();
  private final List field3List = Lists.newArrayList();
  private final List attrCodeList = Lists.newArrayList();

  private Long idx;

  public InsertTask2(Long idx) {
    this.idx = idx;
  }

  public Long getIdx() {
    return idx;
  }

  public void setIdx(Long idx) {
    this.idx = idx;
  }

  @Override
  public void run() {

    if (field2List.size() < 1){
      field2List.addAll(DBTool.quertFieldTab("SELECT * FROM TS_FIELD2", "TS_FIELD2"));
    }

    if (field3List.size() < 1){
      field3List.addAll(DBTool.quertFieldTab("SELECT * FROM TS_FIELD3", "TS_FIELD3"));
    }

    List<Long> routeIs = DBTool.queryRouteIds((idx-1)*500);

    if (routeIs.size() < 1){
      System.out.println("************ routeIs.size() < 1 *************");
      return;
    }

    for (int j = 0; j < routeIs.size(); j++) {
      Long routeId = routeIs.get(j);

      List<String> listMain = Lists.newArrayList();
//    List<String> listRoute = Lists.newArrayList();

      for (int i = 0; i < 800; i++){
        try {
          Long id = IdGenerator.getId();

          Long tmp3 = id % (field3List.size());
          int idx3 = Integer.valueOf(tmp3.toString());
          Map tmpM3 = (Map) field3List.get(idx3);
          Long field3Id = Long.valueOf(tmpM3.get("FIELD3_ID").toString());

          Long tmp = id % (field2List.size());
          int idx = Integer.valueOf(tmp.toString());
          Map tmpM = (Map) field2List.get(idx);
          Long field2Id = Long.valueOf(tmpM.get("FIELD2_ID").toString());

          listMain.add(ParamHandle.handleMainTabStr(id, field2Id, field3Id, routeId));
//        listRoute.add(ParamHandle.handleRouteInfoTabStr(id));

          if ((i % 800 == 0 || i + 1 == 800) && i > 0) { //800次提交一次
            DBTool.batchInsertMAIN(listMain);
//          DBTool.batchInsertROUTE(listRoute);
//          logger.debug("Thread_name : " + Thread.currentThread().getName() + ", 完成第"+ (i/2000) +"次提交数据...");
            listMain.clear();
//          listRoute.clear();
          }

        } catch (Exception e){
          e.printStackTrace();
        }
      }

      System.out.println("Thread_name : " + Thread.currentThread().getName() + ", 完成第"+ j +
              "次提交数据... ROUTE_ID = " + routeId);

    }





  }

}
