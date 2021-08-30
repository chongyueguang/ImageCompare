package com.company.service;

import com.alibaba.fastjson.JSONObject;
import com.company.Const;
import com.company.model.*;
import com.company.service.TblJobInfoService;
import com.company.util.ExcelUtil;
import com.company.util.FileUtils;
import com.company.util.ImageChangeUtils;
import com.company.util.LogUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class TimerService {

    // 画面更新時間
    private static String refreshTime;
    // リクエスト更新時間
    private static String waitTime;

    private static boolean firstFlg = true;

    public TimerService() {
        super();
        try {
            InputStream in = com.company.MainForm.class.getClassLoader().getResourceAsStream("config.properties");
//            String confPath = System.getProperty("user.dir") + "\\config.properties";
//            InputStream in = new BufferedInputStream(new FileInputStream(confPath));
            Properties props = new Properties();
            props.load(in);
            refreshTime = props.getProperty("refreshTime");
            waitTime = props.getProperty("waitTime");
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void refreshScreenTimer(JScrollPane jScrollPane, JTable jTable, Vector vectorHeader) {

        new Timer(Integer.parseInt(refreshTime),new ActionListener(){
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

    public static void waitTimer(ArrayList<CompareFileModel> compareFileArr,JTextField txt_new,JTextField txt_old){
        Timer timer = new Timer(Integer.parseInt(waitTime), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogUtils.info("等待线程启动");
                TblJobInfoService tblJobInfoService = new TblJobInfoService();
                boolean status = tblJobInfoService.getJobInfoStatus(Const.jobID);
                if (firstFlg && status) {
                    tblJobInfoService.updateJobInfoStartTimeByJobID(Const.jobID);
                    HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook("sheet1",null);
                    int concurrent = 3;//线程条数控制
                    ExecutorService executor = Executors.newCachedThreadPool();
                    final Semaphore semaphore = new Semaphore(concurrent);
                    List<Future<RunThreadResModel>> futures = new CopyOnWriteArrayList<>();
                    for (CompareFileModel compareFileModel:compareFileArr) { //遍历所有图片文件
                        if (Const.stopFlg){
                            LogUtils.info("--------------系统停止-----------------");
                            break;
                        }
                        //对图片文件进行转码
                        String imageBase64From = null;
                        String imageBase64To = null;
                        try {
                            imageBase64From = ImageChangeUtils.imageToBase64ByFile(compareFileModel.getFromFile());
                            imageBase64To = ImageChangeUtils.imageToBase64ByFile(compareFileModel.getToFile());
                        } catch (IOException ioException) {
                            LogUtils.error(ioException.getMessage());
                            ioException.printStackTrace();
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

                        RunThreadResModel runThreadResModel = new RunThreadResModel();
                        runThreadResModel.setCompareFileModel(compareFileModel);
                        Future<RunThreadResModel> future = executor.submit(new PostThreadService(semaphore, json, runThreadResModel), runThreadResModel);
                        futures.add(future);
                        String oldFolder = txt_old.getText();
                        oldFolder = oldFolder.substring(oldFolder.lastIndexOf("\\"),oldFolder.length()) ;
                        String newFolder = txt_new.getText();
                        newFolder = newFolder.substring(newFolder.lastIndexOf("\\"),newFolder.length()) ;
                        compareFileModel.getFromFile().renameTo(new File( txt_new.getText() +"\\RESULT\\"+oldFolder+"\\"+ compareFileModel.getKey()));
                        compareFileModel.getToFile().renameTo(new File( txt_new.getText() +"\\RESULT\\"+newFolder+"\\"+ compareFileModel.getKey()));

                    }
                    //响应到客户端
                    try {
                        for (Future<RunThreadResModel> future : futures) {
                            int i = 0;
                            try {
                                RunThreadResModel runThreadResModel = future.get();
                                ResultInfoModel resultInfoModel1 = runThreadResModel.getResultInfoModel();
                                CompareFileModel compareFileModel = runThreadResModel.getCompareFileModel();
                                try {
                                    wb = ExcelUtil.setHSSFWorkbookValue("sheet1", wb, i, resultInfoModel1, compareFileModel, txt_new,txt_old);
                                    i++;
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
                        Date date1 = new Date();	//创建一个date对象
                        DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss"); //定义格式
                        FileOutputStream os = new FileOutputStream(txt_new.getText() +"\\RESULT\\比較結果レポート"+format.format(date1)+".xls");
                        //OutputStreamWriter osr = new OutputStreamWriter(new FileOutputStream(txt_new.getText() + "\\RESULT\\比較結果レポート" + format.format(date1) + ".xls"), "utf-8");
                        wb.write(os);
                        os.flush();
                        os.close();
                        FileUtils.deleteFolder(new File(txt_new.getText() +"\\RESULT\\TEMPOLD"));
                        FileUtils.deleteFolder(new File(txt_new.getText() +"\\RESULT\\TEMPNEW"));
                    } catch (Exception ex) {
                        LogUtils.error(ex.getMessage());
                        ex.printStackTrace();
                    }

                    // 退出线程池
                    executor.shutdown();
                    tblJobInfoService.updateJobInfoEndTimeByJobID(Const.jobID,compareFileArr.size());
                    firstFlg = false;
                }
            }
        });
        timer.start();
    }

}
