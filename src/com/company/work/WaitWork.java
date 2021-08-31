package com.company.work;

import com.alibaba.fastjson.JSONObject;
import com.company.Const;
import com.company.model.*;
import com.company.service.PostThreadService;
import com.company.service.TblJobInfoService;
import com.company.util.ExcelUtil;
import com.company.util.FileUtils;
import com.company.util.ImageChangeUtils;
import com.company.util.LogUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
    private static boolean firstFlg = true;

    public WaitWork(ArrayList<CompareFileModel> compareFileArr, JTextField txt_new, JTextField txt_old) {
        this.compareFileArr = compareFileArr;
        this.txt_new = txt_new;
        this.txt_old = txt_old;
    }

    @Override
    protected Object doInBackground() throws Exception {
        LogUtils.info("等待线程启动");
        TblJobInfoService tblJobInfoService = new TblJobInfoService();
        boolean status = tblJobInfoService.getJobInfoStatus(Const.jobID);
        while (firstFlg && status) {
            tblJobInfoService.updateJobInfoStartTimeByJobID(Const.jobID);
            HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook("sheet1",null);
            int concurrent = 3;//线程条数控制
            ExecutorService executor = Executors.newCachedThreadPool();
            final Semaphore semaphore = new Semaphore(concurrent);
            List<Future<RunThreadResModel>> futures = new CopyOnWriteArrayList<>();
            for (CompareFileModel compareFileModel:compareFileArr) { //遍历所有图片文件
                LogUtils.info("--------------isCancelled()"+isCancelled()+"-----------------");
                if (isCancelled()){
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
                Future<RunThreadResModel> future;
                try {
                    future = executor.submit(new PostThreadService(semaphore, json, runThreadResModel,txt_new,txt_old), runThreadResModel);
                    futures.add(future);
                }catch (Exception e){
                    LogUtils.error("退出循环");
                    LogUtils.error(e.getMessage());
                    e.printStackTrace();
                    break;
                }
                futures.add(future);
            }
            //响应到客户端
            try {
                for (Future<RunThreadResModel> future : futures) {
                    int i = 0;
                    try {
                        if(isCancelled()){
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
            }finally {
                // 退出线程池
                executor.shutdown();
                tblJobInfoService.updateJobInfoEndTimeByJobID(Const.jobID,compareFileArr.size());
                firstFlg = false;
            }
        }
        return null;
    }
}
