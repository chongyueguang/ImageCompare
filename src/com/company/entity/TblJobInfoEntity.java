package com.company.entity;

import java.sql.Timestamp;

public class TblJobInfoEntity {
    private int job_id;
    private String mail_address;
    private int status;
    private int gamen_num;
    private int kannsei_num;
    private Timestamp regist_time;
    private Timestamp start_time;
    private Timestamp end_time;
    private Timestamp update_time;

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public String getMail_address() {
        return mail_address;
    }

    public void setMail_address(String mail_address) {
        this.mail_address = mail_address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGamen_num() {
        return gamen_num;
    }

    public void setGamen_num(int gamen_num) {
        this.gamen_num = gamen_num;
    }

    public int getKannsei_num() {
        return kannsei_num;
    }

    public void setKannsei_num(int kannsei_num) {
        this.kannsei_num = kannsei_num;
    }

    public Timestamp getRegist_time() {
        return regist_time;
    }

    public void setRegist_time(Timestamp regist_time) {
        this.regist_time = regist_time;
    }

    public Timestamp getStart_time() {
        return start_time;
    }

    public void setStart_time(Timestamp start_time) {
        this.start_time = start_time;
    }

    public Timestamp getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Timestamp end_time) {
        this.end_time = end_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }
}
