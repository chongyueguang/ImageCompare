package com.company.work;

import com.alibaba.fastjson.JSONObject;
import com.company.Const;
import com.company.model.*;
import com.company.service.PostThreadService;
import com.company.service.TblJobInfoService;
import com.company.util.ExcelUtils;
import com.company.util.FileUtils;
import com.company.util.ImageChangeUtils;
import com.company.util.LogUtils;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class WaitWork extends SwingWorker {
    private ArrayList<CompareFileModel> compareFileArr;
    private JTextField txt_new;
    private JTextField txt_old;

    public WaitWork(ArrayList<CompareFileModel> compareFileArr, JTextField txt_new, JTextField txt_old) {
        this.compareFileArr = compareFileArr;
        this.txt_new = txt_new;
        this.txt_old = txt_old;
    }

    @Override
    protected Object doInBackground() throws Exception {
        LogUtils.info("wait thread start");
        TblJobInfoService tblJobInfoService = new TblJobInfoService();
        boolean status = tblJobInfoService.getJobInfoStatus(Const.jobID);
        if (status) {
            //tblJobInfoService.updateJobInfoStartTimeByJobID(Const.jobID);
            Const.wb = ExcelUtils.getHSSFWorkbook("sheet1",Const.wb);
            //スレッドカウント制御
            int concurrent = 3;
            ExecutorService executor = Executors.newCachedThreadPool();
            final Semaphore semaphore = new Semaphore(concurrent);
            List<Future<RunThreadResModel>> futures = new CopyOnWriteArrayList<>();
            //すべての画像ファイルをトラバースします
            for (CompareFileModel compareFileModel:compareFileArr) {
                if (isCancelled()){
                    LogUtils.info("--------------system stop-----------------");
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
                Future<RunThreadResModel> future = null;
                try {
                    future = executor.submit(new PostThreadService(semaphore, json, runThreadResModel,txt_new,txt_old), runThreadResModel);
                    //futures.add(future);
                }catch (Exception e){
                    LogUtils.error(e.getMessage());
                    e.printStackTrace();
                    break;
                }finally {
                    futures.add(future);
                }
            }
            //クライアントに応答する
            try {
                int i = 0;
                for (Future<RunThreadResModel> future : futures) {
                    try {
                        if(!isCancelled()){
                            RunThreadResModel runThreadResModel = future.get();
                            ResultInfoModel resultInfoModel1 = runThreadResModel.getResultInfoModel();
                            if(resultInfoModel1 != null){
                                CompareFileModel compareFileModel = runThreadResModel.getCompareFileModel();
                                try {
                                    Const.wb = ExcelUtils.setHSSFWorkbookValue("sheet1", Const.wb, i, resultInfoModel1, compareFileModel, txt_new,txt_old);
                                    i++;
                                    Const.kannseiNum = i;
                                }catch (Exception ex){
                                    LogUtils.error(ex.getMessage());
                                    ex.printStackTrace();
                                }
                            }
                        }

                    } catch (InterruptedException interruptedException) {
                        LogUtils.error(interruptedException.getMessage());
                        interruptedException.printStackTrace();
                    } catch (ExecutionException executionException) {
                        LogUtils.error(executionException.getMessage());
                        executionException.printStackTrace();
                    }
                }
                if(!isCancelled()){
                    //日付オブジェクトを作成する
                    Date date1 = new Date();
                    DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
                    FileOutputStream os = new FileOutputStream(txt_new.getText() +"\\RESULT\\比較結果レポート"+format.format(date1)+".xls");
                    Const.wb.write(os);
                    os.flush();
                    os.close();
                    FileUtils.deleteFolder(new File(txt_new.getText() +"\\RESULT\\TEMPOLD"));
                    FileUtils.deleteFolder(new File(txt_new.getText() +"\\RESULT\\TEMPNEW"));
                    Const.successRunFlg = 2;
                }
            } catch (Exception ex) {
                LogUtils.error(ex.getMessage());
                ex.printStackTrace();
            }finally {
                // スレッドプールを終了します
                executor.shutdown();
                tblJobInfoService.updateJobInfoEndTimeByJobID(Const.jobID,compareFileArr.size());
            }
        }
        return null;
    }
}
