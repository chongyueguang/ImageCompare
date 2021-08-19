package com.company.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.company.model.ImageResponseModel;
import com.company.model.ResultInfoModel;
import com.company.net.FailListener;
import com.company.net.SuccessListener;
import com.company.util.LogUtils;
import com.company.util.PostUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class PostThreadService implements Runnable {

    private Semaphore semaphore;
    private JSONObject jsonData;

    public PostThreadService(Semaphore semaphore, JSONObject jsonData) {
        this.semaphore = semaphore;
        this.jsonData = jsonData;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            LogUtils.info("线程：" + Thread.currentThread().getName() +"进行中，可用残余线程数：" + semaphore.availablePermits());
            PostUtils.postWithParams("http://192.168.8.11/api/diff", jsonData, new SuccessListener() {
                @Override
                public void success(String result) {
                    LogUtils.info("线程："  + Thread.currentThread().getName() + "返信成功");
                    JSONObject datas = JSONObject.parseObject(result);
                    ResultInfoModel resultInfoModel  = datas.toJavaObject(ResultInfoModel.class);
                    resultInfoModel.getData().getDiffImage1();
                }
            }, new FailListener() {
                @Override
                public void fail() {
                    LogUtils.error("线程："  + Thread.currentThread().getName() + "返信失败");
                }
            });
        } catch (InterruptedException e) {
            LogUtils.error("线程："  + Thread.currentThread().getName() + "执行时，问题发生：" + e.getMessage());
        }finally {
            semaphore.release();
        }
    }
}
