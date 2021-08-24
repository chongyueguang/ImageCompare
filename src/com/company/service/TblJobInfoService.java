package com.company.service;

import com.company.bean.JTableInfoBean;
import com.company.dao.TblJobInfoDao;
import com.company.entity.TblJobInfoEntity;
import com.sun.corba.se.spi.ior.ObjectKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TblJobInfoService {

    public Vector<Vector<Object>> getJobInfoByList(){
        Vector<Vector<Object>> jTableInfoBeans = new Vector<>();

        TblJobInfoDao tblJobInfoDao = new TblJobInfoDao();
        List<TblJobInfoEntity> jobInfoByTbl = tblJobInfoDao.getJobInfoByTbl();
        for (TblJobInfoEntity tblJobInfoEntity :jobInfoByTbl) {
            Vector<Object> jTableInfoBeanVector = new Vector<Object>();
            //JTableInfoBean jTableInfoBean = new JTableInfoBean();
            //邮箱
            jTableInfoBeanVector.add(tblJobInfoEntity.getMail_address());
            //状态
            switch (tblJobInfoEntity.getStatus()){
                case 1:
                    jTableInfoBeanVector.add("実行中");
                case 2:
                    jTableInfoBeanVector.add("実行待ち");
                case 3:
                    jTableInfoBeanVector.add("完了");
                case 4:
                    jTableInfoBeanVector.add("エラー");
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
}
