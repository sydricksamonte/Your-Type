package com.blinkedup.yourtype;


import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.GetCallback;


public class TermsOfServiceActivity extends Activity {
	
	ArrayAdapter<String> adapter;
	
	ParseObject parseObj;
	ParseQuery<ParseObject> pqueryObj;
	List<String> data;
	ParseLoader pl;
	
	String obj;
	String restName;
	
	ProgressDialog myPd_ring;
	AlertDialog aDial;

	Handler mHandler;
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
	
	@SuppressLint({ "NewApi", "HandlerLeak" })
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terms_of_service);
		mHandler=new Handler();
		pl = new ParseLoader();
		pl.initParse(this);
		
		mHandler = new Handler()
		{
		    public void handleMessage(Message msg)
		    {
		    	aDial =  new AlertDialog.Builder(TermsOfServiceActivity.this)
          		 .setTitle("")
          		 .setMessage("")
          		 .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          			 public void onClick(DialogInterface dialog, int which) {
          				TermsOfServiceActivity.this.finish();
          			}
          		 }).setIcon(android.R.drawable.ic_dialog_alert).create();
		    	
		    	if (msg.what == 1){
		    		aDial.setTitle("No connection");
           			aDial.setMessage("Cannot connect to the Internet");
           			aDial.show();
		    	}
		    	else if (msg.what == 2){
		    		aDial.setTitle("Error in connection");
           			aDial.setMessage("Cannot connect to server. \nPlease try again later.");
           			aDial.show();
		    	}
		    	else if (msg.what == 3){
		    		aDial.setTitle("Error");
           			aDial.setMessage("Network error encountered");
           			aDial.show();
           		}
		    }
		};
		
		if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
			 ActionBar actionBar = getActionBar();
			 actionBar.setHomeButtonEnabled(true);
			 actionBar.setDisplayHomeAsUpEnabled(true);
		}
		else{
			 Log.e("NOTICE","Device cannot handle ActionBar");
		}
		
		myPd_ring = ProgressDialog.show(TermsOfServiceActivity.this, "Please wait", "Updating Information", true);
        myPd_ring.setCancelable(false);
		 new Thread(new Runnable() {  
	            @Override
	            public void run() {
	                  // TODO Auto-generated method stub
	                  try{
	                  	if (Network.isNetworkAvailable(TermsOfServiceActivity.this)){
	                  		TermsOfServiceActivity.this.runOnUiThread(new Runnable() {
	                           @Override
	                           public void run() {
	                        	 
	                          	
	                        	// Sync A ParseObject
	                       		ParseQuery<ParseObject> query = ParseQuery.getQuery("Terms");
	                       	    query.getInBackground(obj, new GetCallback<ParseObject>() {
	                       	        @Override
	                       	        public void done(ParseObject object, ParseException e) {
	                       	            if (e == null) {
	                       	                restName = object.getString("message");
	                       	               
	                       	                // Update your info after to get the rest info
	                       	                addData();
	                       	                myPd_ring.dismiss();
	                       	            } 
	                       	            else {
	                       	            	myPd_ring.dismiss();
	                       	            	mHandler.sendEmptyMessage(2);
	                       	            	
	                       	            	}
	                       	        	}
	                       	    	});
	                       	    }
	                  		});
	                  	}
	                  	else
	                  	{
	                  		myPd_ring.dismiss();
	                  		mHandler.sendEmptyMessage(1);
	                  	}
	                  }
	                  catch(Exception e){
	                	  myPd_ring.dismiss();
	                	  mHandler.sendEmptyMessage(3);
	                  }
	            }
		 }).start();
	}
	public void addData() {
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(restName);
	}
}