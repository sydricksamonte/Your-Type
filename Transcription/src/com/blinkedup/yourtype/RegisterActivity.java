package com.blinkedup.yourtype;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class RegisterActivity extends ActivityGroup   {
	

	EditText etUsername, etPassword, etRePassword;
	Button btnSave,btnTerms,btnCancel;
	ListView lstAllUsers;
	Write nw;
	ArrayAdapter<String> adapter;
	
	ParseObject parseObj;
	ParseQuery<ParseObject> pqueryObj;
	List<String> data;
	ParseLoader pl;
	ProgressDialog myPd_ring;
	RecordingDB mydb;
	DateUtils dateFunc;
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		try{
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                                                        INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
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
		setContentView(R.layout.activity_register);
	
		mydb = new RecordingDB(this);
		dateFunc = new DateUtils();
	      
		if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
			 ActionBar actionBar = getActionBar();
			 actionBar.setHomeButtonEnabled(true);
			 actionBar.setDisplayHomeAsUpEnabled(true);
		}
		else{
			 Log.e("NOTICE","Device cannot handle ActionBar");
		}
		
		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);
		etRePassword = (EditText) findViewById(R.id.etRePassword);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new MyButtonEventHandler());
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new MyButtonEventHandler());
		btnTerms = (Button) findViewById(R.id.btnTerms);
		btnTerms.setOnClickListener(new MyButtonEventHandler());
		
		data = new ArrayList<String>();
		     
		pl = new ParseLoader();
		nw = new Write();
		pl.initParse(this);
		
		//Validate email
		final EditText emailValidate = (EditText)findViewById(R.id.etUsername); 

		String email = emailValidate.getText().toString().trim();
		String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
	    
	  
	}
	
	public boolean isOnline(){
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if ((netInfo != null) && (netInfo.isConnected()))  return true; ;
		return false;
	}

	private class MyListItemClickListener implements OnItemClickListener{
		public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {

			final String name = adapter.getItem(position).toString();
			
			AlertDialog.Builder confirmDialog = new AlertDialog.Builder(RegisterActivity.this);
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
	
	public void replaceContentView(String id, Intent newIntent) {
		View view = getLocalActivityManager().startActivity(id,newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) .getDecorView(); this.setContentView(view);
	} 

	private class MyButtonEventHandler implements OnClickListener{
		public void onClick(View v) {
			if (v.getId() == R.id.btnSave){
				String pass1 = etUsername.getText().toString(); 
				String pass = etPassword.getText().toString(); 
				String rePass = etRePassword.getText().toString();	
				
				if(TextUtils.isEmpty(pass1) || pass1.length() < 7) { 
					etUsername.setError("Please enter a valid email."); 
					return; 
				}
				else if(TextUtils.isEmpty(pass) || pass.length() < 7) { 
					etPassword.setError("You must have atleast 7 characters in your password"); 
					return; 
				} 
				else if((pass) == (pass1)) { 
					etPassword.setError("Password shoud not be the same with your email address"); 
				} 
				else if(!pass.equals(rePass)){ 
					etPassword.setError("Password did not match"); 
				} 
				else{
					myPd_ring = ProgressDialog.show(getParent(), "Please wait", "Verifying information", true);
			        myPd_ring.setCancelable(false);
			        new Thread(new Runnable() {  
			              @Override
			              public void run() {
			                    // TODO Auto-generated method stub
			                    try{
			                    	if (Network.isNetworkAvailable(getParent())){
			                    		runOnUiThread(new Runnable() {
			                             @Override
			                             public void run() {
			                            	 btnSave.setEnabled(false);
			                            	 ParseUser user = new ParseUser();
			                            	 user.setUsername(etUsername.getText().toString());
			                            	 user.setPassword(etPassword.getText().toString());
			                            	 user.setEmail(etUsername.getText().toString());
			        					 
			                            	 user.signUpInBackground(new SignUpCallback() {
			                            		 public void done(ParseException e) {
			                            			 if (e == null) {
			                            				 String payType = "FREE";
			                            				 final int creditsLeft = 60;
			                            				 
			                            				 ParseObject AudioRec = new ParseObject("Credit");
			                            				 AudioRec.put("payType", payType);
			                            				 AudioRec.put("creditsLeft", creditsLeft);
			                            				 AudioRec.put("isActive", true);
			                            				 AudioRec.put("subsType", 2);
			                            				 AudioRec.put("UserId", ParseUser.getCurrentUser());
			                            				 {
			                            					 AudioRec.saveInBackground(new SaveCallback() {
			                            						 @Override
			                            						 public void done(ParseException ex) {
			                            							 if (ex == null){
			                            								 String strDate = dateFunc.getDate();
			                            								 mydb.insertUpdate(strDate,mydb.addToCredits(creditsLeft));
			                            								 myPd_ring.dismiss();
			                            								 
			                            								 new AlertDialog.Builder(getParent())
			                            									 .setTitle("Sign up complete")
			                            									 .setMessage("Congratulations! You are just given free minute of transcription service. \n\nPlease verify your email to start recording.")
			                            									 .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			                            										 public void onClick(DialogInterface dialog, int which) {
			                            											 Intent explicitBackIntent = new Intent(getParent(),TabHostActivity.class);
			                            											 startActivity(explicitBackIntent);
			                            											 btnSave.setEnabled(true);
			                            										}
			                            								}).setIcon(android.R.drawable.ic_dialog_alert).show();
			                            							 }
			                            							 else{
			                            								 Log.e("",ex.getLocalizedMessage());
			                            							 }
			                            						 }
			                            					}); 
			                            				 }
			                            			 } 
			                            			 else {
			                            				 myPd_ring.dismiss();
			                            				 btnSave.setEnabled(true);
			                            				 Toast.makeText(getParent().getBaseContext(), e.getLocalizedMessage(), 5).show();
			                            				 Log.e("ERROR",e.getLocalizedMessage());
			                            			 }
			                            		 }
			                            	 });
			                             }
			                             });
			                    	}
			                    	else{
			                    		myPd_ring.dismiss();
                        				btnSave.setEnabled(true);
                        				Toast.makeText(getParent().getBaseContext(),"Cannot connect to the Internet", 8).show();
                        			}
			                    }
			                    catch(Exception e){
			                     	myPd_ring.dismiss();
			                     	Toast.makeText(getParent().getBaseContext(), e.getLocalizedMessage(), 8).show();
			                     	btnSave.setEnabled(true);
			                    }
			              }
			        }).start();
				}
			}
			else if (v.getId() == R.id.btnTerms){
					
					btnTerms.setOnClickListener(new OnClickListener(){
						
						@Override
						public void onClick(View v) {
							
						//	Begin Implementation reference for tabs to display when in another activity

								Intent activity3Intent = new Intent(v.getContext(), TermsOfServiceActivity.class);
								StringBuffer urlString = new StringBuffer();
								//Activity1 parentActivity = (Activity1)getParent();
								replaceContentView("TermsOfServiceActivity", activity3Intent);
								}
					});
			}
			else if (v.getId() == R.id.btnCancel){
				//Intent activityIntentRegister = new Intent(RegisterActivity.this, MainActivity.class);
				//replaceContentView("MainActivity", activityIntentRegister);
				//Intent explicitIntent = new Intent(RegisterActivity.this,
						//MainActivity.class);
				//startActivity(explicitIntent);
				RegisterActivity.this.finish();
				
			}
				
			/*
				else if (v.getId() == R.id.btnLogin){
					pqueryObj = ParseQuery.getQuery("_User");
					pqueryObj.whereEqualTo("username", etUsername.getText().toString());
					pqueryObj.getFirstInBackground(new GetCallback<ParseObject>() {
						public void done(ParseObject arg0, ParseException arg1) {
							if (arg1 == null){
								if (arg0.getString("username").toString().equals(etUsername.getText().toString()))
									Toast.makeText(getApplicationContext(), "Welcome " + etUsername.getText().toString(), 3).show();
							}
							else
								Toast.makeText(getApplicationContext(), "Incorrect username ", 3).show();
						}
					});
				}
				
			*/	
		}
	}


}