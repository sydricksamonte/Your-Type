package com.blinkedup.yourtype;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TabHost;

public class TabHostActivity extends TabActivity {
	/** Called when the activity is first created. */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_tab_host);
	
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, FeedActivity.class);
		spec = tabHost.newTabSpec("home1")
				.setIndicator("", res.getDrawable(R.drawable.ic_tab_home))
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, AudioRecordingActivity.class);
		spec = tabHost.newTabSpec("home1")
				.setIndicator("", res.getDrawable(R.drawable.ic_tab_record))
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, AboutActivity.class);
		spec = tabHost
				.newTabSpec("info")
				.setIndicator("",
						res.getDrawable(R.drawable.ic_tab_info))
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, MainActivity.class);
		spec = tabHost.newTabSpec("accs")
				.setIndicator("", res.getDrawable(R.drawable.ic_tab_account))
				.setContent(intent);
		tabHost.addTab(spec);
		//tabHost.getTabWidget().setStripEnabled(false);
		

		// Do the same for the other tabs
		
		//set tab which one you want open first time 0 or 1 or 2
		tabHost.setCurrentTab(0);
		
		
	     tabHost.setup();
	     int heightValue = 45;
	     //loop through the TabWidget's child Views (the tabs) and set their height value.
	     for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++) {
	          tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = (int) (heightValue * this.getResources().getDisplayMetrics().density);
	          tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#202020"));
	          tabHost.getTabWidget().setDividerDrawable(null);
	     }
	     
	}
}