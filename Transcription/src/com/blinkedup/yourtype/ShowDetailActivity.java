package com.blinkedup.yourtype;


import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

@SuppressLint("NewApi") public class ShowDetailActivity extends Activity {

	private ParseQueryAdapter<ParseObject> mainAdapter;
	private CustomAdapter urgentTodosAdapter;
	private ListView listView;
	
	ParseObject parseObj;
	ParseQuery<ParseObject> pqueryObj;
	List<String> data;
	ParseLoader pl;
	
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
		
		//back button 
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
		

		// Initialize main ParseQueryAdapter
		mainAdapter = new ParseQueryAdapter<ParseObject>(this, "Credit");
		mainAdapter.setTextKey("payType");
	//	mainAdapter.setImageKey("image");

		// Initialize the subclass of ParseQueryAdapter
		urgentTodosAdapter = new CustomAdapter(this);

		// Initialize ListView and set initial view to mainAdapter
		listView = (ListView) findViewById(R.id.list);
		listView.setAdapter(mainAdapter);
		mainAdapter.loadObjects();
		

		if (listView.getAdapter() == mainAdapter) {
			listView.setAdapter(urgentTodosAdapter);
			urgentTodosAdapter.loadObjects();
		}

		// Initialize toggle button
		//Button toggleButton = (Button) findViewById(R.id.toggleButton);
		//toggleButton.setOnClickListener(new OnClickListener() {

		//	@Override
		//	public void onClick(View v) {
		//		if (listView.getAdapter() == mainAdapter) {
		//			listView.setAdapter(urgentTodosAdapter);
		//			urgentTodosAdapter.loadObjects();
		//		} else {
		//			listView.setAdapter(mainAdapter);
		//			mainAdapter.loadObjects();
		//		}
			

		//});
	}

}
