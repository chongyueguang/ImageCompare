package com.company.model;

import java.util.List;

public class ImageReqInfoModel {
    private String data;
    private List<ImageModel> ignoreAreas;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<ImageModel> getIgnoreAreas() {
        return ignoreAreas;
    }

    public void setIgnoreAreas(List<ImageModel> ignoreAreas) {
        this.ignoreAreas = ignoreAreas;
    }
}
