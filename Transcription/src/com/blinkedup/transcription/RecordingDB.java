package com.blinkedup.transcription;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RecordingDB extends SQLiteOpenHelper{
	
	/** Database name */
	private static String DBNAME = "dbYourTypeTrans";
	
	/** Version number of the database */
	private static int VERSION = 6;

	public static final String RECORDING_ID = "_id";
	public static final String RECORDING_NAME = "_name";
	public static final String RECORDING_DATE_ADDED = "_date_added";
	public static final String RECORDING_DATE_UPLOADED = "_date_uploaded";
	public static final String RECORDING_DURATION = "_duration";
	public static final String RECORDING_STATUS = "_status";
	public static final String RECORDING_ORIGIN = "_origin";
	public static final String RECORDING_ISACTIVE = "_isActive";
	public static final String RECORDING_FILE_TYPE = "_fileType";
	public static final String RECORDING_DATE_FINALIZED = "_date_finalized";
	public static final String RECORDING_PATH = "_path_loc";
    
    /** A constant, stores the the table name */
    private static final String DATABASE_TABLE = "Recordings";
    
    public static final String UPDATE_ID = "_id";
	public static final String UPDATE_DATE_UPDATE = "_date_updated";
	public static final String UPDATE_REMAINING = "_remaining_sec";
	public static final String UPDATE_ISACTIVE = "_isActive";
	
	  /** A constant, stores the the table name */
    private static final String DATABASE_TABLE_2 = "Updates";
    
    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;  
    DateUtils dateFunc;

    /** Constructor */
	public RecordingDB(Context context) {
		super(context, DBNAME, null, VERSION);	
		this.mDB = getWritableDatabase();
	}
	

	/** This is a callback method, invoked when the method 
	 * getReadableDatabase() / getWritableDatabase() is called 
	  * provided the database does not exists 
	* */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String DATABASE_CREATE = "create table " +  DATABASE_TABLE + "(" + RECORDING_ID + " integer PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ RECORDING_NAME + " text," + RECORDING_DATE_ADDED + " date," +
				RECORDING_DATE_UPLOADED + " date," +RECORDING_DURATION + " text," +
				RECORDING_STATUS + " integer," +RECORDING_ORIGIN + " integer," +
				RECORDING_ISACTIVE + " boolean," +RECORDING_FILE_TYPE + " text," +
				RECORDING_DATE_FINALIZED + " date,"+ RECORDING_PATH + " text);";
		
		db.execSQL(DATABASE_CREATE);
		Log.e("HERE",DATABASE_CREATE);
		
		String DATABASE_CREATE_2 = "create table " +  DATABASE_TABLE_2 + "(" + UPDATE_ID + " integer PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ UPDATE_DATE_UPDATE + " date," + UPDATE_REMAINING + " integer," +
				UPDATE_ISACTIVE + " boolean );";
		
		db.execSQL(DATABASE_CREATE_2);
		
		dateFunc = new DateUtils();
		String strDate = dateFunc.getDate();
		String DATABASE_INSERT_DEFAULT = "INSERT INTO " +  DATABASE_TABLE_2 + "(" 
				+ UPDATE_DATE_UPDATE + " ," + UPDATE_REMAINING + " ," +
				UPDATE_ISACTIVE + "  ) VALUES  ("+ "'"+strDate+"'" + "," + 0 + " ," +
				1 + "  );";
		
		db.execSQL(DATABASE_INSERT_DEFAULT);
		
		Log.e("Updates Create",DATABASE_CREATE_2);
		
	}		
	
	/** Returns all the customers in the table */
	public Cursor getAllCustomers(){
        return mDB.query(DATABASE_TABLE, new String[] { RECORDING_ID,  RECORDING_NAME , RECORDING_DATE_ADDED, RECORDING_DATE_UPLOADED
        		, RECORDING_DURATION, RECORDING_STATUS, RECORDING_ORIGIN, RECORDING_ISACTIVE, RECORDING_FILE_TYPE, RECORDING_DATE_FINALIZED,  RECORDING_PATH  } , 
        		RECORDING_ISACTIVE + "= '1'", null, null, null, 
        		RECORDING_DATE_ADDED + " desc ");
	}

	public void addRecording(String name, String dateAdded, String dateUploaded, int duration, int status, int origin, boolean isActive, String fileType, String dateFinalized, String path) {
		// TODO Auto-generated method stub
		String query_sql = "INSERT INTO "+ DATABASE_TABLE + "(" + RECORDING_ID + ", "
        		+ RECORDING_NAME + "," + RECORDING_DATE_ADDED + " ," +
        		RECORDING_DATE_UPLOADED + " ," +RECORDING_DURATION + " ," +
        		RECORDING_STATUS + " ," + RECORDING_ORIGIN + " ," +
        		RECORDING_ISACTIVE + " ," + RECORDING_FILE_TYPE + " ," +
        		RECORDING_DATE_FINALIZED + " ," + RECORDING_PATH + ") VALUES ("+name+", "+dateAdded+", "+dateUploaded+","+duration+", "+status+", "+origin+", "+isActive+", "+fileType+", "+dateFinalized+", "+path+");";
              
        		mDB.execSQL(query_sql);
	}
	
	public boolean insertRecording(String name, String dateAdded, String dateUploaded, int duration, int status, int origin, boolean isActive, String fileType, String dateFinalized, String path) {
		
	    //  SQLiteDatabase db = this.getWritableDatabase();
	      ContentValues contentValues = new ContentValues();

	      contentValues.put(RECORDING_NAME, name);
	      contentValues.put(RECORDING_DATE_ADDED, dateAdded);
	      contentValues.put(RECORDING_DATE_UPLOADED, dateUploaded);	
	      contentValues.put(RECORDING_DURATION, duration);
	      contentValues.put(RECORDING_STATUS, status);
	      contentValues.put(RECORDING_ORIGIN, origin);
	      contentValues.put(RECORDING_ISACTIVE, isActive);
	      contentValues.put(RECORDING_FILE_TYPE, fileType);
	      contentValues.put(RECORDING_DATE_FINALIZED, dateFinalized);
	      contentValues.put(RECORDING_PATH, path);

	      mDB.insert(DATABASE_TABLE, null, contentValues);
	      return true;
	   }
	
	/*public boolean deleteRecordingPermanently(String id) {
		
		//  SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(DATABASE_TABLE, RECORDING_ID + "=" + id, null);
		return true;
	}*/
	
	public boolean deleteRecording(String id) {
	
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(RECORDING_ISACTIVE,0);

		db.update(DATABASE_TABLE, cv, RECORDING_ID +"="+id, null);
		return true;
	}
		
	public boolean renameRecording(String id, String name) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(RECORDING_NAME,name);

		db.update(DATABASE_TABLE, cv, RECORDING_ID +"="+id, null);
		return true;
	}
	
	public boolean updateRecordingUploadDate(String id, String date) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(RECORDING_DATE_UPLOADED,date);
		cv.put(RECORDING_STATUS, 1);
		
		db.update(DATABASE_TABLE, cv, RECORDING_ID +"="+id, null);
		return true;
	}
	
	public boolean updateRecordingFinalize(String id, String date) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(RECORDING_DATE_FINALIZED,date);
		cv.put(RECORDING_STATUS, 2);
		
		db.update(DATABASE_TABLE, cv, RECORDING_ID +"="+id, null);
		return true;
	}
	
	public int countNameDuplicate(String name) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor dataCount = db.rawQuery("select "+ RECORDING_NAME +" from " + DATABASE_TABLE + " WHERE "+ RECORDING_NAME +" = '"+ name+ "'", null);
		
		int count = dataCount.getCount();
		
		dataCount.close();
		
		return count;
    
	} 
	
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
	
	
	public String StripText(String name){
		name = name.replace("'", "");
			name = "["+name+"]";
		try{
			name = name.replaceAll("[\\/:!@#$%^&*()<>+?\\\\\"{}\\[\\]=`~;]", "");
			
		}
		catch(Exception e){
			
		}
		Log.e("aedasd",name);
		return name;
	}
	
	/** Returns all the customers in the table */
	public Cursor getAllUpdate(){
        return mDB.query(DATABASE_TABLE_2, new String[] { UPDATE_ID,  UPDATE_DATE_UPDATE , UPDATE_REMAINING, UPDATE_ISACTIVE
        		 } , 
        		UPDATE_ISACTIVE + "= '1'", null, null, null, 
        		UPDATE_ID + " desc ");
	}

	public int addToCredits(int add) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("select SUM("+ UPDATE_REMAINING +") from " + DATABASE_TABLE_2 + " WHERE "+ UPDATE_ISACTIVE +" = '1' ", null);
		
		int last = 0;
		
		try{
			cursor.moveToFirst();
			last = Integer.parseInt(cursor.getString(0)) + add;
		}
		catch (Exception e){
						
			last = 0;
		}
		cursor.close();
		return  last;
	}
	
	public boolean insertUpdate(String date, int upRe) {
		
	    //  SQLiteDatabase db = this.getWritableDatabase();
	      ContentValues contentValues = new ContentValues();

	      contentValues.put(UPDATE_DATE_UPDATE, date);
	      contentValues.put(UPDATE_REMAINING, upRe);
	      contentValues.put(UPDATE_ISACTIVE, true);

	      mDB.update(DATABASE_TABLE_2, contentValues, UPDATE_ID +"="+1, null);
	      return true;
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub		
	}

}