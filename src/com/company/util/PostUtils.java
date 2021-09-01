package com.company.util;

import com.alibaba.fastjson.JSONObject;
import com.company.net.CookiesHolder;
import com.company.net.FailListener;
import com.company.net.SuccessListener;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

public class PostUtils {
    public static void post(String url, SuccessListener sListener, FailListener fListener) throws Exception {
        postWithParams(url, new JSONObject(),sListener,fListener);
    }

    public static void postWithParams(String url, JSONObject jsonObject, SuccessListener sListener, FailListener fListener) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(CookiesHolder.getCookieStore()).build();
        CloseableHttpResponse response = null;
        try {

            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(3000) //サーバー応答タイムアウト
                    .setConnectTimeout(3000) //接続サーバーのタイムアウト
                    .build();

            httpPost.setConfig(requestConfig);

            StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            // リクエストはクライアントによって実行（送信）されます
            response = httpClient.execute(httpPost);

            // 応答モデルから応答エンティティを取得します
            HttpEntity responseEntity = response.getEntity();
            //TODO
            LogUtils.warn("status:"+response.getStatusLine());

            if (responseEntity != null) {
               sListener.success(EntityUtils.toString(responseEntity));
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage());
            //e.printStackTrace();
            fListener.fail();
        } finally {
            try {
                // リソースを解放する
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
