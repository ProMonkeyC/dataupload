/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.syncInitialize;

import com.asiainfo.utils.Constants;
import com.google.common.collect.Maps;
import org.codehaus.jackson.map.ObjectMapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chenlong
 * Created on 2018/9/10
 */
public class SyncTest {

  private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private final static ExecutorService executorService = Executors.newFixedThreadPool(40);

  public static void main(String[] args) throws Exception{

    int total = 20000000;

    String json = "{\"CONTACT_NAME\":\"wer\"," +
            "\"RECV_UPDATE_MAIL\":\"wer@f.d\"," +
            "\"PHOHE\":\"8888888\"," +
            "\"DELIVERY_DATE\":\"2018-09-09 12:12\"," +
            "\"PO_NUMBER\":\"21323\"," +
            "\"FAX\":\"8888888\"," +
            "\"ACCT_NAME\":\"dsfd\"," +
            "\"ACCT_ID\":\"1\"," +
            "\"TARGET_IMPDATE\":\"2018-09-09 12:12\"," +
            "\"OPN\":\"123\"," +
            "\"ORDER_TYPE\":\"0\"," +
            "\"PRICE\":\"1\"," +
            "\"COMM_PLAN_ID\":\"17000023\"," +
            "\"RATE_PLAN_ID\":\"17000001\"," +
            "\"EXPEDITED\":\"Y\"," +
            "\"TEST\":\"21\"," +
            "\"CUSTOM_USERNAME\":\"test\"," +
            "\"NOTES\":\"21\"," +
            "\"MODIFY_TAG\":\"0\"," +
            "\"ORDER_STATUS\":\"0\"," +
            "\"SYNC_FLAG\":\"Y\"," +
            "\"TOTAL_FEE\":23232.00," +
            "\"CREATE_TIME\":\"2017-09-09 12:12\"," +
            "\"EX_ORDER_ID\":\"123\"," +
            "\"QUANTITY\":\"123\"," +
            "\"SHIPPING_COMP\":\"\"}";



    String beginTime = format.format(System.currentTimeMillis());

    int beginIdx = 0;

    final Map<String, Object> m = Maps.newHashMap();
    m.put("beginIdx", beginIdx);

    for (int j = 0; j < Constants.ES_URL_LIST.size(); j++){

      m.put("ES_URL", Constants.ES_URL_LIST.get(j));

      for (int n = 0; n < 8; n++) {
        int bi = Integer.valueOf(m.get("beginIdx").toString());
        Map map = new ObjectMapper().readValue(json, Map.class);
        executorService.submit(new TestThread(bi > 0 ? (bi/8)*(n+1) : bi, m.get("ES_URL").toString(), map));
      }
      m.put("beginIdx", Integer.valueOf(m.get("beginIdx").toString()) + 5000000);

    }

    String endTime = format.format(System.currentTimeMillis());
    System.out.println("*********************** BeginTime = " + beginTime + " , EndTime = " + endTime + "***********************");
  }

}
