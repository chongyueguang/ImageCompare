package com.company.service;

import com.alibaba.fastjson.JSONObject;
import com.company.net.FailListener;
import com.company.net.SuccessListener;
import com.company.util.PostUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class PostThreadService implements Runnable {

    private Semaphore semaphore;
    private JSONObject jsonData;
    private CountDownLatch countDownLatch;

    public PostThreadService(Semaphore semaphore, JSONObject jsonData, CountDownLatch countDownLatch) {
        this.semaphore = semaphore;
        this.jsonData = jsonData;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            PostUtils.postWithParams("http//;", jsonData, new SuccessListener() {
                @Override
                public void success(String result) {
                    System.out.println("success");
                }
            }, new FailListener() {
                @Override
                public void fail() {
                    System.out.println("fail");
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            semaphore.release();
            countDownLatch.countDown();
        }
    }
}
