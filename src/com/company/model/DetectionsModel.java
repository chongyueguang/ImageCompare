package com.company.model;

public class DetectionsModel {
	
	private Integer detectId;
	
	private String ocr;
	
	private ImageModel coordinates;

	public Integer getDetectId() {
		return detectId;
	}

	public void setDetectId(Integer detectId) {
		this.detectId = detectId;
	}

	public String getOcr() {
		return ocr;
	}

	public void setOcr(String ocr) {
		this.ocr = ocr;
	}

	public ImageModel getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(ImageModel coordinates) {
		this.coordinates = coordinates;
	}
	
}
