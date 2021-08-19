package com.company.model;

public class ResultInfoModel {

    private ImageResponseModel data;
    private String message;


    public ImageResponseModel getData() {
		return data;
	}

	public void setData(ImageResponseModel data) {
		this.data = data;
	}

	public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
