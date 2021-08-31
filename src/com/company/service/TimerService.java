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
    //private static String waitTime;

    private static boolean firstFlg = true;

    static {
        try {
            InputStream in = com.company.MainForm.class.getClassLoader().getResourceAsStream("config.properties");
//            String confPath = System.getProperty("user.dir") + "\\config.properties";
//            InputStream in = new BufferedInputStream(new FileInputStream(confPath));
            Properties props = new Properties();
            props.load(in);
            refreshTime = props.getProperty("refreshTime");
            //waitTime = props.getProperty("waitTime");
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

}
