package com.blinkedup.yourtype;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UpdateDB extends SQLiteOpenHelper{
	
	/** Database name */
	private static String DBNAME = "dbYourTypeTrans";
	
	/** Version number of the database */
	private static int VERSION = 6;
	
	public static final String UPDATE_ID = "_id";
	public static final String UPDATE_DATE_UPDATE = "_date_updated";
	public static final String UPDATE_REMAINING = "_remaining_sec";
	public static final String UPDATE_ISACTIVE = "_isActive";

    
    /** A constant, stores the the table name */
    private static final String DATABASE_TABLE = "Updates";
    
    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;  
    

    /** Constructor */
	public UpdateDB(Context context) {
		super(context, DBNAME, null, VERSION);	
		this.mDB = getWritableDatabase();
	}
	

	/** This is a callback method, invoked when the method 
	 * getReadableDatabase() / getWritableDatabase() is called 
	  * provided the database does not exists 
	* */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String DATABASE_CREATE = "create table " +  DATABASE_TABLE + "(" + UPDATE_ID + " integer PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ UPDATE_DATE_UPDATE + " date," + UPDATE_REMAINING + " integer," +
				UPDATE_ISACTIVE + " boolean );";
		
		db.execSQL(DATABASE_CREATE);
		Log.e("Updates Create",DATABASE_CREATE);
	}		
	
	/** Returns all the customers in the table */
	public Cursor getAllCustomers(){
        return mDB.query(DATABASE_TABLE, new String[] { UPDATE_ID,  UPDATE_DATE_UPDATE , UPDATE_REMAINING, UPDATE_ISACTIVE
        		 } , 
        		UPDATE_ISACTIVE + "= '1'", null, null, null, 
        		UPDATE_ID + " desc ");
	}

	
	public boolean insertUpdate(String date, int upRe) {
		
	    //  SQLiteDatabase db = this.getWritableDatabase();
	      ContentValues contentValues = new ContentValues();

	      contentValues.put(UPDATE_DATE_UPDATE, date);
	      contentValues.put(UPDATE_REMAINING, upRe);
	      contentValues.put(UPDATE_ISACTIVE, true);


	      mDB.insert(DATABASE_TABLE, null, contentValues);
	      return true;
	   }
	
	/*public boolean deleteRecordingPermanently(String id) {
		
		//  SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(DATABASE_TABLE, RECORDING_ID + "=" + id, null);
		return true;
	}*/
	/*
	public boolean deleteRecording(String id) {
	
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(RECORDING_ISACTIVE,0);

		db.update(DATABASE_TABLE, cv, RECORDING_ID +"="+id, null);
		return true;
	}
		*/
	/*
	public boolean renameRecording(String id, String name) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(RECORDING_NAME,name);

		db.update(DATABASE_TABLE, cv, RECORDING_ID +"="+id, null);
		return true;
	}
	*/
	/*
	public boolean updateRecordingUploadDate(String id, String date) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(RECORDING_DATE_UPLOADED,date);
		cv.put(RECORDING_STATUS, 1);
		
		db.update(DATABASE_TABLE, cv, RECORDING_ID +"="+id, null);
		return true;
	}
	*/
	/*
	public boolean updateRecordingFinalize(String id, String date) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(RECORDING_DATE_FINALIZED,date);
		cv.put(RECORDING_STATUS, 2);
		
		db.update(DATABASE_TABLE, cv, RECORDING_ID +"="+id, null);
		return true;
	}
	*/
	/*
	public int countNameDuplicate(String name) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor dataCount = db.rawQuery("select "+ RECORDING_NAME +" from " + DATABASE_TABLE + " WHERE "+ RECORDING_NAME +" = '"+ name+ "'", null);
		
		int count = dataCount.getCount();
		
		dataCount.close();
		
		return count;
    
	} 
	*/
	/*
	public int retrieveLastId() {
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("select "+ RECORDING_ID +" from " + DATABASE_TABLE + " ORDER BY "+ RECORDING_ID +" DESC LIMIT 1 ", null);
		
		int last = 0;
		
		try{
			cursor.moveToFirst();
			last = Integer.parseInt(cursor.getString(0));
		}
		catch (Exception e){
			last = 0;
		}
		cursor.close();
		return  last;
	} 
	*/
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub		
	}

}