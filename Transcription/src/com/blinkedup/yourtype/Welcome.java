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
 
        // Retrieve current user from Parse.com
       // ParseUser currentUser = ParseUser.getCurrentUser();
        
        // Convert currentUser into String
       // String struser = currentUser.getUsername().toString();
 
        // Locate TextView in activity_welcome.xml
       // TextView txtuser = (TextView) findViewById(R.id.txtuser);
 
        // Set the currentUser String into TextView
     //   txtuser.setText("You are logged in as " + struser);
 
        // Locate Button in welcome.xml
        logout = (Button) findViewById(R.id.logout);
        btnBuyCredits = (Button) findViewById(R.id.btnBuyCredits);
        showCredits = (Button) findViewById(R.id.credits);
 
        // Logout Button Click Listener
        logout.setOnClickListener(new OnClickListener() {
 
            public void onClick(View arg0) {
                // Logout current user
                //ParseUser.logOut();
                //finish();
            	
            	ParseUser.logOut();
            	ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
           // 	finish();
       
//      	  	Begin Implementation reference for tabs to display when in another activity

				Intent activity3Intent = new Intent(Welcome.this, MainActivity.class);
			
				StringBuffer urlString = new StringBuffer();
				//Activity1 parentActivity = (Activity1)getParent();
				replaceContentView("MainActivity", activity3Intent);
            	
            }
            
            
            
        });
        
        
        
        btnBuyCredits.setOnClickListener(new View.OnClickListener() {
  	      @Override
  	      public void onClick(View view) {
  	        Intent intent = new Intent(Welcome.this, InAppPurchase.class);
  	        startActivity(intent);
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
          //	End Implementation reference for tabs to display when in another activity
}