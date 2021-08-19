package com.company.model;

import java.util.List;

public class DiffDetailsModel {
	
	private Integer diff_id;
	
	private List<Integer> image1_id_list;
	
	private List<Integer> image2_id_list;

	public Integer getDiff_id() {
		return diff_id;
	}

	public void setDiff_id(Integer diff_id) {
		this.diff_id = diff_id;
	}

	public List<Integer> getImage1_id_list() {
		return image1_id_list;
	}

	public void setImage1_id_list(List<Integer> image1_id_list) {
		this.image1_id_list = image1_id_list;
	}

	public List<Integer> getImage2_id_list() {
		return image2_id_list;
	}

	public void setImage2_id_list(List<Integer> image2_id_list) {
		this.image2_id_list = image2_id_list;
	}
	
}
