/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.utils;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author chenlong
 * Created on 2018/9/4
 */
public class IdGenerator {

  private static Long randomNumber;
  private static Long curIndex = Long.valueOf(0L);

  private static AtomicLong atomicLong = new AtomicLong(154891545780975858l);

  private static AtomicLong field51 = new AtomicLong(1881046431389154578l);

  private static AtomicLong field15 = new AtomicLong(1589154578097585l);

  private static AtomicLong info1 = new AtomicLong(1539137842474345678l);

  public IdGenerator() {
  }

  public synchronized static Long getId(Long idx){
    Long index = null;
    index = (curIndex = curIndex.longValue() + 1L).longValue() % 1000L;
    if (curIndex.longValue() >= 1000L) {
      curIndex = 0L;
    }
    // 随机数 1-1000000
    randomNumber = Long.valueOf(new Random().nextInt(10000000));
    return ((new Date()).getTime() * 100000L + index.longValue() * 100L
            + randomNumber.longValue() + idx);
  }


  public synchronized static Long genId(String machineId){
    String orderId =
            machineId +
                    (System.currentTimeMillis() + "").substring(1) +
                    (System.nanoTime() + "").substring(7, 10);

    return Long.valueOf(orderId);
  }

  public static Long getId(){
    return atomicLong.getAndIncrement();
  }

  public static Long getF51(){
    return field51.getAndIncrement();
  }

  public static Long getF15(){
    return field15.getAndIncrement();
  }

  public static Long getInfo1(){
    return info1.getAndIncrement();
  }

}
