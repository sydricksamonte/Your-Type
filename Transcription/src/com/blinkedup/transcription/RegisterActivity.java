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
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
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

public class RegisterActivity extends ActivityGroup {
	

	EditText etUsername, etPassword;
	Button btnSave,btnTerms,btnCancel;
	ListView lstAllUsers;
	
	ArrayAdapter<String> adapter;
	
	ParseObject parseObj;
	ParseQuery<ParseObject> pqueryObj;
	List<String> data;
	ParseLoader pl;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);	
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new MyButtonEventHandler());
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new MyButtonEventHandler());
		btnTerms = (Button) findViewById(R.id.btnTerms);
		btnTerms.setOnClickListener(new MyButtonEventHandler());
		
		data = new ArrayList<String>();
		     
		pl = new ParseLoader();
		pl.initParse(this);
		
		//Validate email
		final EditText emailValidate = (EditText)findViewById(R.id.etUsername); 

	//	final TextView textView = (TextView)findViewById(R.id.text); 

		String email = emailValidate.getText().toString().trim();

		String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


		// onClick of button perform this simplest code.
		
	
		
		if (email.matches(emailPattern))
		{
		Toast.makeText(getApplicationContext(),"valid email address",Toast.LENGTH_SHORT).show();
		}
		else 
		{
		Toast.makeText(getApplicationContext(),"Please enter a valid email address.", Toast.LENGTH_LONG).show();
		
	
		}
		
		 
		
		
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
					    
					    if(TextUtils.isEmpty(pass1) || pass1.length() < 7) 
					    { 
					    	etUsername.setError("Please enter a username."); 
					        return; 
					    } 
					  
					    
					    else if(TextUtils.isEmpty(pass) || pass.length() < 7) 
					    { 
					    	etPassword.setError("You must have atleast 7 characters in your password"); 
					        return; 
					    } 
					    
					    else if((pass) == (pass1)) 
					    { 
					    	etPassword.setError("Password shoud not be the same with your email address"); 
					      
					    } 
					    
					   
					    
					    
					    //continue processing

			
					
					
					ParseUser user = new ParseUser();
					user.setUsername(etUsername.getText().toString());
					user.setPassword(etPassword.getText().toString());
					user.setEmail(etUsername.getText().toString());
					 
					// other fields can be set just like with ParseObject
					user.put("phone", "650-253-0000");
					
					user.signUpInBackground(new SignUpCallback() {
						  public void done(ParseException e) {
						    if (e == null) {
						    	Toast.makeText(getApplicationContext(), "Record successfully saved.", 3).show();
						    	etUsername.setText("");
								etPassword.setText("");
						    } else {
						    	Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), 3).show();
						    }
						  }
						});
					
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
					
					btnCancel.setOnClickListener(new OnClickListener(){
						
						@Override
						public void onClick(View v) {
							
						//	Begin Implementation reference for tabs to display when in another activity

								Intent activity3Intent = new Intent(v.getContext(), MainActivity.class);
								StringBuffer urlString = new StringBuffer();
								//Activity1 parentActivity = (Activity1)getParent();
								replaceContentView("MainActivity", activity3Intent);
								}

					
					});
			
				}
				
				else if (v.getId() == R.id.btnLoad){
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
				
				
		}
	}


}