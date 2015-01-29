package com.blinkedup.transcription;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FeedActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, OnItemClickListener {
	
	SimpleCursorAdapter mAdapter;	
	ListView mListView;		
	DateUtils dateFunc;
	String dateFormatted = "";
	String strDuration = "";
	Drawable img;
	Typeface tfRegular;
	Typeface tfUltra;
	Button showDetail; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        
        tfRegular = Typeface.createFromAsset(getAssets(),
                "Avenir_Reg.ttf");
        tfUltra = Typeface.createFromAsset(getAssets(),
                "Avenir_Ultra.ttf");
    	
        dateFunc = new DateUtils();
        mListView = (ListView) findViewById(R.id.listview);  
        //ListView listview = (ListView) findViewById(R.id.listview1);
        mListView.setOnItemClickListener(this);
        
        
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
							statDesc = "Uploaded — Awaiting Process";
							img = getResources().getDrawable( R.drawable.colors_orange );
							img.setBounds( 0, 0, 12, 12 );
							tv.setCompoundDrawables( img, null, null, null );
						}
						else if (statVal.equals("2")){
							statDesc = "Transcription Done";
							img = getResources().getDrawable( R.drawable.colors_green );
							img.setBounds( 0, 0, 12, 12 );
							tv.setCompoundDrawables( img, null, null, null );
						}
						else{
							statDesc = "Waiting for Upload";
							img = getResources().getDrawable( R.drawable.colors_gray );
							img.setBounds( 0, 0, 12, 12 );
							tv.setCompoundDrawables( img, null, null, null );
						}
						tv.setText(statDesc);
						tv.setTypeface(tfRegular);
						
						 return true; 
					}
					else{
						return false; 
					}
				}
				else if (column == 1) { 
					TextView tvxx = (TextView) view;
					tvxx.setTypeface(tfRegular);
					tvxx.setText(cursor.getString(cursor.getColumnIndex("_name")));
						tvxx.setTypeface(tfRegular);
					//	showDetail = (Button) findViewById(R.id.);
					//	showDetail.setOnClickListener(new AddButtonClickHandler());
					 return true;
				}
				else if (column == 2) {
					TextView txv = (TextView) view;
					
					if (txv.getId() ==  R.id.recDateAdd){
						dateFormatted = dateFunc.convertStringToDate(cursor.getString(cursor.getColumnIndex("_date_added")));
						txv.setText(dateFormatted);
						txv.setTypeface(tfRegular);
					}
					return true;
				}
				else if (column == 4) {
					TextView txv = (TextView) view;
					
					if (txv.getId() ==  R.id.recDurat){
						strDuration = dateFunc.convIntToLength(cursor.getString(cursor.getColumnIndex("_duration")));
						txv.setText(strDuration);
						txv.setTypeface(tfRegular);
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
	/////
	public class AddButtonClickHandler implements OnClickListener {
		public void onClick(View view) {
			//int num1 = Integer.parseInt(firstNum.getText().toString());
			//int num2 = Integer.parseInt(secondNum.getText().toString());
			Intent explicitIntent = new Intent(FeedActivity.this,
					FeedDetailActivity.class);
			explicitIntent.putExtra("RECID","1");
			startActivity(explicitIntent);
			
		}
	}
	 public void onItemClick(AdapterView<?> l, View v, int position, long id) {
	        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
	        String recId = "-";
	        TextView tc = (TextView)v.findViewById(R.id.recId);
	       
	        recId = tc.getText().toString();
	       
	        Intent explicitIntent = new Intent(FeedActivity.this,
					FeedDetailActivity.class);
	       

			explicitIntent.putExtra("INTENT_RECORDING_ID",recId);
			explicitIntent.putExtra("INTENT_RECORDING_NAME",recId);
			explicitIntent.putExtra("INTENT_DATE_UPLOADED",recId);
			explicitIntent.putExtra("INTENT_DURATION",recId);
			explicitIntent.putExtra("INTENT_STATUS",recId);
			explicitIntent.putExtra("INTENT_ORIGIN",recId);
			explicitIntent.putExtra("INTENT_FILE_TYPE",recId);
			explicitIntent.putExtra("INTENT_DATE_FINALIZED",recId);
			startActivity(explicitIntent);
	    }
	
}
