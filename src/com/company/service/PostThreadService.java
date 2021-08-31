package com.company.service;

import com.alibaba.fastjson.JSONObject;
import com.company.Const;
import com.company.model.CompareFileModel;
import com.company.model.ResultInfoModel;
import com.company.model.RunThreadResModel;
import com.company.net.FailListener;
import com.company.net.SuccessListener;
import com.company.util.ImageChangeUtils;
import com.company.util.LogUtils;
import com.company.util.PostUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Semaphore;

public class PostThreadService extends Thread implements Runnable {
    private String postUrl;
    private Semaphore semaphore;
    private JSONObject jsonData;
    private RunThreadResModel runThreadResModel;
    private JTextField txt_new;
    private JTextField txt_old;


    public PostThreadService(Semaphore semaphore, JSONObject jsonData, RunThreadResModel runThreadResModel, JTextField txt_new,JTextField txt_old) {
        super();
        this.semaphore = semaphore;
        this.jsonData = jsonData;
        this.runThreadResModel = runThreadResModel;
        this.txt_new = txt_new;
        this.txt_old = txt_old;
        try {
            InputStream in = com.company.MainForm.class.getClassLoader().getResourceAsStream("config.properties");
//            String confPath = System.getProperty("user.dir") + "\\config.properties";
//            InputStream in = new BufferedInputStream(new FileInputStream(confPath));
            Properties props = new Properties();
            props.load(in);
            postUrl = props.getProperty("postUrl");
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        synchronized (this){
            try {
                semaphore.acquire();
                if(!Const.stopFlg){
                    LogUtils.info("线程：" + Thread.currentThread().getName() +"进行中，可用残余线程数：" + semaphore.availablePermits());
                    PostUtils.postWithParams(postUrl + "/api/diff", jsonData, new SuccessListener() {
                        @Override
                        public void success(String result) {
                            LogUtils.info("线程："  + Thread.currentThread().getName() + "返信成功");
                            JSONObject datas = JSONObject.parseObject(result);
                            runThreadResModel.setResultInfoModel(datas.toJavaObject(ResultInfoModel.class));
                            CompareFileModel compareFileModel = runThreadResModel.getCompareFileModel();
                            String oldFolder = txt_old.getText();
                            oldFolder = oldFolder.substring(oldFolder.lastIndexOf("\\"),oldFolder.length()) ;
                            String newFolder = txt_new.getText();
                            newFolder = newFolder.substring(newFolder.lastIndexOf("\\"),newFolder.length()) ;
                            compareFileModel.getFromFile().renameTo(new File( txt_new.getText() +"\\RESULT\\"+oldFolder+"\\"+ compareFileModel.getKey()));
                            compareFileModel.getToFile().renameTo(new File( txt_new.getText() +"\\RESULT\\"+newFolder+"\\"+ compareFileModel.getKey()));
                            ImageChangeUtils.base64StrToImage(runThreadResModel.getResultInfoModel().getData().getDiffImage1(),txt_new.getText() +"\\RESULT\\TEMPOLD\\"+ compareFileModel.getKey());
                            ImageChangeUtils.base64StrToImage(runThreadResModel.getResultInfoModel().getData().getDiffImage2(),txt_new.getText() +"\\RESULT\\TEMPNEW\\"+ compareFileModel.getKey());
                            ImageChangeUtils.joinImage(new File(txt_new.getText() +"\\RESULT\\TEMPOLD\\"+ compareFileModel.getKey()),
                                    new File(txt_new.getText() +"\\RESULT\\TEMPNEW\\"+ compareFileModel.getKey()),
                                    txt_new.getText() +"\\RESULT\\比較結果\\"+ compareFileModel.getKey());
                            Const.runThreadResModels.add(runThreadResModel);
                        }
                    }, new FailListener() {
                        @Override
                        public void fail() throws Exception {
                            //Const.stopFlg = true;
                            LogUtils.error("线程："  + Thread.currentThread().getName() + "返信失败");
                            throw new Exception("线程返信失败");
                        }
                    });
                }
            } catch (InterruptedException e) {
                //Const.stopFlg = true;
                LogUtils.error("线程："  + Thread.currentThread().getName() + "执行时，问题发生：" + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                LogUtils.error(e.getMessage());
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }
    }
}
