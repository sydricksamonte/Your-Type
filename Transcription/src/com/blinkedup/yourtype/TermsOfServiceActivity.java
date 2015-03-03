package com.blinkedup.yourtype;


import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class TermsOfServiceActivity extends Activity {
	
ArrayAdapter<String> adapter;
	
	ParseObject parseObj;
	ParseQuery<ParseObject> pqueryObj;
	List<String> data;
	ParseLoader pl;
	
	String obj;
	String restName;
	
	ProgressDialog myPd_ring;

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
	protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terms_of_service);
		
		pl = new ParseLoader();
		pl.initParse(this);
		
		if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
			 ActionBar actionBar = getActionBar();
			 actionBar.setHomeButtonEnabled(true);
			 actionBar.setDisplayHomeAsUpEnabled(true);
		}
		else{
			 Log.e("NOTICE","Device cannot handle ActionBar");
		}
		
		myPd_ring = ProgressDialog.show(this, "Please wait", "Updating Information", true);
        myPd_ring.setCancelable(false);
		 new Thread(new Runnable() {  
	            @Override
	            public void run() {
	                  // TODO Auto-generated method stub
	                  try{
	                  	if (Network.isNetworkAvailable(TermsOfServiceActivity.this)){
	                  		runOnUiThread(new Runnable() {
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
	                       	            } else {
	                       	            	myPd_ring.dismiss();
	                       	            	new AlertDialog.Builder(TermsOfServiceActivity.this)
	                       	            	.setTitle("Error")
	                       	            	.setMessage("Cannot connect to server. /n/nPlease try again later.")
	                       	            	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	           								 public void onClick(DialogInterface dialog, int which) {
	           									TermsOfServiceActivity.this.finish();
	           								}
	           						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	                       	            }
	                       	        }
	                       	    });
	                          	
	                           }
	                           });
	                  	}
	                  	else{
	                  		myPd_ring.dismiss();
	                  		new AlertDialog.Builder(getParent())
							 .setTitle("No connection")
							 .setMessage("Cannot connect to the Internet")
							 .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								 public void onClick(DialogInterface dialog, int which) {
									TermsOfServiceActivity.this.finish();
								}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	                  	}
	                  }
	                  catch(Exception e){
	                	  myPd_ring.dismiss();
	                	  new AlertDialog.Builder(getParent())
							 .setTitle("Error")
							 .setMessage(e.getLocalizedMessage())
							 .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								 public void onClick(DialogInterface dialog, int which) {
									TermsOfServiceActivity.this.finish();
								}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	                  }
	            }
	      }).start();
		
		 
		
	    
	   
	    
}
	
	public void addData() {

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(restName);
     
    }
}