package com.blinkedup.transcription;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/** A custom Content Provider to do the database operations */
public class Recording extends ContentProvider{
	
	public static final String PROVIDER_NAME = "com.blinkedup.transcription.recording";
	
	/** A uri to do operations on cust_master table. A content provider is identified by its uri */
	public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/recordings" );
	
	/** Constants to identify the requested operation */
	private static final int RECORDINGS = 1;
	
	private static final UriMatcher uriMatcher ;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "recordings", RECORDINGS);
	}
	
	/** This content provider does the database operations by this object */
	RecordingDB mCustomerDB;
	
	/** A callback method which is invoked when the content provider is starting up */
	@Override
	public boolean onCreate() {
		mCustomerDB = new RecordingDB(getContext());
		return true;
	}	

		
	@Override
	public String getType(Uri uri) {		
		return null;
	}
	
	

	/** A callback method which is by the default content uri */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {	
		
		if(uriMatcher.match(uri)==RECORDINGS){
			return mCustomerDB.getAllCustomers();
		}else{			
			return null;
		}
	}


	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
/*

/////////////////
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
*/