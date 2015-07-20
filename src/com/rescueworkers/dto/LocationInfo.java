package com.rescueworkers.dto;

public class LocationInfo {
	String lattitude;
	String longtitude;
	String locationTime;
	String uuid;
	int uploadFlag; // 是否已上传

	public String getLattitude() {
		return lattitude;
	}

	public void setLattitude(String lattitude) {
		this.lattitude = lattitude;
	}

	public String getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getUploadFlag() {
		return uploadFlag;
	}

	public void setUploadFlag(int uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

	public String getLocationTime() {
		return locationTime;
	}

	public void setLocationTime(String locationTime) {
		this.locationTime = locationTime;
	}

}
