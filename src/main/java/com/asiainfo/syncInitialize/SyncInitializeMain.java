/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.syncInitialize;


import com.asiainfo.utils.Constants;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chenlong
 * Created on 2018/9/6
 */
public class SyncInitializeMain {

  private static final ExecutorService service = Executors.newFixedThreadPool(3);

  public static void main(String[] args) {

    for (int i = 0; i < Constants.TABLE_NAME_LIST.size(); i++) {

      HandleData handleData = new HandleData(Constants.TABLE_NAME_LIST.get(i));
      service.submit(handleData);

    }

  }

}
