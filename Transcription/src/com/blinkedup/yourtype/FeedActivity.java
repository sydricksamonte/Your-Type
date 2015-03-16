package com.blinkedup.yourtype;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	String recDuratRaw;
	
	Handler mHandler;
	
	ProgressDialog myPd_ring;
	
	ParseLoader pl;
	
	ImageButton btnAll;
	ImageButton btnUp;
	ImageButton btnWaiting;
	ImageButton btnDone;
	ImageButton btnRefresh;
	ImageButton btnQuestion;
	
	public final String AUDIO_RECORDER_FOLDER = "YourType";
	RecordingDB myDb;
	boolean isRefreshed;
	String sortKey;
	String[] arrArg =  {""};
	boolean isEnteringUpdateActivity;
    @Override
    public void onResume(){
        super.onResume();
        
       // populate();
        if (isEnteringUpdateActivity == true){
        	RePopulate();
     		isEnteringUpdateActivity = false;
     	}
		/** Creating a loader for populating listview from sqlite database */
		/** This statement, invokes the method onCreatedLoader() */
    }

    private void RePopulate() {
    	getSupportLoaderManager().restartLoader(0, null, this);
    	mAdapter.notifyDataSetChanged();
    
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pl = new ParseLoader();
		pl.initParse(this);
		
        arrArg[0] = "";
        populateTable();
        isRefreshed = false;
        myDb = new RecordingDB(this);
        
        mHandler = new Handler()
		{
		    public void handleMessage(Message msg)
		    {
		    	if (msg.what == 1){
		    		Toast.makeText(FeedActivity.this, "Please log-in to check for changes in transcription status", 5).show();
		    		btnRefresh.setEnabled(true);
		    	}
		    	if (msg.what == 2){
		    		Toast.makeText(FeedActivity.this, "Please connect to internet to check for changes in transcription status", 5).show();
		    		btnRefresh.setEnabled(true);
		    	}
		 	}
		};
		
		/** Creating a loader for populating listview from sqlite database */
		/** This statement, invokes the method onCreatedLoader() */
		getSupportLoaderManager().initLoader(0, null, this);
		
		btnAll = (ImageButton)findViewById(R.id.btnSortAll);
		
		btnAll.setOnClickListener( new OnClickListener() {
			@Override
	            public void onClick(View v) {
				 arrArg[0] = "";
				 RePopulate();
				}
			});
		
		btnWaiting = (ImageButton)findViewById(R.id.btnSortWaiting);
		btnWaiting.setOnClickListener( new OnClickListener() {
			@Override
	            public void onClick(View v) {
					arrArg[0] = "0";
					RePopulate();
				}
			});
		btnUp = (ImageButton)findViewById(R.id.btnSortUp);
		btnUp.setOnClickListener( new OnClickListener() {
			@Override
	            public void onClick(View v) {
				 arrArg[0] = "1";
				 RePopulate();
				}
			});
		
		btnDone = (ImageButton)findViewById(R.id.btnSortDone);
		btnDone.setOnClickListener( new OnClickListener() {
			@Override
	            public void onClick(View v) {
				 arrArg[0] = "2";
				 RePopulate();
				
				}
			});
		
		btnRefresh = (ImageButton)findViewById(R.id.btnRefresh);
		btnRefresh.setOnClickListener( new OnClickListener() {
			@Override
	            public void onClick(View v) {
				myPd_ring = ProgressDialog.show(FeedActivity.this, "Please wait", "Updating...", true);
		        myPd_ring.setCancelable(false);
		       
		        new Thread(new Runnable() {  
		              @Override
		              public void run() {
		            	 
		                    // TODO Auto-generated method stub
		                    try{
		                    	
		                    	if (ParseUser.getCurrentUser() != null){
		                    		if (Network.isNetworkAvailable(FeedActivity.this)){
		                    			runOnUiThread(new Runnable() {
		                    				@Override
		                    				public void run() {
		                    					checkParse();
		                    				}
		                    			});
		                    		}
		                    		else{
			                    		ImportFiles();
			                    		mHandler.sendEmptyMessage(2);
			                    	}
		                    	}
		                    	else{
		                    		Log.e("NOTICE","No user attached");
		                    		mHandler.sendEmptyMessage(1);
		                    		ImportFiles();
		                    	}
		                    }
		                    catch(Exception e){
		                    	btnRefresh.setEnabled(true);
		                    }
		              }
		        }).start();
				
			}		
		});
		btnQuestion = (ImageButton)findViewById(R.id.btnButtonQuestion);
		btnQuestion.setOnClickListener( new OnClickListener() {
			@Override
	            public void onClick(View v) {
				
				}
			});
    }
   
    private void ImportFiles() throws IOException{
    	makeImportFolder();
    	
    	Date curDate = new Date();
		//Date lastDateUpdate = myDb.getLastImportDate();
		Date fileDate = new Date();
    	
		String strDate = dateFunc.getDate();
		
		String filepath = Environment.getExternalStorageDirectory().getPath();
		Log.e("FILE!",filepath);
		//Log.e("FILE! u",lastDateUpdate.toString());
		
		File yourFile;
		MediaPlayer mp = new MediaPlayer();
		FileInputStream fs;
		FileDescriptor fd;
		
		String fileEx = "";
		myPd_ring.dismiss();/////////
    	File yourDir = new File(filepath + "/" + AUDIO_RECORDER_FOLDER ,"IMPORTS");
    	int fileSucc = 0;
    	int fileFail = 0;
    	String errorName ="";
    	long fileSize = 0;
    	String errorMess = "";
    	
    	int listFilesCount = yourDir.listFiles().length;
    	int countIndex = 0;
    	for (File f : yourDir.listFiles()) {
    	    if (f.isFile()){
    	    	//Date lastModDate = new Date(f.lastModified());
    	    	fileSize = f.length();
    	    	countIndex++;
    	    	Log.e("INSf",fileSize+"");
    	    	if (fileSize <= 9961472)  {
    	    		fileEx = FilenameUtils.getExtension(f.getAbsolutePath().toString());
    	    		
    	    		Log.e("INSx",fileEx+"");
    	    		if ((fileEx.equals("mp3"))||(fileEx.equals("mp4") )||(fileEx.equals("3gp") )||(fileEx.equals("aac"))||(fileEx.equals("m4a"))){
    	    		// insertRecording(String name, String dateAdded, String dateUploaded, int duration, int status, int origin, boolean isActive, String fileType, String dateFinalized, String path) {
    	    			String fname = FilenameUtils.removeExtension(f.getName());
    	    			if  (!myDb.isFileExistent(fname)){	
    	    				fs = new FileInputStream(f);
    	    				fd = fs.getFD();
    	    				mp.setDataSource(fd);
    	    				mp.prepare(); 
    	    				int length = mp.getDuration();
    	    				int lengthInSec = Math.round(length/1000);
    	    				mp.release();
    	    		
    	    				if(myDb.insertRecording(fname, strDate, "", lengthInSec, 0, 1, true, "."+fileEx,"",yourDir.getAbsolutePath().toString()+ "/" )) {
    	    					//Toast.makeText(getApplicationContext(), "Recording saved as "+ strDefaultRecordingName, Toast.LENGTH_SHORT).show(); 
    	    					fileSucc++;
    	    				}  
    	    				else{
    	    					fileFail++;
    	    					errorName = errorName +" "+f.getName();
    	    					errorMess = "Failed to insert data on database";
    	    					//Toast.makeText(getApplicationContext(), "Cannot write on database", Toast.LENGTH_SHORT).show(); 
    	    				}
    	    			}
    	    			
    	    		}
    	    		else{
	    				fileFail++;
	    				errorName = errorName +" "+f.getName();
	    				errorMess = "File is not supported";
	    			}
    	    	}
    	    	else{
    				fileFail++;
    				errorName = errorName +" "+f.getName();
    				errorMess = "Cannot accept files larger than 10MB";
    			}
    	    	
    	    	if (listFilesCount == countIndex){
    	    		myPd_ring.dismiss();
    	    	}
    	    }
    	}
    	if (fileFail > 0){
    		new AlertDialog.Builder(FeedActivity.this)
   		    .setTitle("Import failure")
   		    .setMessage("Cannot add these file(s): \n"+fileFail+ ". "+errorName+"\n("+errorMess+")")
   		    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
   		        public void onClick(DialogInterface dialog, int which) { 
   		            // continue with delete
   		        }
   		     })
   		    .setIcon(android.R.drawable.ic_dialog_alert).show();
    	}
    	strDate = dateFunc.getDate();
		myDb.updateImportDate(strDate);
		RePopulate();
    }
    
    private void makeImportFolder(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File importDirec = new File(filepath + "/" + AUDIO_RECORDER_FOLDER, "IMPORTS");
		if (!importDirec.exists()) {
			importDirec.mkdirs();
		}
    }
    
    String parseUserRecordingID;
	boolean parseIsFinalized = false;
	Date parseDateFinalized = new Date();
	Date parseDateUploaded = new Date();
	int recListSize = 0;
	int recListCount = 0;
	
    private void checkParse(){
    	ParseQuery<ParseObject> queryCredits = ParseQuery.getQuery("Product");
		queryCredits.whereEqualTo("user",ParseUser.getCurrentUser());
		queryCredits.whereEqualTo("installCode",myDb.getInstallCode());
		queryCredits.selectKeys((Arrays.asList("userRecordingID","isFinalized", "dateFinalized","createdAt")));
		queryCredits.orderByAscending("createdAt");
		{
			queryCredits.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(List<ParseObject> arg0,
						com.parse.ParseException arg1) {
					// TODO Auto-generated method stub
					Log.e("fdgf","dfsrf");
					recListSize = arg0.size();
					if (arg1 == null) {
						if (recListSize == 0){
							try {
								ImportFiles();
							} catch (IOException e) {}
						}
						else{
						for (ParseObject object : arg0) {
							//tem_id = object.getObjectId();
							//item_credit = 0;
							//item_credit = (Integer) object.getNumber("creditsLeft");
							recListCount++;
							parseUserRecordingID = object.getString("userRecordingID");
	
							parseDateFinalized =  object.getDate("dateFinalized");
							parseIsFinalized = object.getBoolean("isFinalized");
							parseDateUploaded = object.getCreatedAt();
							
							String formattedDate = dateFunc.getRawDateStringFromParse(parseDateFinalized);
							String formattedDateUpload = dateFunc.getRawDateStringFromParse(parseDateUploaded);
	                        if (parseIsFinalized == true){
	                        	myDb.updateRecordingFinalize(parseUserRecordingID,formattedDate);
	                        }
	                        else{
	                        	myDb.updateRecordingUploadDate(parseUserRecordingID, formattedDateUpload);
	                        }
	                        if (recListCount == recListSize){
	                        	try {
									ImportFiles();
								} catch (IOException e) {}
	                        }
	                        isRefreshed = true;
						}
						}
					}
					else{
						try {
							ImportFiles();
						} catch (IOException e) {}
					}
				}
			});
		}	
		
    }
    
    public void populateTable(){
    	 setContentView(R.layout.activity_feed);
  
  
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
  			RecordingDB.RECORDING_STATUS, RecordingDB.RECORDING_STATUS, RecordingDB.RECORDING_PATH,RecordingDB.RECORDING_DURATION},
                  new int[] { R.id.recDateAdd , R.id.recDateFin, R.id.recDateUploaded, R.id.recDurat , R.id.recFileType, R.id.recId, R.id.recIsActive , R.id.recName, R.id.recOrigin, R.id.recStat, R.id.recStatDesc, R.id.recPath, R.id.recDuratRaw }, 0);		
  	
  		
  		mListView.setAdapter(mAdapter);		
  		
  		
  		mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() { 
  			@Override public boolean setViewValue(View view, Cursor cursor, int column) 
  			{ 	
  				if (column == 5) { 
  					TextView tv = (TextView) view;
  					if (tv.getId() ==  R.id.recStatDesc){
  						String statVal = cursor.getString(cursor.getColumnIndex("_status"));
  						String statDesc;
  						if (statVal.equals("0")){
  							statDesc = "Waiting for Upload";
  							img = getResources().getDrawable( R.drawable.colors_gray );
  							
  							img.setBounds( 0, 0, 12, 12 );
  							tv.setCompoundDrawables( img, null, null, null );
  						}
  						else if (statVal.equals("1")){
  							statDesc = "Uploaded";
  							img = getResources().getDrawable( R.drawable.colors_orange );
  							
  							img.setBounds( 0, 0, 12, 12 );
  							tv.setCompoundDrawables( img, null, null, null );
  						}
  						
  						else{
  							statDesc = "Done - Sent to your Email";
  							img = getResources().getDrawable( R.drawable.colors_green );
  							img.setBounds( 0, 0, 12, 12 );
  							tv.setCompoundDrawables( img, null, null, null );
  						}
  						tv.setText(statDesc);
  						//tv.setTypeface(tfRegular);
  						
  						 return true; 
  					}
  					else{
  						return false; 
  					}
  				}
  				else if (column == 1) { 
  					TextView tvxx = (TextView) view;
  					//tvxx.setTypeface(tfRegular);
  					tvxx.setText(cursor.getString(cursor.getColumnIndex("_name")));
  					//	tvxx.setTypeface(tfRegular);
  					//	showDetail = (Button) findViewById(R.id.);
  					//	showDetail.setOnClickListener(new AddButtonClickHandler());
  					 return true;
  				}
  				else if (column == 2) {
  					TextView txv = (TextView) view;
  					
  					if (txv.getId() ==  R.id.recDateAdd){
  						dateFormatted = dateFunc.convertStringToDate(cursor.getString(cursor.getColumnIndex("_date_added")));
  						txv.setText(dateFormatted);
  						//txv.setTypeface(tfRegular);
  					}
  					return true;
  				}
  				else if (column == 4) {
  					TextView txv = (TextView) view;
  					
  					if (txv.getId() ==  R.id.recDurat){
  						
  						strDuration = dateFunc.convIntToLength(cursor.getString(cursor.getColumnIndex("_duration")));
  						txv.setText(strDuration);
  						//txv.setTypeface(tfRegular);
  						return true;
  					}
  					else{
  						return false;
  					}
  				}
  				else{
  					return false;
  				}
  			} 
  		}); 
  	
  		
    }
    
   

	/** A callback method invoked by the loader when initLoader() is called */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Uri uri = Recording.CONTENT_URI;
		//Log.e("3","3");
		
		return new CursorLoader(this, uri, null, null, arrArg, null);
		
	}
	

	/** A callback method, invoked after the requested content provider returned all the data */
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		mAdapter.swapCursor(arg1);
		if (mAdapter != null){
  			TextView noitem;
  	  		noitem = (TextView) findViewById(R.id.empty_list_item);
  	  		noitem.setTextColor(getResources().getColor(R.color.graylight2));
  	  		
  	  		mListView.setEmptyView(findViewById(R.id.empty_list_item));
  		
  		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
		//Log.e("2","2");
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
	        //Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
		
	        TextView tc_recId = (TextView)v.findViewById(R.id.recId);
	        TextView tc_recName = (TextView)v.findViewById(R.id.recName);
	        TextView tc_recStat = (TextView)v.findViewById(R.id.recStat);
	        TextView tc_recDateAdd = (TextView)v.findViewById(R.id.recDateAdd);
	        TextView tc_recDurat = (TextView)v.findViewById(R.id.recDurat);
	        TextView tc_recDateFin = (TextView)v.findViewById(R.id.recDateFin);
	        TextView tc_recDateUploaded = (TextView)v.findViewById(R.id.recDateUploaded);
	        TextView tc_recFileType = (TextView)v.findViewById(R.id.recFileType);
	        TextView tc_recOrigin = (TextView)v.findViewById(R.id.recOrigin);
	        TextView tc_recPath = (TextView)v.findViewById(R.id.recPath);
	        TextView tc_recDuratRaw = (TextView)v.findViewById(R.id.recDuratRaw);
	       
	       Log.e("sdf",tc_recDuratRaw.getText().toString());
	        Intent explicitIntent = new Intent(FeedActivity.this,
					FeedDetailActivity.class);
	       
	        explicitIntent.putExtra("INTENT_RECORDING_ID",tc_recId.getText().toString());
			explicitIntent.putExtra("INTENT_RECORDING_NAME",tc_recName.getText().toString());
			explicitIntent.putExtra("INTENT_DATE_ADDED",tc_recDateAdd.getText().toString());
			explicitIntent.putExtra("INTENT_DATE_UPLOADED",tc_recDateUploaded.getText().toString());
			explicitIntent.putExtra("INTENT_DURATION",tc_recDurat.getText().toString());
			
			
			explicitIntent.putExtra("INTENT_STATUS",tc_recStat.getText().toString());
			explicitIntent.putExtra("INTENT_ORIGIN",tc_recOrigin.getText().toString());
			explicitIntent.putExtra("INTENT_FILE_TYPE",tc_recFileType.getText().toString());
			explicitIntent.putExtra("INTENT_DATE_FINALIZED",tc_recDateFin.getText().toString());
			explicitIntent.putExtra("INTENT_PATH",tc_recPath.getText().toString());
			explicitIntent.putExtra("INTENT_DURATION_RAW",tc_recDuratRaw.getText().toString());
			Log.e("accc",tc_recName.getText().toString());
			Log.e("bccc",tc_recDurat.getText().toString());
			Log.e("dccc",tc_recStat.getText().toString());
			Log.e("fccc",tc_recOrigin.getText().toString());
			Log.e("gccc",tc_recFileType.getText().toString());
			Log.e("hccc",tc_recPath.getText().toString());
			Log.e("iccc",tc_recDuratRaw.getText().toString());
			
			startActivity(explicitIntent);
			isEnteringUpdateActivity = true;
			
			
	    }
	 
	 	protected void onPause() {
         super.onPause();

         	if (isEnteringUpdateActivity == true){	
         	}
	 	}
	 
	
}
