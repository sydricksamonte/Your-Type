package com.blinkedup.transcription;

public class Recording {
			
	private int id;
	private String name;
	private String date_added;
	private String date_uploaded;
	private String duration;
	private int status;
	private int origin;
	private boolean isActive;
	private String fileType;
	private String date_finalized;

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	@Override
	public String toString() {
		return name;
	}
	
	public String getDateAdded() {
		return this.date_added;
	}

	public void setDateAdded(String date_added) {
		this.date_added = date_added;
	}
	
	public String getDateUploaded() {
		return this.date_uploaded;
	}

	public void setDateUploaded(String date_uploaded) {
		this.date_uploaded = date_uploaded;
	}
	
	public String getDuration() {
		return this.duration;
	}

	public void setDuration(String duration) {
		this.duration = name;
	}
	
	
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getOrigin() {
		return this.origin;
	}

	public void setOrigin(int origin) {
		this.origin = origin;
	}
	
	
	public boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public String getFileType() {
		return this.fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	public String getDateFinalized() {
		return this.date_finalized;
	}

	public void setIsActive(String date_finalized) {
		this.date_finalized = date_finalized;
	}
}
