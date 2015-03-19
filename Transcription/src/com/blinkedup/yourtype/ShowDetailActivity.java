package com.blinkedup.yourtype;


import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

@SuppressLint({ "NewApi", "CutPasteId" }) public class ShowDetailActivity extends Activity {

	private ParseQueryAdapter<ParseObject> mainAdapter;
	private CustomAdapter urgentTodosAdapter;
	private ListView listView;
	
	ProgressDialog myPd_ring;
	
	RecordingDB myDb;
	ParseObject parseObj;
	ParseQuery<ParseObject> pqueryObj;
	List<String> data;
	ParseLoader pl;
	DateUtils dateFunc;
	
	TextView lblDateOf, lblTotRemaining, lblStatRem;
	int item_credit;
	int doneLoad = 0;
	// app icon in action bar clicked; goto parent activity.
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            
	            this.finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_detail);
		item_credit = 0;
		dateFunc = new DateUtils();
		myDb = new RecordingDB(this);
	
		lblDateOf = (TextView)findViewById(R.id.lblDateOf);
		lblTotRemaining = (TextView)findViewById(R.id.lblTotRemaining);
		lblStatRem = (TextView)findViewById(R.id.lblStatRem);
		lblDateOf.setText("");	
		lblStatRem.setText("");	
		lblTotRemaining.setText("...");
			
		if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		else{
			Log.e("NOTICE","Device cannot handle ActionBar");
		}
		
		// Initialize main Parse loader id
		pl = new ParseLoader();
		pl.initParse(this);
		
		myPd_ring = ProgressDialog.show(ShowDetailActivity.this, "Please wait", "Calculating credits", true);
        myPd_ring.setCancelable(false);
        
		 new Thread(new Runnable() {  
             @Override
             public void run() {
                   // TODO Auto-generated method stub
                   try{
                   		runOnUiThread(new Runnable() {
                   			@Override
                   			public void run() {
                   				
                   				final String strDate = dateFunc.getDate();
		
                   				ParseQuery<ParseObject> queryCredits = ParseQuery.getQuery("Credit");
                   				queryCredits.whereEqualTo("UserId",ParseUser.getCurrentUser());
                   				queryCredits.whereEqualTo("isActive",true);
                   				queryCredits.selectKeys((Arrays.asList("creditsLeft")));
                   				queryCredits.orderByAscending("createdAt");
                   				{
                   					queryCredits.findInBackground(new FindCallback<ParseObject>() {
                   						public void done(List<ParseObject> objects, ParseException e) {
                   							if (e == null){
                   								myDb.deleteAllUpdate(strDate);
                   								int looper = 0;
                   								for (ParseObject object : objects) {
                   									item_credit = 0;
                   									item_credit = (Integer) object.getNumber("creditsLeft");
                   									
                   									int newInt = myDb.addToCredits(item_credit);
                   									looper = looper + item_credit;
                   									Log.e("1---xddss",looper+"");
                   									Log.e("2---xddss",item_credit+"");
                   									myDb.insertUpdate(strDate, newInt);
                   								}
                   								lblStatRem.setText("Total Credits:");	
                   								Log.e("xddss",looper+"");
                   								lblDateOf.setText("Calculated on: "+dateFunc.convertStringToDate(strDate));	
                   								lblTotRemaining.setText(dateFunc.getDurationString(looper));
                   							}
                   							doneLoad++;
                   							finishLoader();
                   						}
                   					});
                   				}
		
                   				
                   				// Initialize main ParseQueryAdapter
                   				mainAdapter = new ParseQueryAdapter<ParseObject>(ShowDetailActivity.this, "Credit");
                   				mainAdapter.setTextKey("payType");
                   				// Initialize the subclass of ParseQueryAdapter
                   				urgentTodosAdapter = new CustomAdapter(ShowDetailActivity.this);
                   				// Initialize ListView and set initial view to mainAdapter
                   				listView = (ListView) findViewById(R.id.list);
                   				listView.setAdapter(mainAdapter);
                   				mainAdapter.loadObjects();
		
                   				if (listView.getAdapter() == mainAdapter) {
                   					listView.setAdapter(urgentTodosAdapter);
                   					urgentTodosAdapter.loadObjects();
                   					doneLoad++;
                   					finishLoader();
                   				}
                   			
                   			}
                   		});
                   }
                   catch(Exception e){
                	   myPd_ring.dismiss();
                   }
             }
		 }).start();
	}
	
	public void finishLoader(){
		if (doneLoad == 2){
		myPd_ring.dismiss();
		}
	}
	/*public void displayRemainingCredits(){
		String rem = mydb.getRemainingCredit();
		int showInt = Integer.parseInt(rem);
		if (showInt == 0){
			tvCredits.setText("No Credits");
		}
		else{
		tvCredits.setText(getDurationString(Integer.parseInt(rem)));
		}
		
		if (mydb.getCreditRecentDate().equals("")){
			tvdispCreditsDate.setText("");
		}
		else{
			tvdispCreditsDate.setText("Last updated on: "+mydb.getCreditRecentDate());
		}
	}*/
}
