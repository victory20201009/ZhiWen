package com.zzu.zk.zhiwen.utils;

import java.net.HttpURLConnection;
import java.net.URL;

public class UrlUtils {


public static HttpURLConnection  init(String urll) throws Exception {

    URL url = new URL(urll);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    connection.setDoOutput(true);
    connection.setDoInput(true);
    connection.setUseCaches(false);
    connection.setRequestProperty("content-type", "text/html");
    connection.setConnectTimeout(10 * 1000);
    connection.connect();
    return connection;
}

}
