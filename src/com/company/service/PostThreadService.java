package com.company.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.company.model.ImageResponseModel;
import com.company.model.ResultInfoModel;
import com.company.net.FailListener;
import com.company.net.SuccessListener;
import com.company.util.LogUtils;
import com.company.util.PostUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class PostThreadService implements Runnable {

    private Semaphore semaphore;
    private JSONObject jsonData;
    private File fromFile;
    private File toFile;
    private String newFolderPath;

    public PostThreadService(Semaphore semaphore, JSONObject jsonData, File fromFile, File toFile,String newFolderPath) {
        this.semaphore = semaphore;
        this.jsonData = jsonData;
        this.fromFile = fromFile;
        this.toFile = toFile;
        this.newFolderPath = newFolderPath;
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
                    resultInfoModel.getData().getDiffImage2();

                    try {
                        fromFile.renameTo(new File( newFolderPath +"\\RESULT\\実行完了現エビデンス\\"+ toFile.getPath() + "\\" + toFile.getName()));
                        toFile.renameTo(new File( newFolderPath +"\\RESULT\\実行完了新エビデンス\\"+ toFile.getPath() + "\\" + toFile.getName()));
                    }catch (Exception e){
                        LogUtils.error(e.getMessage());
                    }
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
