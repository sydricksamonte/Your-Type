package com.blinkedup.transcription;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class FeedActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {
	
	SimpleCursorAdapter mAdapter;	
	ListView mListView;		
	DateUtils dateFunc;
	String dateFormatted = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        dateFunc = new DateUtils();
        mListView = (ListView) findViewById(R.id.listview);        
        
		mAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.listview_item_feed,
                null,
                new String[] { RecordingDB.RECORDING_DATE_ADDED, RecordingDB.RECORDING_DATE_FINALIZED, RecordingDB.RECORDING_DATE_UPLOADED,
			RecordingDB.RECORDING_DURATION, RecordingDB.RECORDING_FILE_TYPE, RecordingDB.RECORDING_ID,
			RecordingDB.RECORDING_ISACTIVE, RecordingDB.RECORDING_NAME, RecordingDB.RECORDING_ORIGIN,
			RecordingDB.RECORDING_STATUS, RecordingDB.RECORDING_STATUS},
                new int[] { R.id.recDateAdd , R.id.recDateFin, R.id.recDateUploaded, R.id.recDurat , R.id.recFileType, R.id.recId, R.id.recIsActive , R.id.recName, R.id.recOrigin, R.id.recStat, R.id.recStatDesc }, 0);		
	
		
		mListView.setAdapter(mAdapter);		
		
		mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() { 
			@Override public boolean setViewValue(View view, Cursor cursor, int column) 
			{ 
				if (column == 5) { 
					TextView tv = (TextView) view;
					if (tv.getId() ==  R.id.recStatDesc){
						 
						String statVal = cursor.getString(cursor.getColumnIndex("_status"));
						String statDesc;
						if (statVal.equals("1")){
							statDesc = "Uploaded - Awaiting Process";
						}
						else if (statVal.equals("2")){
							statDesc = "Transcription Done";
						}
						else{
							statDesc = "Waiting for Upload";
						}
						tv.setText(statDesc);
						return true; 
					}
					else{
						return false; 
					}
				}
				else if (column == 2) {
					TextView txv = (TextView) view;
					
					if (txv.getId() ==  R.id.recDateAdd){
						//Log.e("OIdx",cursor.getString(cursor.getColumnIndex("_date_added")));
						dateFormatted = dateFunc.convertStringToDate(cursor.getString(cursor.getColumnIndex("_date_added")));
						txv.setText(dateFormatted);
						
					}
					return true;
				}
				else{
					return false;
				}
			} 
		}); 
				
	
		
		/** Creating a loader for populating listview from sqlite database */
		/** This statement, invokes the method onCreatedLoader() */
		getSupportLoaderManager().initLoader(0, null, this);
		
    }

    
    public String getStat(String stat)
    {   
    	String desc = "";
        if (stat.equals("0")){
        	desc = "Unuploaded";
        }
        else{
        	desc = "Awaiting Process!";
        }
        return desc;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	/** A callback method invoked by the loader when initLoader() is called */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Uri uri = Recording.CONTENT_URI;
		return new CursorLoader(this, uri, null, null, null, null);
	}

	/** A callback method, invoked after the requested content provider returned all the data */
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		mAdapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}
}
