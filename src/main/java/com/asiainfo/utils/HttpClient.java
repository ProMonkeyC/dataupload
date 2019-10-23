/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.asiainfo.utils;

import com.alibaba.dubbo.common.json.JSON;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author chenlong
 * Created on 2018/9/6
 */
public class HttpClient {
  /**
   * 超时时间, 单位: 秒.
   */
  private static final int timeOut = 10 * 1000;

  private static CloseableHttpClient httpClient = null;

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HttpClient.class);


  /**
   * 设置 Header.
   *
   * @param httpRequestBase
   */
  private static void setRequestHeader(HttpRequestBase httpRequestBase, int _timeOut) {
    // 配置请求的超时设置
    int tmOut = timeOut;
    if (_timeOut != 0) {
      tmOut = _timeOut;
    }
    RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(tmOut)
            .setConnectTimeout(tmOut)
            .setSocketTimeout(tmOut)
            .build();
    httpRequestBase.setConfig(requestConfig);
  }

  public static CloseableHttpClient createHttpClient(int maxTotal, int maxPerRoute, int maxRoute
          , String hostname, int port, String username, String password) {
    ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
    LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", plainsf)
            .register("https", sslsf)
            .build();
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
    // 将最大连接数增加
    cm.setMaxTotal(maxTotal);
    // 将每个路由基础的连接增加
    cm.setDefaultMaxPerRoute(maxPerRoute);
    HttpHost httpHost = new HttpHost(hostname, port);
    // 将目标主机的最大连接数增加
    cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

    // 请求重试处理
    HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
      public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        if (executionCount >= 5) {// 如果已经重试了5次，就放弃
          return false;
        }
        if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
          return true;
        }
        if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
          return false;
        }
        if (exception instanceof InterruptedIOException) {// 超时
          return false;
        }
        if (exception instanceof UnknownHostException) {// 目标服务器不可达
          return false;
        }
        if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
          return false;
        }
        if (exception instanceof SSLException) {// SSL握手异常
          return false;
        }

        if (exception instanceof SocketException) {// SocketException
          return true;
        }

        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        // 如果请求是幂等的，就再次尝试
        if (!(request instanceof HttpEntityEnclosingRequest)) {
          return true;
        }
        return false;
      }
    };
    CloseableHttpClient httpClient = null;
    if (username != null && password != null) {
      CredentialsProvider credsProvider = new BasicCredentialsProvider();
      credsProvider.setCredentials(AuthScope.ANY,
              new UsernamePasswordCredentials(username, password));
      httpClient = HttpClients.custom()
              .setDefaultCredentialsProvider(credsProvider)
              .setConnectionManager(cm)
              .setRetryHandler(httpRequestRetryHandler)
              .build();
    } else {
      httpClient = HttpClients.custom()
              .setConnectionManager(cm)
              .setRetryHandler(httpRequestRetryHandler)
              .build();
    }


    return httpClient;
  }

  /**
   * 获取HttpClient对象.
   *
   * @param urlString
   * @return
   */
  public synchronized static CloseableHttpClient getHttpClient(String urlString, String username, String password) throws MalformedURLException {
    URL url;
    try {
      url = new URL(urlString);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw e;
    }
    String hostname = url.getHost();
    int port = 80;
    if (hostname.contains(":")) {
      String[] arr = hostname.split(":");
      hostname = arr[0];
      port = Integer.parseInt(arr[1]);
    }
    if (httpClient == null) {
      httpClient = createHttpClient(50, 15, 60, hostname, port, username, password);
    }
    return httpClient;
  }

  private static void setPostParams(HttpPost httpost,
                                    Map<String, Object> params) {
    List<NameValuePair> nvps = new ArrayList<>();
    Set<String> keySet = params.keySet();
    for (String key : keySet) {
      nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
    }
    try {
      httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public static List<NameValuePair> buildNameValuePairParams(Map<String, Object> params) {
    List<NameValuePair> nvps = new ArrayList<>();
    Set<String> keySet = params.keySet();
    for (String key : keySet) {
      nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
    }

    return nvps;
  }

  public static String post(String url, Map<String, Object> params, int timeOut) throws Exception {
    return postInner(url, params, timeOut, null, null);
  }

  public static String post(String url, Map<String, Object> params) throws Exception {
    return postInner(url, params, 0, null, null);
  }

  public static String post(String url, Map<String, Object> params, String username, String password) throws Exception {
    return postInner(url, params, 0, username, password);
  }


  /**
   * POST请求URL获取内容.
   *
   * @param url
   * @param params json
   * @return
   * @throws Exception
   */
  private static String postInner(String url, Map<String, Object> params, int timeOut, String username, String password) throws Exception {
    HttpPost httppost = new HttpPost(url);
    setRequestHeader(httppost, timeOut);
    CloseableHttpResponse response = null;

    try {
      if (params != null) {
        StringEntity jsonEntity = new StringEntity(JSON.json(params), Consts.UTF_8);
        jsonEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httppost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httppost.setEntity(jsonEntity);
      }
      response = getHttpClient(url, username, password).execute(httppost, HttpClientContext.create());
      HttpEntity entity = response.getEntity();
      String result = EntityUtils.toString(entity, "utf-8");
      EntityUtils.consume(entity);
      return result;
    } catch (Exception e) {
      throw e;
    } finally {
      try {
        if (response != null) {
          response.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * GET请求URL获取内容.
   *
   * @param url
   */
  public static String get(String url, List<NameValuePair> params) throws URISyntaxException {
    HttpGet httpget = new HttpGet(url);
    if (params != null && !params.isEmpty()) {
      URI uri = new URIBuilder().addParameters(params).build();
      httpget.setURI(uri);
    }
    setRequestHeader(httpget, 0);
    CloseableHttpResponse response = null;
    try {
      response = getHttpClient(url, null, null).execute(httpget, HttpClientContext.create());
      if (response.getStatusLine().getStatusCode() < 200 ||
              response.getStatusLine().getStatusCode() > 300) {
        throw new Exception("error");
      }
      HttpEntity entity = response.getEntity();
      String result = EntityUtils.toString(entity, "utf-8");
      EntityUtils.consume(entity);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (response != null) {
          response.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * DELETE请求删除内容
   */
  public static String delete(String url) throws Exception {
    HttpDelete httpDelete = new HttpDelete(url);
    setRequestHeader(httpDelete, 0);
    CloseableHttpResponse response = null;
    try {
      response = getHttpClient(url, null, null).execute(httpDelete);
      HttpEntity entity = response.getEntity();
      String result = EntityUtils.toString(entity, "utf-8");
      EntityUtils.consume(entity);
      return result;
    } catch (Exception e) {
      throw e;
    } finally {
      try {
        if (response != null) {
          response.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
