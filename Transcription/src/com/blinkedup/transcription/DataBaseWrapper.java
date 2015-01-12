package com.blinkedup.transcription;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseWrapper extends SQLiteOpenHelper {


	public static final String RECORDINGS = "Recordings";
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
	
	public static final String UPDATES = "Updates";
	public static final String UPDATE_ID = "_id";
	public static final String UPDATE_DATE_ADDED = "_date_added";
	public static final String UPDATE_REMAINING_SEC = "_remaining_sec";
	public static final String UPDATE_ISACTIVE = "_isActive";

	
	public static final String INSTALLS = "Installs";
	public static final String INSTALL_ID = "_id";
	public static final String INSTALL_INSTALL_CODE = "_installCode";


	   
	private static final String DATABASE_NAME = "dbYourType.db";
	private static final int DATABASE_VERSION = 2;

	// creation SQLite statement
	//private static final String DATABASE_CREATE = "create table " + STUDENTS
		//	+ "(" + STUDENT_ID + " integer primary key autoincrement, "
		//	+ STUDENT_NAME + " text not null);";
	
	 /*RECORDING_ID = "_id";
		public static final String INSTALLS = "Installs";
	public static final String INSTALL_ID = "_id";
	public static final String INSTALL_INSTALL_CODE = "_installCode";
		*/
	private static final String DATABASE_CREATE = "create table " + RECORDINGS
			+ "(" + RECORDING_ID + " integer PRIMARY KEY AUTOINCREMENT UNIQUE, "
			+ RECORDING_NAME + " text," + RECORDING_DATE_ADDED + " date," +
			RECORDING_DATE_UPLOADED + " date," +RECORDING_DURATION + " text," +
			RECORDING_STATUS + " integer," +RECORDING_ORIGIN + " integer," +
			RECORDING_ISACTIVE + " boolean," +RECORDING_FILE_TYPE + " text," +
			RECORDING_DATE_FINALIZED + " date);   " 
					+
					"create table " + UPDATES + "(" + UPDATE_ID +
					" integer PRIMARY KEY AUTOINCREMENT UNIQUE, "
					+ UPDATE_DATE_ADDED + " date," + UPDATE_REMAINING_SEC + " integer," +
					UPDATE_ISACTIVE + " boolean);" 
					+
					"create table " + INSTALLS + "(" + INSTALL_ID +
					" integer PRIMARY KEY AUTOINCREMENT UNIQUE, "
					+ INSTALL_INSTALL_CODE + " text);"
					+ "INSERT INTO" + UPDATES +" ("+ UPDATE_ISACTIVE + "," +UPDATE_REMAINING_SEC+") VALUES (\"1\",\"0\")";
	

	//INSERT INTO updates (isActive,remaining_sec) VALUES (\"1\",\"0\")";

	   

	public DataBaseWrapper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// you should do some logging in here
		// ..

		db.execSQL("DROP TABLE IF EXISTS " + UPDATES);
		onCreate(db);
	}

}
