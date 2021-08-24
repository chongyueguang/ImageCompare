package com.company.service;

import com.alibaba.fastjson.JSONObject;
import com.company.model.CompareFileModel;
import com.company.model.ImageReqInfoModel;
import com.company.model.SettingsModel;
import com.company.service.TblJobInfoService;
import com.company.util.ImageChangeUtils;
import com.company.util.LogUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class TimerService {

    private static boolean firstFlg = true;

    public static void refreshScreenTimer(JScrollPane jScrollPane, JTable jTable, Vector vectorHeader) {

        new Timer(10000,new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                TblJobInfoService tblJobInfoService = new TblJobInfoService();
                //更新
                tblJobInfoService.updateJobInfoStatusByTime();
                DefaultTableModel dtm2=(DefaultTableModel)jTable.getModel();
                dtm2.setDataVector(tblJobInfoService.getJobInfoByListToVector(),vectorHeader);
                jTable.validate();
                jTable.updateUI();
                jScrollPane.validate();
                jScrollPane.updateUI();
            }
        }).start();
    }

    public static void waitTimer(ArrayList<CompareFileModel> compareFileArr,JTextField txt_new,int jobID){
        new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TblJobInfoService tblJobInfoService = new TblJobInfoService();
                int status = tblJobInfoService.getJobInfoByJobID(jobID - 1);
                if(firstFlg&&((status==3)||(status==4))){
                    int concurrent = 3;//线程条数控制
                    //int fileSize = 1;//每次获取数据的数量
                    ExecutorService executor = Executors.newCachedThreadPool();
                    final Semaphore semaphore = new Semaphore(concurrent);
                    for (CompareFileModel compareFileModel : compareFileArr){ //遍历所有图片文件
                        //对图片文件进行转码
                        String imageBase64From = null;
                        String imageBase64To = null;
                        try {
                            imageBase64From = ImageChangeUtils.imageToBase64ByFile(compareFileModel.getFromFile());
                            imageBase64To = ImageChangeUtils.imageToBase64ByFile(compareFileModel.getToFile());
                        } catch (IOException ioException) {
                            //ioException.printStackTrace();
                            LogUtils.error(ioException.getMessage());
                        }

                        ImageReqInfoModel imageReqInfoModelFrom = new ImageReqInfoModel();
                        imageReqInfoModelFrom.setData(imageBase64From);
                        imageReqInfoModelFrom.setIgnoreAreas(compareFileModel.getFromImageModel());

                        ImageReqInfoModel imageReqInfoModelTo = new ImageReqInfoModel();
                        imageReqInfoModelTo.setData(imageBase64To);
                        imageReqInfoModelTo.setIgnoreAreas(compareFileModel.getToImageModel());

                        SettingsModel settingsModel = new SettingsModel();
                        settingsModel.setConfThres(0);
                        settingsModel.setIouThres(0);
                        settingsModel.setLevdThres(0);
                        settingsModel.setShiftThres(0);

                        JSONObject json = new JSONObject();
                        json.put("image1",imageReqInfoModelFrom);
                        json.put("image2",imageReqInfoModelTo);
                        json.put("settings",settingsModel);


                        executor.execute(new PostThreadService(semaphore, json,compareFileModel.getFromFile(),compareFileModel.getToFile(),txt_new.getText()));
                    }
                    // 退出线程池
                    executor.shutdown();
                    firstFlg = false;
                }
            }
        }).start();

//        java.util.Timer timerUtil = new java.util.Timer();
//        timerUtil.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                TblJobInfoService tblJobInfoService = new TblJobInfoService();
//                int status = tblJobInfoService.getJobInfoByJobID(jobID - 1);
//                if((status==3)||(status==4)){
//                    int concurrent = 3;//线程条数控制
//                    //int fileSize = 1;//每次获取数据的数量
//                    ExecutorService executor = Executors.newCachedThreadPool();
//                    final Semaphore semaphore = new Semaphore(concurrent);
//                    for (CompareFileModel compareFileModel : compareFileArr){ //遍历所有图片文件
//                        //对图片文件进行转码
//                        String imageBase64From = null;
//                        String imageBase64To = null;
//                        try {
//                            imageBase64From = ImageChangeUtils.imageToBase64ByFile(compareFileModel.getFromFile());
//                            imageBase64To = ImageChangeUtils.imageToBase64ByFile(compareFileModel.getToFile());
//                        } catch (IOException ioException) {
//                            //ioException.printStackTrace();
//                            LogUtils.error(ioException.getMessage());
//                        }
//
//                        ImageReqInfoModel imageReqInfoModelFrom = new ImageReqInfoModel();
//                        imageReqInfoModelFrom.setData(imageBase64From);
//                        imageReqInfoModelFrom.setIgnoreAreas(compareFileModel.getFromImageModel());
//
//                        ImageReqInfoModel imageReqInfoModelTo = new ImageReqInfoModel();
//                        imageReqInfoModelTo.setData(imageBase64To);
//                        imageReqInfoModelTo.setIgnoreAreas(compareFileModel.getToImageModel());
//
//                        SettingsModel settingsModel = new SettingsModel();
//                        settingsModel.setConfThres(0);
//                        settingsModel.setIouThres(0);
//                        settingsModel.setLevdThres(0);
//                        settingsModel.setShiftThres(0);
//
//                        JSONObject json = new JSONObject();
//                        json.put("image1",imageReqInfoModelFrom);
//                        json.put("image2",imageReqInfoModelTo);
//                        json.put("settings",settingsModel);
//
//
//                        executor.execute(new PostThreadService(semaphore, json,compareFileModel.getFromFile(),compareFileModel.getToFile(),txt_new.getText()));
//                    }
//                    // 退出线程池
//                    executor.shutdown();
//                }
//                timerUtil.cancel();
//            }
//        }, 5000);
    }
}
