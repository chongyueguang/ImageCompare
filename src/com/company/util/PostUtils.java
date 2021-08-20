package com.company.util;

import com.alibaba.fastjson.JSONObject;
import com.company.net.CookiesHolder;
import com.company.net.FailListener;
import com.company.net.SuccessListener;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

public class PostUtils {
    public static void post(String url, SuccessListener sListener, FailListener fListener) {
        postWithParams(url, new JSONObject(),sListener,fListener);
    }

    public static void postWithParams(String url, JSONObject jsonObject, SuccessListener sListener, FailListener fListener) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(CookiesHolder.getCookieStore()).build();
        CloseableHttpResponse response = null;
        try {

            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(30000) //服务器响应超时时间
                    .setConnectTimeout(30000) //连接服务器超时时间
                    .build();

            httpPost.setConfig(requestConfig);

            StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            // 由客户端执行(发送)请求
            response = httpClient.execute(httpPost);

            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            //TODO
            LogUtils.warn("status:"+response.getStatusLine());

            if (responseEntity != null) {
               sListener.success(EntityUtils.toString(responseEntity));
            }
        } catch (Exception e) {
           // e.printStackTrace();
            LogUtils.error(e.getMessage());
            fListener.fail();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
