package com.blinkedup.yourtype;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;



import com.parse.ParseUser;
 
public class Welcome extends ActivityGroup {
	
    // Declare Variable
    Button logout, btnBuyCredits, showCredits;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the view from .xml
        setContentView(R.layout.activity_welcome);
        
        Intent intent = getIntent();
		
		String message = intent.getExtras().getString("NAME");	
		TextView tv = (TextView) findViewById(R.id.textViewOutput);
		tv.setText(message);
 
        // Locate Button in welcome.xml
        logout = (Button) findViewById(R.id.logout);
        btnBuyCredits = (Button) findViewById(R.id.btnBuyCredits);
        showCredits = (Button) findViewById(R.id.credits);
 
        // Logout Button Click Listener
        logout.setOnClickListener(new OnClickListener() {
 
            public void onClick(View arg0) {
            	ParseUser.logOut();
            	ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
				Intent activity3Intent = new Intent(Welcome.this, MainActivity.class);
			
				StringBuffer urlString = new StringBuffer();
				replaceContentView("MainActivity", activity3Intent);
            }
        });
        
        btnBuyCredits.setOnClickListener(new View.OnClickListener() {
        	@Override
  	      	public void onClick(View view) {
        		//Intent intent = new Intent(Welcome.this, TabHostActivity.class);
        		//startActivity(intent);
        		
        		Intent activityIntentToRegister = new Intent(Welcome.this, InAppPurchase.class);
				replaceContentView("InAppPurchase", activityIntentToRegister);
 		
  	      	}
        });
        
        showCredits.setOnClickListener(new View.OnClickListener() {
    	      @Override
    	      public void onClick(View view) {
    	    	  Intent intent = new Intent(Welcome.this, ShowDetailActivity.class);
    	    	  startActivity(intent);
    	      }
        });
	}
    
    public void replaceContentView(String id, Intent newIntent) {
		View view = getLocalActivityManager().startActivity(id,newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) .getDecorView(); this.setContentView(view);
	} 
}