package com.company.service;

import com.alibaba.fastjson.JSONObject;
import com.company.Const;
import com.company.model.CompareFileModel;
import com.company.model.ImageReqInfoModel;
import com.company.model.ResultInfoModel;
import com.company.model.SettingsModel;
import com.company.service.TblJobInfoService;
import com.company.util.ExcelUtil;
import com.company.util.ImageChangeUtils;
import com.company.util.LogUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.*;

public class TimerService {

    private static boolean firstFlg = true;

    public static void refreshScreenTimer(JScrollPane jScrollPane, JTable jTable, Vector vectorHeader) {

        new Timer(10000,new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                TblJobInfoService tblJobInfoService = new TblJobInfoService();
                if(Const.jobID!=0){
                    int status = tblJobInfoService.getJobInfoByJobID(Const.jobID);
                    if (status == 1){
                        //更新
                        tblJobInfoService.updateJobInfoKannSeiByJobID(Const.jobID,Const.kannseiNum);
                    }
                }
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

    public static void waitTimer(ArrayList<CompareFileModel> compareFileArr,JTextField txt_new){
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogUtils.info("等待线程启动");
                TblJobInfoService tblJobInfoService = new TblJobInfoService();
                int status = tblJobInfoService.getJobInfoByJobID(Const.jobID - 1);
                if (firstFlg && ((status == 3) || (status == 4))) {
                    tblJobInfoService.updateJobInfoStartTimeByJobID(Const.jobID);
                    HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook("sheet1",null);
                    int concurrent = 3;//线程条数控制
                    //int fileSize = 1;//每次获取数据的数量
                    ExecutorService executor = Executors.newCachedThreadPool();
                    final Semaphore semaphore = new Semaphore(concurrent);
                    for (int i = 0 ; i <= compareFileArr.size();i++) { //遍历所有图片文件
                        if (Const.stopFlg){
                            LogUtils.info("--------------系统停止-----------------");
                            return;
                        }
                        CompareFileModel compareFileModel = compareFileArr.get(i);
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
                        json.put("image1", imageReqInfoModelFrom);
                        json.put("image2", imageReqInfoModelTo);
                        json.put("settings", settingsModel);

                        ResultInfoModel resultInfoModel = new ResultInfoModel();
                        Future<ResultInfoModel> future = executor.submit(new PostThreadService(semaphore, json, resultInfoModel), resultInfoModel);
                        try {
                            ResultInfoModel resultInfoModel1 = future.get();

                            try {
                                compareFileModel.getFromFile().renameTo(new File( txt_new.getText() +"\\RESULT\\実行完了現エビデンス\\"+ compareFileModel.getToFile().getPath() + "\\" + compareFileModel.getToFile().getName()));
                                compareFileModel.getToFile().renameTo(new File( txt_new.getText() +"\\RESULT\\実行完了新エビデンス\\"+ compareFileModel.getToFile().getPath() + "\\" + compareFileModel.getToFile().getName()));
                                wb = ExcelUtil.setHSSFWorkbookValue("sheet1", wb, i, resultInfoModel1, compareFileModel, txt_new);
                                Const.kannseiNum = i;
                            }catch (Exception ex){
                                LogUtils.error(ex.getMessage());
                            }

                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        } catch (ExecutionException executionException) {
                            executionException.printStackTrace();
                        }

                    }
                    //响应到客户端
                    try {
                        FileOutputStream os = new FileOutputStream(txt_new.getText() +"\\RESULT\\比較結果レポート.xls");
                        wb.write(os);
                        os.flush();
                        os.close();
                    } catch (Exception ex) {
                        //ex.printStackTrace();
                        LogUtils.error(ex.getMessage());
                    }

                    // 退出线程池
                    executor.shutdown();
                    tblJobInfoService.updateJobInfoEndTimeByJobID(Const.jobID,compareFileArr.size());
                    firstFlg = false;
                }
            }
        });
        timer.start();

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
