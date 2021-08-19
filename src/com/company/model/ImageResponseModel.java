package com.company.model;

import java.util.List;

public class ImageResponseModel {
	
	private List<ImageResultModel> image1Result;
	
	private List<ImageResultModel> image2Result;
	
	private StatisticModel statistic;
	
	private String diffResult;
	
	private String diffImage1;

	private String diffImage2;
	
	private List<DiffDetailsModel> diffDetails;

	public List<ImageResultModel> getImage1Result() {
		return image1Result;
	}

	public void setImage1Result(List<ImageResultModel> image1Result) {
		this.image1Result = image1Result;
	}

	public List<ImageResultModel> getImage2Result() {
		return image2Result;
	}

	public void setImage2Result(List<ImageResultModel> image2Result) {
		this.image2Result = image2Result;
	}

	public StatisticModel getStatistic() {
		return statistic;
	}

	public void setStatistic(StatisticModel statistic) {
		this.statistic = statistic;
	}

	public String getDiffResult() {
		return diffResult;
	}

	public void setDiffResult(String diffResult) {
		this.diffResult = diffResult;
	}

	public String getDiffImage1() {
		return diffImage1;
	}

	public void setDiffImage1(String diffImage1) {
		this.diffImage1 = diffImage1;
	}

	public String getDiffImage2() {
		return diffImage2;
	}

	public void setDiffImage2(String diffImage2) {
		this.diffImage2 = diffImage2;
	}

	public List<DiffDetailsModel> getDiffDetails() {
		return diffDetails;
	}

	public void setDiffDetails(List<DiffDetailsModel> diffDetails) {
		this.diffDetails = diffDetails;
	}

}
