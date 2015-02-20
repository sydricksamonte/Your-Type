package com.blinkedup.transcription;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
								// Use ActivityGroup so tabs will display when in another activity
public class MainActivity extends ActivityGroup {

	EditText etUsername, etPassword;
	Button btnSave, btnLoad, btnLogin, btnlogout;
	ListView lstAllUsers;
	
	ArrayAdapter<String> adapter;
	
	ParseObject parseObj;
	ParseQuery<ParseObject> pqueryObj;
	List<String> data;
	ParseLoader pl;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);
		lstAllUsers = (ListView) findViewById(R.id.lstAllUsers);
		adapter = new ArrayAdapter<String>(this, R.layout.dropdown_item);
		lstAllUsers.setAdapter(adapter);
		lstAllUsers.setOnItemClickListener(new MyListItemClickListener());
		
		btnSave = (Button) findViewById(R.id.btnSave);
		btnLoad = (Button) findViewById(R.id.btnLoad);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnlogout = (Button) findViewById(R.id.btnlogout);
		btnSave.setOnClickListener(new MyButtonEventHandler());
		btnLoad.setOnClickListener(new MyButtonEventHandler());
		btnlogout.setOnClickListener(new MyButtonEventHandler());
		btnLogin.setOnClickListener(new MyButtonEventHandler());
		data = new ArrayList<String>();                                             //Application ID							   ClientID
		  
		pl = new ParseLoader();
		pl.initParse(this);
		
		btnSave.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
			//	Begin Implementation reference for tabs to display when in another activity

					Intent activity3Intent = new Intent(v.getContext(), RegisterActivity.class);
					StringBuffer urlString = new StringBuffer();
					//Activity1 parentActivity = (Activity1)getParent();
					replaceContentView("RegisterActivity", activity3Intent);
					}			
		});
	}
	
	public void replaceContentView(String id, Intent newIntent) {
		View view = getLocalActivityManager().startActivity(id,newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) .getDecorView(); this.setContentView(view);
		} 
          //	End Implementation reference for tabs to display when in another activity
	
	
	public boolean isOnline(){
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if ((netInfo != null) && (netInfo.isConnected()))  return true; ;
		return false;
	}
	
	

	private class MyListItemClickListener implements OnItemClickListener{
		public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {

			final String name = adapter.getItem(position).toString();
			
			AlertDialog.Builder confirmDialog = new AlertDialog.Builder(MainActivity.this);
			confirmDialog.setTitle("Confirm Deletion");
			confirmDialog.setMessage("Are you sure you want to \nDelete the record of " + name + "?") ;
			confirmDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (!isOnline()) { Toast.makeText(getApplicationContext(), "No internet.", 3).show(); return; }; 
					pqueryObj = ParseQuery.getQuery("_User");
					pqueryObj.whereEqualTo("username", name);
					pqueryObj.getFirstInBackground(new GetCallback<ParseObject>(){
						public void done(ParseObject arg0, ParseException arg1) {
							arg0.deleteInBackground(new DeleteCallback() {
								public void done(ParseException arg0) {
										Toast.makeText(getApplicationContext(), "User '" + name + "' Deleted", 3).show();
								}
							});
						}
					});
				}
			});
			confirmDialog.setNegativeButton("Cancel", null);
			confirmDialog.show();
		}
	}
	
	

	private class MyButtonEventHandler implements OnClickListener{

		public void onClick(View v) {
				if (v.getId() == R.id.btnLoad){
					if (ParseUser.getCurrentUser() != null){
					pqueryObj = ParseQuery.getQuery("_User");
					pqueryObj.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> arg0, ParseException arg1) {
							
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "We have " + arg0.size() + " records", 3).show();
							adapter.clear();
							for(int i = 0; i < arg0.size(); i++){
								Object obj = arg0.get(i);
								String name = ((ParseObject) obj).getString("username");
								adapter.add(name);
							}
						}
					});
					}
				}
				else if (v.getId() == R.id.btnLogin){
					ParseUser.logInInBackground( etUsername.getText().toString(), etPassword.getText().toString(), new LogInCallback() {
						  public void done(ParseUser user, ParseException e) {
						    if (user != null) {
						    	Toast.makeText(getApplicationContext(), "Welcome " + etUsername.getText().toString(), 3).show();
								
								Intent myIntent = new Intent(MainActivity.this, Welcome.class);
								myIntent.putExtra("NAME", etUsername.getText().toString());
								startActivity(myIntent);
						    } else {
						    	Toast.makeText(getApplicationContext(), "Sign-in failed. Incorrect log-in details", 3).show();
						    }
						  }
						});
				}
				
				else if (v.getId() == R.id.btnlogout){
				
					ParseUser.logOut();
					Toast.makeText(getApplicationContext(), "You are logged out ", 3).show();
					//ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
				}
		}
		
		
	}


}