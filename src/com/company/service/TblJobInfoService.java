package com.company.service;

import com.company.dao.TblJobInfoDao;
import com.company.entity.TblJobInfoEntity;
import com.company.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class TblJobInfoService {
    /* タイムアウト時間(単位：秒) */
    private final int jobTblTimeOut = 65;

    private TblJobInfoDao tblJobInfoDao;

    public Vector<Vector<Object>> getJobInfoByListToVector(){
        Vector<Vector<Object>> jTableInfoBeans = new Vector<>();
        tblJobInfoDao = new TblJobInfoDao();
        List<TblJobInfoEntity> jobInfoByTbl = tblJobInfoDao.getJobInfoByTbl();
        for (TblJobInfoEntity tblJobInfoEntity :jobInfoByTbl) {
            Vector<Object> jTableInfoBeanVector = new Vector<Object>();
            //メール
            jTableInfoBeanVector.add(tblJobInfoEntity.getMail_address());
            //ステータス
            switch (tblJobInfoEntity.getStatus()){
                case 1:
                    jTableInfoBeanVector.add("実行中");
                    break;
                case 2:
                    jTableInfoBeanVector.add("実行待ち");
                    break;
                case 3:
                    jTableInfoBeanVector.add("完了");
                    break;
                case 4:
                    jTableInfoBeanVector.add("エラー");
                    break;
            }
            //予測時間
            if(tblJobInfoEntity.getStatus() != 1 ){
                jTableInfoBeanVector.add("0");
            }else {
                if(tblJobInfoEntity.getKannsei_num() != 0){
                    long nowTime = System.currentTimeMillis();
                    long startTime = tblJobInfoEntity.getStart_time().getTime();
                    long runTime = nowTime - startTime;
                    long predictionEndTime = runTime/tblJobInfoEntity.getKannsei_num()*(tblJobInfoEntity.getGamen_num()-tblJobInfoEntity.getKannsei_num())/1000;
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    jTableInfoBeanVector.add(sdf.format(new Date(predictionEndTime)));
                }else {
                    jTableInfoBeanVector.add("");
                }
            }
            //完成率
            jTableInfoBeanVector.add(tblJobInfoEntity.getKannsei_num()+"/"+tblJobInfoEntity.getGamen_num());
            //Regist時間
            jTableInfoBeanVector.add(tblJobInfoEntity.getRegist_time().toString());
            //開始時間
            if(tblJobInfoEntity.getStart_time() != null){
                jTableInfoBeanVector.add(tblJobInfoEntity.getStart_time().toString());
            }else {
                jTableInfoBeanVector.add("");
            }
            //更新時間
            if(tblJobInfoEntity.getUpdate_time() != null){
                jTableInfoBeanVector.add(tblJobInfoEntity.getUpdate_time().toString());
            }else {
                jTableInfoBeanVector.add("");
            }

            jTableInfoBeanVector.add(jTableInfoBeanVector);
            jTableInfoBeans.add(jTableInfoBeanVector);
        }
        return  jTableInfoBeans;
    }

    public void updateJobInfoStatusByTime(){
        int runTime = 65;
        tblJobInfoDao = new TblJobInfoDao();
        boolean updFlg = tblJobInfoDao.updateJobTblToInit(runTime);
        LogUtils.info("実行時間が長すぎる，ステータス更新結果：" + updFlg);
    }

    public int insertJobInfoByJobID(String mailAddress,int totalCount){
        tblJobInfoDao = new TblJobInfoDao();
        int insertFlg = 0 ;
        List<TblJobInfoEntity> jobInfoByTbl = tblJobInfoDao.getJobInfoByTbl();
        for (TblJobInfoEntity tblJobInfoEntity :jobInfoByTbl){
            if(tblJobInfoEntity.getStatus() == 1 || tblJobInfoEntity.getStatus() == 2){
                insertFlg = 1;
            }
        }
        int jobID = tblJobInfoDao.insertJobQueue(mailAddress, totalCount,insertFlg);
        return jobID;
    }

    public int getJobInfoByJobID(int job_id){
        tblJobInfoDao = new TblJobInfoDao();
        int status = tblJobInfoDao.getStatusByJobID(job_id);
        return status;
    }

    public void updateJobInfoKannSeiByJobID(int job_id,int kannSeiNum){
        tblJobInfoDao = new TblJobInfoDao();
        tblJobInfoDao.updataJobTblForTime(kannSeiNum,job_id);
    }

    public void updateJobInfoStartTimeByJobID(int job_id){
        tblJobInfoDao = new TblJobInfoDao();
        tblJobInfoDao.updateJobTblToStart(job_id);
    }

    public void updateJobInfoEndTimeByJobID(int job_id,int kannSeiNum){
        tblJobInfoDao = new TblJobInfoDao();
        tblJobInfoDao.updateJobTblToEnd(job_id,kannSeiNum);
    }

    public void updateJobInfoEndTimeByJobIDForStop(int job_id,int kannSeiNum){
        tblJobInfoDao = new TblJobInfoDao();
        tblJobInfoDao.updateJobTblForStop(job_id,kannSeiNum);
    }

    public Boolean getJobInfoStatus(int job_id){
        tblJobInfoDao = new TblJobInfoDao();
        List<Integer> statusList = tblJobInfoDao.getJobStatusListByJobID(job_id);
        for (Integer status :statusList) {
            if(status == 1 || status == 2){
                try {
                    TimeUnit.SECONDS.sleep(5);
                    getJobInfoStatus(job_id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
