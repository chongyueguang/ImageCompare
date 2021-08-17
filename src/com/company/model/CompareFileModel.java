package com.company.model;

import java.io.File;

public class CompareFileModel {
    private String key;
    private File fromFile;
    private File toFile;

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
}
