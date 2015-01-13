package com.blinkedup.transcription;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class RecordingOperations {
	private DataBaseWrapper dbHelper;
	private String[] RECORDING_TABLE_COLUMNS = { DataBaseWrapper.RECORDING_ID,
			DataBaseWrapper.RECORDING_NAME,
			DataBaseWrapper.RECORDING_DATE_ADDED, 
			DataBaseWrapper.RECORDING_DATE_UPLOADED, 
			DataBaseWrapper.RECORDING_DURATION, 
			DataBaseWrapper.RECORDING_STATUS, 
			DataBaseWrapper.RECORDING_ORIGIN, 
			DataBaseWrapper.RECORDING_ISACTIVE , 
			DataBaseWrapper.RECORDING_FILE_TYPE, 
			DataBaseWrapper.RECORDING_DATE_FINALIZED
			};
	private SQLiteDatabase database;
	
	public RecordingOperations(Context context) {
		dbHelper = new DataBaseWrapper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public Recording addRecording(String name, String dateAdded, String dateUploaded, int duration, int status, int origin, boolean isActive, String fileType, String dateFinalized) {

		ContentValues values = new ContentValues();

		values.put(DataBaseWrapper.RECORDING_NAME, name);
		values.put(DataBaseWrapper.RECORDING_DATE_ADDED, dateAdded);
		values.put(DataBaseWrapper.RECORDING_DATE_UPLOADED, dateUploaded);
		values.put(DataBaseWrapper.RECORDING_DURATION,duration );
		values.put(DataBaseWrapper.RECORDING_STATUS, status);
		values.put(DataBaseWrapper.RECORDING_ORIGIN, origin);
		values.put(DataBaseWrapper.RECORDING_ISACTIVE, isActive);
		values.put(DataBaseWrapper.RECORDING_FILE_TYPE, fileType);
		values.put(DataBaseWrapper.RECORDING_DATE_FINALIZED, dateFinalized);

		long studId = database.insert(DataBaseWrapper.RECORDINGS, null, values);

		// now that the student is created return it ...
		Cursor cursor = database.query(DataBaseWrapper.RECORDINGS,
				RECORDING_TABLE_COLUMNS, DataBaseWrapper.RECORDING_ID + " = "
						+ studId, null, null, null, null);

		cursor.moveToFirst();

		Recording newComment = parseRecording(cursor);
		cursor.close();
		return newComment;
	}
	
	private Recording parseRecording(Cursor cursor) {
		Recording recording = new Recording();
		recording.setId((cursor.getInt(0)));
		recording.setName(cursor.getString(1));
		return recording;
	}

	
	
}
