package com.company.model;

import java.util.List;

public class ImageResultModel {
	
	private Integer width;

	private Integer height;
	
	private List<DetectionsModel> detections;

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public List<DetectionsModel> getDetections() {
		return detections;
	}

	public void setDetections(List<DetectionsModel> detections) {
		this.detections = detections;
	}


}
