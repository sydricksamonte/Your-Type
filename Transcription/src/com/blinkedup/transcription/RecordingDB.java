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
    
    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;  
    

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
		
		/*String sql = 	"create table "+ DATABASE_TABLE + " ( "
						+ KEY_ROW_ID + " integer primary key autoincrement , "
                		+ KEY_CODE + " text  , " 
                		+ KEY_NAME + "  text  , "  
                		+ KEY_PHONE + "  text  ) " ;
		*/
		db.execSQL(DATABASE_CREATE);
		Log.e("HERE",DATABASE_CREATE);
		//String sql = "insert into " + DATABASE_TABLE + " ( " + RECORDING_NAME + "," + RECORDING_STATUS + "," + RECORDING_ISACTIVE + " ) "
			//	+ " values ( 'Testing Record', '1','1' )";
		//db.execSQL(sql);
		/*
		sql = "insert into " + DATABASE_TABLE + " ( " + KEY_CODE + "," + KEY_NAME + "," + KEY_PHONE + " ) "
				+ " values ( 'C02', 'Ajith','0123456789' )";
		db.execSQL(sql);
		
		sql = "insert into " + DATABASE_TABLE + " ( " + KEY_CODE + "," + KEY_NAME + "," + KEY_PHONE + " ) "
				+ " values ( 'C03', 'James','2013456789' )";
		db.execSQL(sql);
		
		sql = "insert into " + DATABASE_TABLE + " ( " + KEY_CODE + "," + KEY_NAME + "," + KEY_PHONE + " ) "
				+ " values ( 'C04', 'Mohammed' , '9012345678' )";
		db.execSQL(sql);
		*/
	}		
	
	/** Returns all the customers in the table */
	public Cursor getAllCustomers(){
        return mDB.query(DATABASE_TABLE, new String[] { RECORDING_ID,  RECORDING_NAME , RECORDING_DATE_ADDED, RECORDING_DATE_UPLOADED
        		, RECORDING_DURATION, RECORDING_STATUS, RECORDING_ORIGIN, RECORDING_ISACTIVE, RECORDING_FILE_TYPE, RECORDING_DATE_FINALIZED,  RECORDING_PATH  } , 
        		null, null, null, null, 
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
	
	public boolean deleteRecording(String id) {
		
		//  SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(DATABASE_TABLE, RECORDING_ID + "=" + id, null);
		return true;
	}
		
	public boolean renameRecording(String id, String name) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(RECORDING_NAME,name);

		db.update(DATABASE_TABLE, cv, RECORDING_ID +"="+id, null);
		return true;
	}
	
	public int countNameDuplicate(String name) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor dataCount = db.rawQuery("select "+ RECORDING_NAME +" from " + DATABASE_TABLE + " WHERE "+ RECORDING_NAME +" = '"+ name+ "'", null);
		
		return dataCount.getCount();
    } 
	
	public String StripText(String name){
		name = "["+name+"]";
		name = name.replaceAll("[/:!@#$%^&*()<>+?\"{}\\[\\]=`~;]", "");
	
		return name;
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub		
	}

}