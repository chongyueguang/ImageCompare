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

    public PostThreadService(Semaphore semaphore, JSONObject jsonData) {
        this.semaphore = semaphore;
        this.jsonData = jsonData;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            double ran = Math.random();
            System.out.println(ran);
            Thread.sleep((long) (ran * 10000));
            System.out.println("线程" + Thread.currentThread().getName() +"进入，当前还可用" + semaphore.availablePermits() + "个线程");
            PostUtils.postWithParams("http://192.168.8.11/api/diff;", jsonData, new SuccessListener() {
                @Override
                public void success(String result) {
                    System.out.println("success");
                }
            }, new FailListener() {
                @Override
                public void fail() {
                    System.out.println("线程" + Thread.currentThread().getName() + "fail");
                }
            });
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }finally {
            semaphore.release();
        }
    }
}
