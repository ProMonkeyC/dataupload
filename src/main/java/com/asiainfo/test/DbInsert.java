/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.test;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chenlong
 * Created on 2018/9/4
 */
public class DbInsert {

  private final static ExecutorService service = Executors.newFixedThreadPool(20);

  public static void main(String[] args) {

    for (int i = 0; i < 20; i++) {

//      service.submit(new InsertTask(Long.valueOf(i+1)));
      service.submit(new InsertTask2(Long.valueOf(i+1)));

    }

  }

}
