package com.company.model;

public class SettingsModel {
    private Integer confThres;
    private Integer iouThres;
    private Integer shiftThres;
    private Integer levdThres;

    public Integer getConfThres() {
        return confThres;
    }

    public void setConfThres(Integer confThres) {
        this.confThres = confThres;
    }

    public Integer getIouThres() {
        return iouThres;
    }

    public void setIouThres(Integer iouThres) {
        this.iouThres = iouThres;
    }

    public Integer getShiftThres() {
        return shiftThres;
    }

    public void setShiftThres(Integer shiftThres) {
        this.shiftThres = shiftThres;
    }

    public Integer getLevdThres() {
        return levdThres;
    }

    public void setLevdThres(Integer levdThres) {
        this.levdThres = levdThres;
    }
}
