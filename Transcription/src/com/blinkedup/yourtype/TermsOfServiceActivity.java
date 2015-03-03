package com.blinkedup.yourtype;


import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
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

public class TermsOfServiceActivity extends Activity {
	
ArrayAdapter<String> adapter;
	
	ParseObject parseObj;
	ParseQuery<ParseObject> pqueryObj;
	List<String> data;
	ParseLoader pl;
	
	String obj;
	String restName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terms_of_service);
		
		pl = new ParseLoader();
		pl.initParse(this);
		
		
		// Sync A ParseObject
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Terms");
	    query.getInBackground(obj, new GetCallback<ParseObject>() {
	        @Override
	        public void done(ParseObject object, ParseException e) {
	            if (e == null) {
	                restName = object.getString("message");
	               
	                // Update your info after to get the rest info
	            
	                addData();
	            } else {
	                e.printStackTrace();
	            }
	        }
	    });
}
	
	public void addData() {

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(restName);
     
    }
}