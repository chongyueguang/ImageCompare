package com.company.dao;

import com.company.entity.TblJobInfoEntity;
import com.company.util.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TblJobInfoDao {

    private Connection con;
    /* タイムアウト時間(単位：秒) */
    private final int jobTblTimeOut = 65;
    /* 日付のフォーマット形式 */
    private final String dateFormat = "yyyy-MM-dd HH:mm:ss";

    public TblJobInfoDao() {
        this.con = JdbcUtils.getConnection();
    }

    /**
     * 待ち隊列を取得
     *
     * @return 待ち隊列
     */
    public List<TblJobInfoEntity> getJobInfoByTbl() {
        if(con == null){
            return null;
        }
        ResultSet rs = null;
        try {
            PreparedStatement pstate = con.prepareStatement("SELECT t.* FROM comparedb.tbl_job_info t order by JOB_ID desc;");
            rs = pstate.executeQuery();
            List<TblJobInfoEntity> list =new ArrayList<TblJobInfoEntity>();
            while (rs.next()) {
                TblJobInfoEntity entity = new TblJobInfoEntity();
                entity.setJob_id(rs.getInt("JOB_ID"));
                entity.setMail_address(rs.getString("MAIL_ADDRESS"));
                entity.setStatus(rs.getInt("STATUS"));
                entity.setGamen_num(rs.getInt("GAMEN_NUM"));
                entity.setKannsei_num(rs.getInt("KANNSEI_NUM"));
                Timestamp regTime = rs.getTimestamp("REGIST_TIME");
                entity.setRegist_time(regTime);
                Timestamp staTime = rs.getTimestamp("START_TIME");
                entity.setStart_time(staTime);
                Timestamp endTime = rs.getTimestamp("END_TIME");
                entity.setEnd_time(endTime);
                Timestamp updTime = rs.getTimestamp("UPDATE_TIME");
                entity.setUpdate_time(updTime);
                list.add(entity);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs == null) {
                } else {
                    rs.close();
                }
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
