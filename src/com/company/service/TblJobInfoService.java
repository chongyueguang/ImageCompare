package com.company.service;

import com.company.bean.JTableInfoBean;
import com.company.dao.TblJobInfoDao;
import com.company.entity.TblJobInfoEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TblJobInfoService {

    public Vector<JTableInfoBean> getJobInfoByList(){
        Vector<JTableInfoBean> jTableInfoBeans = new Vector<>();
        TblJobInfoDao tblJobInfoDao = new TblJobInfoDao();
        List<TblJobInfoEntity> jobInfoByTbl = tblJobInfoDao.getJobInfoByTbl();
        for (TblJobInfoEntity tblJobInfoEntity :jobInfoByTbl) {
            JTableInfoBean jTableInfoBean = new JTableInfoBean();
            //邮箱
            jTableInfoBean.setMailAddress(tblJobInfoEntity.getMail_address());
            //状态
            switch (tblJobInfoEntity.getStatus()){
                case 1:
                    jTableInfoBean.setStatus("実行中");
                case 2:
                    jTableInfoBean.setStatus("実行待ち");
                case 3:
                    jTableInfoBean.setStatus("完了");
                case 4:
                    jTableInfoBean.setStatus("エラー");
            }
            //预测时间
            if(tblJobInfoEntity.getStatus() != 2 ){
                jTableInfoBean.setPredictionEndTime("0");
            }else {
                long nowTime = System.currentTimeMillis();
                long startTime = tblJobInfoEntity.getStart_time().getTime();
                long runTime = nowTime - startTime;
                long predictionEndTime = runTime/tblJobInfoEntity.getKannsei_num()*(tblJobInfoEntity.getGamen_num()-tblJobInfoEntity.getKannsei_num())/1000;
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                jTableInfoBean.setPredictionEndTime(sdf.format(new Date(predictionEndTime)));
            }
            //完了比例
            jTableInfoBean.setCompletionRatio(tblJobInfoEntity.getKannsei_num()+"/"+tblJobInfoEntity.getGamen_num());
            //投入时间
            jTableInfoBean.setRegistTime(tblJobInfoEntity.getRegist_time().toString());
            //开始时间
            jTableInfoBean.setStartTime(tblJobInfoEntity.getStart_time().toString());
            //更新时间
            jTableInfoBean.setUpdateTime(tblJobInfoEntity.getUpdate_time().toString());

            jTableInfoBeans.add(jTableInfoBean);
        }
        return  jTableInfoBeans;
    }
}
