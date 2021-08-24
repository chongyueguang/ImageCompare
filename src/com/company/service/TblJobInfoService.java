package com.company.service;

import com.company.dao.TblJobInfoDao;
import com.company.entity.TblJobInfoEntity;
import com.company.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

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
            //邮箱
            jTableInfoBeanVector.add(tblJobInfoEntity.getMail_address());
            //状态
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
            //预测时间
            if(tblJobInfoEntity.getStatus() != 1 ){
                jTableInfoBeanVector.add("0");
            }else {
                long nowTime = System.currentTimeMillis();
                long startTime = tblJobInfoEntity.getStart_time().getTime();
                long runTime = nowTime - startTime;
                long predictionEndTime = runTime/tblJobInfoEntity.getKannsei_num()*(tblJobInfoEntity.getGamen_num()-tblJobInfoEntity.getKannsei_num())/1000;
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                jTableInfoBeanVector.add(sdf.format(new Date(predictionEndTime)));
            }
            //完了比例
            jTableInfoBeanVector.add(tblJobInfoEntity.getKannsei_num()+"/"+tblJobInfoEntity.getGamen_num());
            //投入时间
            jTableInfoBeanVector.add(tblJobInfoEntity.getRegist_time().toString());
            //开始时间
            jTableInfoBeanVector.add(tblJobInfoEntity.getStart_time().toString());
            //更新时间
            jTableInfoBeanVector.add(tblJobInfoEntity.getUpdate_time().toString());

            jTableInfoBeanVector.add(jTableInfoBeanVector);
            jTableInfoBeans.add(jTableInfoBeanVector);
        }
        return  jTableInfoBeans;
    }

    public void updateJobInfoStatusByTime(){
        tblJobInfoDao = new TblJobInfoDao();
        List<TblJobInfoEntity> jobInfoByTbl = tblJobInfoDao.getJobInfoByTbl();
        for (TblJobInfoEntity tblJobInfoEntity:jobInfoByTbl) {
            long nowTime = System.currentTimeMillis();
            long updTime = tblJobInfoEntity.getUpdate_time().getTime();
            long runTime = nowTime - updTime;
            if (runTime/1000 > jobTblTimeOut && (tblJobInfoEntity.getStatus()==1)){
                boolean updFlg = tblJobInfoDao.updateJobTblToStart(tblJobInfoEntity.getJob_id());
                LogUtils.info("Job_id："+tblJobInfoEntity.getJob_id()+"运行时间过长，状态更新结果：" + updFlg);
            }
        }
    }

    public int insertJobInfoByJobID(String mailAddress,int totalCount){
        tblJobInfoDao = new TblJobInfoDao();
        int jobID = tblJobInfoDao.insertJobQueue(mailAddress, totalCount);
        return jobID;
    }

    public int getJobInfoByJobID(int job_id){
        tblJobInfoDao = new TblJobInfoDao();
        int status = tblJobInfoDao.getStatusByJobID(job_id);
        return status;
    }
}
