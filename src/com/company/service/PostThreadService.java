package com.company.service;

import com.alibaba.fastjson.JSONObject;
import com.company.Const;
import com.company.model.ResultInfoModel;
import com.company.model.RunThreadResModel;
import com.company.net.FailListener;
import com.company.net.SuccessListener;
import com.company.util.LogUtils;
import com.company.util.PostUtils;
import java.util.concurrent.Semaphore;

public class PostThreadService implements Runnable {
    private Semaphore semaphore;
    private JSONObject jsonData;
    private RunThreadResModel runThreadResModel;

    public PostThreadService(Semaphore semaphore, JSONObject jsonData, RunThreadResModel runThreadResModel) {
        this.semaphore = semaphore;
        this.jsonData = jsonData;
        this.runThreadResModel = runThreadResModel;
    }

    @Override
    public void run() {
        synchronized (this){
            try {
                semaphore.acquire();
                LogUtils.info("线程：" + Thread.currentThread().getName() +"进行中，可用残余线程数：" + semaphore.availablePermits());
                //Thread.sleep(10000);
                PostUtils.postWithParams("http://192.168.8.11/api/diff", jsonData, new SuccessListener() {
                    @Override
                    public void success(String result) {
                        LogUtils.info("线程："  + Thread.currentThread().getName() + "返信成功");
                        JSONObject datas = JSONObject.parseObject(result);
                        runThreadResModel.setResultInfoModel(datas.toJavaObject(ResultInfoModel.class));
                    }
                }, new FailListener() {
                    @Override
                    public void fail() {
                        Const.stopFlg = true;
                        LogUtils.error("线程："  + Thread.currentThread().getName() + "返信失败");
                        //throw new Exception("线程返信失败");
                    }
                });
            } catch (InterruptedException e) {
                Const.stopFlg = true;
                LogUtils.error("线程："  + Thread.currentThread().getName() + "执行时，问题发生：" + e.getMessage());
                e.printStackTrace();
            }finally {
                semaphore.release();
            }
        }
    }
}
