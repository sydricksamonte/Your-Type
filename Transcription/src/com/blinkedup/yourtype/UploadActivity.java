package com.blinkedup.yourtype;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
	Network myNet;
	DateUtils du;
	
	Integer iissss;
	float duration2 = 0;
	int flooredDuration = 0;
	
	String item_id;
	String item_id_str;
	int item_credit;
 	int diff_item_credit;
 	int chopped_duration;
 	ArrayList<String>  creditor;
  	int creditorIndex;
 	ParseObject post;
 	boolean isErrorFree = true;
  	boolean isUseab = false;
 	int totalMinLeft41 = 0;
  	int totalEverything1 = 0;
  	String  minLStr41;
 	String  strExpDate1;
	
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
	
	public String getVersion(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			return pInfo.versionName + "";
		} catch (NameNotFoundException e) {
			return "";
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
		myNet = new Network();
		
		if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
			 ActionBar actionBar = getActionBar();
			 actionBar.setHomeButtonEnabled(true);
			 actionBar.setDisplayHomeAsUpEnabled(true);
		}
		else{
			 Log.e("NOTICE","Device cannot handle ActionBar");
		}
		 
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
		
		creditor = null;
		creditor = new ArrayList<String>();
		
		creditorIndex = 0;
		    
		btnUpload.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				btnUpload.setEnabled(false);
				if (Network.isNetworkAvailable(UploadActivity.this)){
					String appVersion = getVersion(UploadActivity.this);
					Log.e("VERSION",getVersion(UploadActivity.this));
					String  platform = "Android";
				 
					ParseQuery<ParseObject> query1 = ParseQuery.getQuery("AppPlatform");
					query1.whereEqualTo("appVersion",appVersion);
					query1.whereEqualTo("platform",platform);
					query1.selectKeys((Arrays.asList("uploadMessage","isValid","boardMessage")));
					query1.setLimit(1);
               
					query1.findInBackground(new FindCallback<ParseObject>() {
					
                    public void done(List<ParseObject> objecter, ParseException e) {
                    	if (e != null){
                    		Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    	}
                    	else{
                    		for (ParseObject objectd : objecter) {
                    			
                    			Boolean isValStr = objectd.getBoolean("isValid");
                    			String messageStr =	objectd.getString("uploadMessage");
                    			String boardStr = objectd.getString("boardMessage");
                    			if (isValStr == true){
                    				isUseab = true;
                    				duration2 = Integer.parseInt(recDurat); 
                                	if (isUseab == true){
                                		if (ParseUser.getCurrentUser() != null){
                                			creditorIndex = 0;
                         					iissss = 0;
                         					isErrorFree = true;
                         		
                         					ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Credit");
                         					query1.whereEqualTo("UserId",ParseUser.getCurrentUser());
	                         				query1.selectKeys((Arrays.asList("creditsLeft")));
	                         				query1.orderByDescending("createdAt");
	                         				{
	                         				query1.findInBackground(new FindCallback<ParseObject>() {
	                         					public void done(List<ParseObject> objects, ParseException e) {
	                         						
	                         						if (e != null) {        
	                         							Toast.makeText(UploadActivity.this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
	                         						}
	                         						else{
	                         							totalEverything1 = 0;
	                         							for (ParseObject object : objects) {
	                         								totalMinLeft41 = (Integer) object.getNumber("creditsLeft"); 
	                         								Log.e("dgf",minLStr41 +"dfg");
	                         								totalEverything1 = totalEverything1 + totalMinLeft41;
	                         							}
	                         							
	                         							if (totalEverything1 <= duration2){
	                         								//Insufficient credits
	                         								String sMessage ="You only have "+ totalEverything1/60 +" minute(s) remaining on your account which is insufficient to upload file.\nPlease subscribe again for continuous usage.";
                         		                    			new AlertDialog.Builder(UploadActivity.this)
                         		                    		    .setTitle("Insufficient Credits")
                         		                    		    .setMessage(sMessage)
                         		                    		    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                         		                    		        public void onClick(DialogInterface dialog, int which) { 
                         		                    		            // continue with delete
                         		                    		        }
                         		                    		     })
                         		                    		    .setIcon(android.R.drawable.ic_dialog_alert).show();
                         		                    	}
	                         							else{
	                         								//Credits enough
	                         								recDesc = editDesc.getText().toString();
	                         								recStrTransType = recType.getSelectedItem().toString();
                                             				
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
                                             				{
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
                                             			    				{
                                             			    				AudioRec.saveInBackground(new SaveCallback() {
                                             			    					@Override
                                             			    					public void done(ParseException e) {
                                             			    						if (e == null){
                                             			    		                	progressBar.dismiss();
                                             			    		                 	Toast.makeText(UploadActivity.this, "Recording sent for typing", Toast.LENGTH_SHORT).show();
                                             			    		                        	
                                             			    		                  	String strDate = du.getDate();
                                             			    		                  	if(mydb.updateRecordingUploadDate(rec_id,strDate)) {
                                             			    		                  		//function to subtract total credits
                                             			    		                  		ParseQuery<ParseObject> queryCredits = ParseQuery.getQuery("Credit");
                                             			    		                  		queryCredits.whereEqualTo("UserId",ParseUser.getCurrentUser());
                                             			    		                  		queryCredits.selectKeys((Arrays.asList("creditsLeft")));
                                             	                                         	queryCredits.orderByAscending("createdAt");
                                             	                                         	{
                                             	                                         	queryCredits.findInBackground(new FindCallback<ParseObject>() {
                                             	                                         		public void done(List<ParseObject> objects, ParseException e) {
                                             	                                         			if (e == null) {
                                             	                                         				for (ParseObject object : objects) {
                                             	                                         					if (iissss <= 0){
                                             	                                         						chopped_duration = (int) (duration2 + 0);
                                             	                                         						iissss = 1;
                                             	                                         					}
                                             	                                         					item_id = object.getObjectId();
                                             	                                         					item_credit = 0;
                                             	                                         					item_credit = (Integer) object.getNumber("creditsLeft");
                                             	                                         					
                                             	                                         					creditor.add(new String(item_credit+""));
                                             	                                                                       
                                             	                                         					if (chopped_duration <= 0){
                                             	                                         					}
                                             	                                         					else if (item_credit >= chopped_duration){
                                             	                                         						//Recording duration duration is enough on this credit
                                             	                                         						diff_item_credit = item_credit;
                                             	                                         						chopped_duration =  chopped_duration - diff_item_credit;
                                             	                                                        
                                             	                                         						ParseQuery<ParseObject> query = ParseQuery.getQuery("Credit");
                                             	                                         						query.getInBackground(item_id, new GetCallback<ParseObject>() {
                                             	                                                             	public void done(ParseObject cred, ParseException e) {
                                             	                                                                	if (e != null){
                                             	                                                                		Log.e("PARSE ERROR","ERROR ON CALCULATING NEW CREDITS (LARGER ITEM CREDIT)");
                                             	    	                                                               	isErrorFree = false;
                                             	    	                                                           //  [db pushToLog:1 withPuchaseType:[NSString stringWithFormat:@"ERROR ON CALCULATING NEW CREDITS (LARGER ITEM CREDIT) FOR FILE %@%@ DUE TO ERROR %@ - %@",_UploadModal[2],_UploadModal[4],error.localizedDescription,error.localizedFailureReason]];
                                             	                                                                	}
                                             	    	                                                          	else{
                                             	    	                                                             	Integer theremaining = Math.abs(chopped_duration);
                                             	    	                                                             	int dnumber = theremaining;
                                             	    	                                                            	cred.put("creditsLeft",dnumber);
                                             	    	                                                            	cred.saveInBackground();
                                             	    	                                                                            // NSLog(@"2--- item_credit %d diff_item_credit %d SAVE LOG %@",item_credit,diff_item_credit,dnumber);

                                             	    	                                                              	}
                                             	                                                             		}
                                             	                                         						});
                                             	                                         					}
                                             	                                                         
                                             	                                         					else if(item_credit < chopped_duration){
                                             	                                         						//Recording duration duration needs to be chopped to credits
                                             	                                                                              
                                             	                                         						if (chopped_duration <= 0){}
                                             	                                                            	else{
                                             	                                                            		diff_item_credit = 0;
                                             	                                                                   	//holder of chopped credits
                                             	                                                            		//chopped_duration = 0;
                                             	                                                            		diff_item_credit = item_credit;
                                             	                                                                  	//pass value of credit to chopped credit
                                             	                                                                    chopped_duration = chopped_duration - diff_item_credit;
                                             	                                                                    //subtract the chopped duration to full recording (value of recording)
                                             	                                                                	// NSLog(@"2. Subtract This: %d, Duration minimized to: %d",diff_item_credit,chopped_duration);
                                             	                                                                                  
                                             	                                                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Credit");
                                             	                                                                    query.getInBackground(item_id, new GetCallback<ParseObject>() {
                                             	                                                                    	public void done(ParseObject cred, ParseException e) {
                                             	                                                                    		if (e != null){
                                             	                                                                    			Log.e("PARSE ERROR","ERROR ON CALCULATING NEW CREDITS (SMALLER ITEM CREDIT) FOR FILE");
                                             	                                                                    			isErrorFree = false;
                                             	                                                                    			// [db pushToLog:1 withPuchaseType:[NSString stringWithFormat:@"ERROR ON CALCULATING NEW CREDITS (SMALLER ITEM CREDIT) FOR FILE %@%@ DUE TO ERROR %@ - %@",_UploadModal[2],_UploadModal[4],error.localizedDescription,error.localizedFailureReason]];
                                             	                                                                    		}
                                             	                                                                    		else{
                                             	                                                                    			Integer theremaining = 0;
                                             	                                                                    			int dnumber = theremaining;
                                             	                                                                    			cred.put("creditsLeft",dnumber);
                                             	                                                                    			cred.saveInBackground();
                                             	                                                                    			//NSLog(@"item_credit %d diff_item_credit %d SAVE LOG %@",item_credit,diff_item_credit,dnumber);
                                             	                                                                    		}
                                             	                                                                    	}
                                             	                                                                    });
                                             	                                                            	}
                                             	                                         					}
                                             	                                         				}
                                             	                                         				
                                             	                                         				if (isErrorFree == false){}
                                             	                                                    	else{
                                             	                                                    		//String dateString = [[db DateformatterAdd] stringFromDate:cDate];
                                             	                                                        	// NSString * insertToDB = [NSString stringWithFormat: @"UPDATE updates SET remaining_sec = \"%d\", date_updated = \"%@\" WHERE id = \"%@\" AND isActive  = \"%@\"" ,totalEverything1 - (int)duration2, dateString , @"1", @"1"];
                                             	                                                    		//   [db insertConnection:insertToDB];

                                             	                                                    	}
                                             	                                         				
                                             	                                         				creditorIndex++;
                                             	                                         				// Log.d("score", "Retrieved " + scoreList.size() + " scores");
                                             	                                                 	} 
                                             	                                         			else {
                                             	                                         				Log.d("score", "Error: " + e.getMessage());
                                             	                                                  	}
                                             	                                         		}
                                             	                                         		
                                             	                                         	});
                                             			    		                  	}
                                             	                                         	//  PFQuery * queryCredits = [PFQuery queryWithClassName:@"Credit"];
                                             	                                         	//  [queryCredits selectKeys:@[@"creditsLeft"]];
                                             	                                         	//   [queryCredits whereKey:@"UserId" equalTo:[PFUser currentUser]];
                                             	                                         	// cDate = new Date();
                                             	                                         	//   //[queryCredits whereKey:@"expiryDate" greaterThan:cDate];
                                             	                                         	//   [queryCredits orderByAscending:@"createdAt"];
                                             	                                         	Intent explicitBackIntent = new Intent(UploadActivity.this,TabHostActivity.class);
                                             	                                         	startActivity(explicitBackIntent);
                                             			    		                	}  
                                             			    						}
                                             			    						else{
                                             							    			Toast.makeText(getApplicationContext(), "ERROR Database failed to function", Toast.LENGTH_LONG).show(); 
                                             							    		}
                                             			    					}
                                             			    				}); 
                                             							}
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
	                         						}
	                         					}
	                         					}
	                         					
	                         				});}
                                		}
                                		else{
                         					btnUpload.setEnabled(false);
                         					new AlertDialog.Builder(UploadActivity.this)
      		                    		    .setTitle("You are logged-out")
      		                    		    .setMessage("Please log-in to upload recordings.")
      		                    		    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
      		                    		        public void onClick(DialogInterface dialog, int which) { 
      		                    		            // continue with delete
      		                    		        }
      		                    		     })
      		                    		    .setIcon(android.R.drawable.ic_dialog_alert).show();
      		                    			 btnUpload.setEnabled(true);
                                		}
                                	}
                    			}
                    			else{
                    				Log.e("NOTICE","Old Version");
                    				isUseab = false;
                    				//UIAlertView *alert5 = [[UIAlertView alloc] initWithTitle: @"Error" message: messageStr delegate: nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                    				// [alert5 show];
                    				//  String isValStr = pf_isVal.toString();
                    				//String messageStr =	pf_me
                    				// String sMessage ="You only have "+ totalEverything1/60 +" minute(s) remaining on your account which is insufficient to upload file\nPlease subscribe again for continuous usage.";
  		                    		new AlertDialog.Builder(UploadActivity.this)
  		                    			.setTitle(boardStr)
  		                    			.setMessage(messageStr)
  		                    		    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
  		                    		    	public void onClick(DialogInterface dialog, int which) { 
  		                    		            // continue with delete
  		                    		        }
  		                    		     })
  		                    		    .setIcon(android.R.drawable.ic_dialog_alert).show();
  		                    			btnUpload.setEnabled(true);
                    			}
                    		}
                    	}
                 	}
					});
				}
				else{
					Toast.makeText(UploadActivity.this, "Can't connect to the internet", 5).show(); 
					Log.e("NOTICE","Can't connect to the internet");
				}
			}
		});
	}

	
}
