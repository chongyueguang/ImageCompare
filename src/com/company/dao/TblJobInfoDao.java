package com.company.dao;

import com.company.entity.TblJobInfoEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TblJobInfoDao extends JdbcBaseDao {

    /* 日付のフォーマット形式 */
    private final String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 待ち隊列を取得
     *
     * @return 待ち隊列
     */
    public List<TblJobInfoEntity> getJobInfoByTbl() {
        Connection con = getConnection();
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

    /**
     * ステータスを取得
     *
     * @return
     */
    public int getStatusByJobID(int job_id) {
        Connection con = getConnection();
        if(con == null){
            return -1;
        }
        ResultSet rs = null;
        try {
            PreparedStatement pstate = con.prepareStatement("SELECT t.STATUS FROM comparedb.tbl_job_info t where job_id = ? ;");
            pstate.setInt(1,job_id);
            rs = pstate.executeQuery();

            return rs.getInt("STATUS");
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
        return -1;
    }

    /**
     * コンペア開始の時、jobテーブルを更新
     *
     * @return true:更新成功；false:更新エラー;
     */
    public boolean updateJobTblToStart(int job_ID) {
        Connection con = getConnection();
        if(con == null){
            return false;
        }
        String sql = "UPDATE tbl_job_info SET `STATUS` = 4 WHERE STATUS = 1;";
        try {
            PreparedStatement pstate = con.prepareStatement(sql);
            pstate.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Job_IDを取得
     *
     * @return -1:エラーがある　-1以外：取得したJOB_ID
     */
    private int getJobIDBySeq() {
        Connection con = getConnection();
        if(con == null){
            return 0;
        }
        ResultSet rs = null;
        try {
            PreparedStatement pstate = con.prepareStatement("select nextval();");
            rs = pstate.executeQuery();
            rs.next();
            return rs.getInt("nextval()");
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
        return -1;
    }

    /**
     * 待ち隊列に入る(job_ID不要)
     *
     * @return -1:エラーがある　-1以外：取得したJOB_ID
     */
    public int insertJobQueue(String mailAdd, int totalCount) {
        return insertJobQueue(getJobIDBySeq(), mailAdd, totalCount);
    }

    /**
     * 待ち隊列に入る
     *
     * @return -1:エラーがある　-1以外：取得したJOB_ID
     */
    private int insertJobQueue(int job_ID, String mailAdd, int totalCount) {
        Connection con = getConnection();
        if(con == null){
            return 0;
        }
        ResultSet rs = null;
        try {
            if (job_ID > 0) {
                PreparedStatement pstate = con.prepareStatement("INSERT INTO tbl_job_info" +
                        "(" +
                        "  `JOB_ID`," +
                        "  `MAIL_ADDRESS`," +
                        "  `STATUS`," +
                        "  `GAMEN_NUM`," +
                        "  `KANNSEI_NUM`," +
                        "  `REGIST_TIME`," +
                        "  `START_TIME`," +
                        "  `END_TIME`," +
                        "  `UPDATE_TIME`" +
                        ")" +
                        "VALUES" +
                        "(" +
                        "  ?," +
                        "  ?," +
                        "  2," +
                        "  ?," +
                        "  0," +
                        "  now()," +
                        "  NULL," +
                        "  NULL," +
                        "  now()" +
                        ");" +
                        " ");
                pstate.setInt(1, job_ID);
                pstate.setString(2, mailAdd);
                pstate.setInt(3, totalCount);
                pstate.execute();
                return job_ID;
            }
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
        return -1;
    }
}
