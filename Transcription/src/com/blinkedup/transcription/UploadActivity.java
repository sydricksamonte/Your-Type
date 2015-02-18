package com.blinkedup.transcription;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import  org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class UploadActivity extends Activity{
	
	ProgressDialog progressBar;
	Spinner recType;
	EditText editDesc;
	
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();

    private long fileSize = 0;
    boolean isRan;
    
    ParseLoader pl;
    
    String rec_id;
	String recName;
	String recDateAdd;
	String recStat;
	String recDurat;
	String recFileType;
	String recPath;
	Button btnUpload;

	String recDesc;
	String recStrTransType;
	
	RecordingDB mydb;
	DateUtils du;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; goto parent activity.
	            this.finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	

	
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
		pl = new ParseLoader();
		pl.initParse(this);
		
		du = new DateUtils();
		mydb = new RecordingDB(this);
		
		if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
			 ActionBar actionBar = getActionBar();
			 actionBar.setHomeButtonEnabled(true);
			 actionBar.setDisplayHomeAsUpEnabled(true);
		}
		else{
			 Log.e("NOTICE","Device cannot handle ActionBar");
		}
		 
		//TextView tc_recName = (TextView)findViewById(R.id.recUploadName);
		EditText tc_recName = (EditText)findViewById(R.id.txtDesc);
		Intent intent = getIntent();
		 rec_id = (String) intent.getSerializableExtra("INTENT_UPLOAD_RECORDING_ID");
		 recName = (String) intent.getSerializableExtra("INTENT_UPLOAD_RECORDING_NAME");
		 recDateAdd = (String) intent.getSerializableExtra("INTENT_UPLOAD_DATE_ADDED");
		 recStat = (String) intent.getSerializableExtra("INTENT_UPLOAD_STATUS");
		 recDurat = (String) intent.getSerializableExtra("INTENT_UPLOAD_DURATION");
		 recFileType = (String) intent.getSerializableExtra("INTENT_UPLOAD_FILE_TYPE");
		 recPath = (String) intent.getSerializableExtra("INTENT_UPLOAD_PATH");
		 recType = (Spinner)findViewById(R.id.spnType);
		 tc_recName.requestFocus();
		 btnUpload = (Button)findViewById(R.id.btnUpload);
		 editDesc = (EditText)findViewById(R.id.txtDesc);
		isRan = true;
		
		
		btnUpload.setOnClickListener(new OnClickListener() {
			@Override
	    public void onClick(View v) {
				
			if (isNetworkAvailable(UploadActivity.this)){
				if (ParseUser.getCurrentUser() != null){
					recDesc = editDesc.getText().toString();
					recStrTransType = recType.getSelectedItem().toString();
					
					btnUpload.setEnabled(false);
					isRan = true;
					progressBar = new ProgressDialog(UploadActivity.this);
					progressBar.setMax(100);
					
					progressBar.setMessage("Please wait until loading finishes");
					progressBar.setTitle("Uploading File");
					progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					progressBar.setCancelable(false);
					progressBar.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					progressBar.show();
					
					String fileName	= recName  + recFileType;
					File filePath = new File(recPath,  fileName);
				
					File fileObj = filePath;
					byte[] data;
					
					try {
						data =  FileUtils.readFileToByteArray(fileObj);
					} 
					catch (IOException e1) {
						// TODO Auto-generated catch block
						Log.e("sdfsd",	e1.getLocalizedMessage());
						data = null;
					}
				     
				    final ParseFile file = new ParseFile(fileName, data);
			    		if (data != null){
			    			file.saveInBackground(new SaveCallback() {
			    				
			    			  public void done(ParseException e) {
			    				  if (e == null){
			    					  
			    					  
			    					  ParseObject AudioRec = new ParseObject("Product");
			    					  AudioRec.put("Description", recDesc);
			    					  AudioRec.put("Type", recStrTransType);
			    					  AudioRec.put("user", ParseUser.getCurrentUser());
			    					  AudioRec.put("audio", file);
			    					  AudioRec.put("isFinalized", false);
			    					  AudioRec.saveInBackground(new SaveCallback() {
			    		                    @Override
			    		                    public void done(ParseException e) {
			    		                        if (e == null)
			    		                        	 progressBar.dismiss();
			    		                        	Toast.makeText(UploadActivity.this, "Recording sent for typing", Toast.LENGTH_SHORT).show();
			    		                        	
			    		                        	String strDate = du.getDate();
			    		                        	
			    		                        	if(mydb.updateRecordingUploadDate(rec_id,strDate)) {
			    		                        		Intent explicitBackIntent = new Intent(UploadActivity.this,
			    						     	        		TabHostActivity.class);
			    						 				startActivity(explicitBackIntent);
			    		                        	}  
							    					else{
							    						Toast.makeText(getApplicationContext(), "ERROR Database failed to function", Toast.LENGTH_LONG).show(); 
							    					}
			    		                        	
			    		                    }
			    		                }); 
			    					}
			    				  	else{
			    					  Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show(); 
			    				  	}
			    				  }
			    			  }, new ProgressCallback() {
			    				
			    				  	public void done(Integer percentDone) {
			    					  // Update your progress spinner here. percentDone will be between 0 and 100.
			    				  		progressBar.incrementProgressBy(percentDone);
			    				  		Log.e("1","WORKING 1"+ percentDone);
			    				  		}
			    				  	});
			    			}
			    		else{
			    			Toast.makeText(UploadActivity.this, "Unable to locate file properly", 5).show(); 
			    		}
		    	 
		    		}
				else{
					btnUpload.setEnabled(false);
					 Toast.makeText(UploadActivity.this, "Please log-in to upload recording", 5).show(); 
				}
			}
			else{
				Toast.makeText(UploadActivity.this, "Can't connect to the internet", 5).show(); 
			}
			};
		});
	}

	public static boolean isNetworkAvailable(Context context){
		
	    HttpGet httpGet = new HttpGet("http://www.google.com");
	    HttpParams httpParameters = new BasicHttpParams();
	    // Set the timeout in milliseconds until a connection is established.
	    // The default value is zero, that means the timeout is not used.
	    int timeoutConnection = 1500;
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	    // Set the default socket timeout (SO_TIMEOUT)
	    // in milliseconds which is the timeout for waiting for data.
	    int timeoutSocket = 2500;
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

	    DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
	    try{
	        Log.e("INTERNET", "Checking network connection...");
	        httpClient.execute(httpGet);
	        Log.e("INTERNET", "Connection OK");
	        return true;
	    }
	    catch(ClientProtocolException e){
	        e.printStackTrace();
	        Log.e("INTERNET", "Connection FAILED");
	        return false;
	    }
	    catch(IOException e){
	        e.printStackTrace();
	        Log.e("INTERNET", "Connection FAILED");
	        return false;
	    }
	    
	    //Log.d("INTERNET", "Connection unavailable");
	}
}
