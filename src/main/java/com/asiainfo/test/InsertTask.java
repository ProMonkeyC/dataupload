/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.test;

import com.asiainfo.utils.DBTool;
import com.asiainfo.utils.IdGenerator;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * @author chenlong
 * Created on 2018/9/4
 */
public class InsertTask implements Runnable{

  private final List field2List = Lists.newArrayList();
  private final List field3List = Lists.newArrayList();

  private Long idx;

  public InsertTask(Long idx) {
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

    List list = Lists.newArrayList();
    for (int i = 0; i < 500000; i++){
      try {
        Long id = IdGenerator.getId(idx * (i+1));
        Long tmp = id % (field2List.size());
        int idx = Integer.valueOf(tmp.toString());
        Map tmpM = (Map) field2List.get(idx);
        Long field2Id = Long.valueOf(tmpM.get("FIELD2_ID").toString());

        Long tmp3 = id % (field3List.size());
        int idx3 = Integer.valueOf(tmp3.toString());
        Map tmpM3 = (Map) field3List.get(idx3);
        Long field3Id = Long.valueOf(tmpM3.get("FIELD3_ID").toString());

        list.add(DBTool.getSql(ParamHandle.handleMainTab(id, field2Id, field3Id), "TAB_MAIN"));
        list.add(DBTool.getSql(ParamHandle.handleRouteInfoTab(id), "TAB_BACKUP"));

        if ((i % 2000 == 0 || i + 1 == 500000) && i > 0) { //2000次提交一次
          DBTool.batchInsert(list);
          System.out.println("Thread_name : " + Thread.currentThread().getName() + ", 完成第"+ (i/2000) +"次提交数据...");
          list.clear();
        }

      } catch (Exception e){
        e.printStackTrace();
      }
    }

  }
}
