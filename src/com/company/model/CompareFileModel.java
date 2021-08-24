package com.company.model;

import java.io.File;
import java.util.List;

public class CompareFileModel {
    private String key;
    private File fromFile;
    private File toFile;
    private List<ImageModel> fromImageModel;
    private List<ImageModel> toImageModel;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public File getFromFile() {
        return fromFile;
    }

    public void setFromFile(File fromFile) {
        this.fromFile = fromFile;
    }

    public File getToFile() {
        return toFile;
    }

    public void setToFile(File toFile) {
        this.toFile = toFile;
    }

    public List<ImageModel> getFromImageModel() {
        return fromImageModel;
    }

    public void setFromImageModel(List<ImageModel> fromImageModel) {
        this.fromImageModel = fromImageModel;
    }

    public List<ImageModel> getToImageModel() {
        return toImageModel;
    }

    public void setToImageModel(List<ImageModel> toImageModel) {
        this.toImageModel = toImageModel;
    }
}
