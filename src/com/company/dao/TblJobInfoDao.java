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
            PreparedStatement pstate = con.prepareStatement("SELECT t.* FROM tbl_job_info t where STATUS in (1,2)  order by JOB_ID desc;");
            rs = pstate.executeQuery();
            List<TblJobInfoEntity> list =new ArrayList<TblJobInfoEntity>();
            while (rs.next()) {
                TblJobInfoEntity entity = new TblJobInfoEntity();
                entity.setJob_id(rs.getInt("JOB_ID"));
                entity.setMail_address(rs.getString("MAIL_ADDRESS"));
                entity.setStatus(rs.getInt("STATUS"));
                entity.setGamen_num(rs.getInt("GAMEN_NUM"));
                entity.setKannsei_num(rs.getInt("KANNSEI_NUM"));
                if (rs.getTimestamp("REGIST_TIME") == null){
                    entity.setRegist_time(null);
                }else {
                    entity.setRegist_time(rs.getTimestamp("REGIST_TIME"));
                }
                if (rs.getTimestamp("START_TIME") == null){
                    entity.setStart_time(null);
                }else {
                    entity.setStart_time(rs.getTimestamp("START_TIME"));
                }
                if (rs.getTimestamp("END_TIME") == null){
                    entity.setEnd_time(null);
                }else {
                    entity.setEnd_time(rs.getTimestamp("END_TIME"));
                }
                if (rs.getTimestamp("UPDATE_TIME") == null){
                    entity.setUpdate_time(null);
                }else {
                    entity.setUpdate_time(rs.getTimestamp("UPDATE_TIME"));
                }
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
     * 待ち隊列を取得
     *
     * @return 待ち隊列
     */
    public List<Integer> getJobStatusListByJobID(int job_id) {
        Connection con = getConnection();
        if(con == null){
            return null;
        }
        ResultSet rs = null;
        try {
            PreparedStatement pstate = con.prepareStatement("SELECT t.STATUS FROM tbl_job_info t where job_id < ? ;");
            pstate.setInt(1,job_id);
            rs = pstate.executeQuery();
            List<Integer> list =new ArrayList<Integer>();
            while (rs.next()) {
                list.add(rs.getInt("STATUS"));
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
            PreparedStatement pstate = con.prepareStatement("SELECT t.STATUS FROM tbl_job_info t where job_id = ? ;");
            pstate.setInt(1,job_id);
            rs = pstate.executeQuery();
            int status = 0;
            while (rs.next()) {
                status = rs.getInt("STATUS");
            }

            return status;
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
    public boolean updateJobTblToInit(int jobTblTimeOut) {
        Connection con = getConnection();
        if(con == null){
            return false;
        }
        String sql = "UPDATE tbl_job_info SET `STATUS` = 4 WHERE STATUS in (1,2) and `UPDATE_TIME` <= date_sub(now(), interval " + jobTblTimeOut + " second);";
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
    public int insertJobQueue(String mailAdd, int totalCount,int insertFlg) {
        return insertJobQueue(getJobIDBySeq(), mailAdd, totalCount,insertFlg);
    }

    /**
     * 待ち隊列に入る
     *
     * @return -1:エラーがある　-1以外：取得したJOB_ID
     */
    private int insertJobQueue(int job_ID, String mailAdd, int totalCount,int insertFlg) {
        int runStatus = 1;
        if (insertFlg == 1){
            runStatus = 2;
        }
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
                        "  ?," +
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
                pstate.setInt(3, runStatus);
                pstate.setInt(4, totalCount);
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

    /**
     * 隊列中時、JOBテーブルを定期更新
     *
     * @return true:更新成功；false:更新エラー;
     */
    public boolean updataJobTblForTime(int kannSeiNum,int job_ID) {
        Connection con = getConnection();
        if(con == null){
            return false;
        }
        try {
            String sql = "UPDATE tbl_job_info SET `KANNSEI_NUM` = ?,`UPDATE_TIME` = now() WHERE `JOB_ID` = ?;";
            PreparedStatement pstate = con.prepareStatement(sql);
            pstate.setInt(1, kannSeiNum);
            pstate.setInt(2, job_ID);
            pstate.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
        try {
            PreparedStatement pstate = con.prepareStatement("UPDATE tbl_job_info" +
                    "   SET `STATUS` = 1," +
                    "       `START_TIME` = now()," +
                    "       `UPDATE_TIME` = now()" +
                    "   WHERE `JOB_ID` = ?;" +
                    "");
            pstate.setInt(1, job_ID);
            pstate.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * コンペア完成の時、jobテーブルを更新
     *
     * @return true:更新成功；false:更新エラー;
     */
    public boolean updateJobTblToEnd(int job_ID, int kannseiNum) {
        Connection con = getConnection();
        if(con == null){
            return false;
        }
        try {
            PreparedStatement pstate = con.prepareStatement("UPDATE tbl_job_info" +
                    "   SET `STATUS` = 3," +
                    "       `KANNSEI_NUM` = ?," +
                    "       `END_TIME` = now()," +
                    "       `UPDATE_TIME` = now()" +
                    "   WHERE `JOB_ID` = ?;" +
                    "");
            pstate.setInt(1, kannseiNum);
            pstate.setInt(2, job_ID);
            pstate.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止するとき、jobテーブルを更新
     *
     * @param kannseiNum (エラーの時が-1)
     * @return true:更新成功；false:更新エラー;
     */
    public boolean updateJobTblForStop(int job_ID, int kannseiNum) {
        Connection con = getConnection();
        if(con == null){
            return false;
        }
        try {
            String sql = "UPDATE tbl_job_info" +
                    "   SET `STATUS` = 4,";
            if (kannseiNum > 0) {
                sql += " `KANNSEI_NUM` = ?,";
            }
            sql += "  `END_TIME` = now()," +
                    "  `UPDATE_TIME` = now()" +
                    "   WHERE `JOB_ID` = ?;";
            PreparedStatement pstate = con.prepareStatement(sql);
            if (kannseiNum > 0) {
                pstate.setInt(1, kannseiNum);
                pstate.setInt(2, job_ID);
            } else {
                pstate.setInt(1, job_ID);
            }
            pstate.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
