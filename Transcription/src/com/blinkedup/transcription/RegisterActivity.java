package com.blinkedup.transcription;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.parse.SaveCallback;

public class RegisterActivity extends Activity {

	EditText etUsername, etPassword;
	Button btnSave;
	ListView lstAllUsers;
	
	ArrayAdapter<String> adapter;
	
	ParseObject parseObj;
	ParseQuery<ParseObject> pqueryObj;
	List<String> data;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);	
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new MyButtonEventHandler());
		data = new ArrayList<String>();
		                                           //Application ID							   ClientID
		Parse.initialize(getApplicationContext(), "g9n6hw4p142ALoDaR6JQJmnYfdPkXL7Dyh1qKGo8", "JWe06Y7DDEIWgq8wKcK9w5jiPvsamJTwatTyediO");
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
					pqueryObj = ParseQuery.getQuery("tbl_Users");
					pqueryObj.whereEqualTo("Username", name);
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
				if (v.getId() == R.id.btnSave){
					parseObj = new ParseObject("tbl_Users");
					String username = etUsername.getText().toString();
					String password = etPassword.getText().toString();
					parseObj.put("Username", username);
					parseObj.put("Password", password);
					parseObj.saveInBackground(new SaveCallback(){
						public void done(ParseException arg0) {
							Toast.makeText(getApplicationContext(), "Record successfully saved.", 3).show();
						}
					});
					etUsername.setText("");
					etPassword.setText("");
				}
				else if (v.getId() == R.id.btnLoad){
					pqueryObj = ParseQuery.getQuery("tbl_Users");
					pqueryObj.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> arg0, ParseException arg1) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "We have " + arg0.size() + " records", 3).show();
							adapter.clear();
							for(int i = 0; i < arg0.size(); i++){
								Object obj = arg0.get(i);
								String name = ((ParseObject) obj).getString("Username");
								adapter.add(name);
							}
						}
					});
				}
				else if (v.getId() == R.id.btnLogin){
					pqueryObj = ParseQuery.getQuery("tbl_Users");
					pqueryObj.whereEqualTo("Username", etUsername.getText().toString());
					pqueryObj.getFirstInBackground(new GetCallback<ParseObject>() {
						public void done(ParseObject arg0, ParseException arg1) {
							if (arg1 == null){
								if (arg0.getString("Username").toString().equals(etUsername.getText().toString()))
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