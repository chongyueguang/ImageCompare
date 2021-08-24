package com.company.bean;

import java.util.Date;
import java.sql.Timestamp;

public class JTableInfoBean {
    //邮箱
    private String mailAddress;
    //状态
    private String status;
    //预测时间
    private String predictionEndTime;
    //完了比例
    private String completionRatio;
    //投入时间
    private String registTime;
    //开始时间
    private String startTime;
    //更新时间
    private String updateTime;

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPredictionEndTime() {
        return predictionEndTime;
    }

    public void setPredictionEndTime(String predictionEndTime) {
        this.predictionEndTime = predictionEndTime;
    }

    public String getCompletionRatio() {
        return completionRatio;
    }

    public void setCompletionRatio(String completionRatio) {
        this.completionRatio = completionRatio;
    }

    public String getRegistTime() {
        return registTime;
    }

    public void setRegistTime(String registTime) {
        this.registTime = registTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
