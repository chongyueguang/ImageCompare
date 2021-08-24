package com.company.model;

import java.io.File;
import java.util.List;

public class ImageAttributeModel {
    private File file;

    private List<ImageModel> imageModel;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<ImageModel> getImageModel() {
        return imageModel;
    }

    public void setImageModel(List<ImageModel> imageModel) {
        this.imageModel = imageModel;
    }
}
