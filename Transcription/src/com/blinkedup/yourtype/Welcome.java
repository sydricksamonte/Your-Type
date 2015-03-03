package com.blinkedup.yourtype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;



import com.parse.ParseUser;
 
public class Welcome extends Activity {
	
	
 
    // Declare Variable
    Button logout, btnBuyCredits;
   
 
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
 
        // Logout Button Click Listener
        logout.setOnClickListener(new OnClickListener() {
 
            public void onClick(View arg0) {
                // Logout current user
                //ParseUser.logOut();
                //finish();
            	
            	ParseUser.logOut();
            	ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
            	finish();
            }
        });
        
        btnBuyCredits.setOnClickListener(new View.OnClickListener() {
  	      @Override
  	      public void onClick(View view) {
  	        Intent intent = new Intent(Welcome.this, InAppPurchase.class);
  	        startActivity(intent);
  	      }

  	    });
    }
}