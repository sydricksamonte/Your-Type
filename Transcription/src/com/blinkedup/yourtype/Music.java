package com.blinkedup.yourtype;

public class Music {
	
	private int musicID;
	private String musicName;
	private String fullPath;
	private String album;
	private String artistName;
	private int duration;
	private int fileSize;

	public void setMusicID(int musicID){
		this.musicID = musicID;
	}
	
	public int getMusicID(){
		return this.musicID;
	}
	
	public void setmusicName(String musicName){
		this.musicName = musicName;
	}
	
	public String getMusicName(){
		return this.musicName;
	}
	
	public void setAlbum(String album){
		this.album =album;
		
	}
	
	public String getAlbum(String album){
		return this.album;
		
	}
	
	public void setFullPath(String fullPath){
		this.fullPath = fullPath;
	}
	
	public String getFullPath(){
		return this.fullPath;
	}
	
	public void setArtistName(String artistName){
		this.artistName = artistName;
	}
	
	public String getArtistName(){
		return  this.artistName;
	}
	
	public void setDuration(int duration){
		this.duration = duration;
	}
	
	public int getDuration(){
		return this.duration;
	}
	
	public void setFileSize(int fileSize){
		this.fileSize = fileSize;
	}
	
	public int getFileSize(){
		return this.fileSize;
	}
	
	
}
