package com.blinkedup.transcription;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class AboutActivity extends Activity{
	 @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_about);

	    ImageButton orderButton = (ImageButton)findViewById(R.id.imageButton1);
	    ImageButton fbButton = (ImageButton)findViewById(R.id.imageButton2);
	    ImageButton twitterButton = (ImageButton)findViewById(R.id.imageButton3);
	    
	    orderButton.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View view) {
	        Intent intent = new Intent(AboutActivity.this, SendEmailActivity.class);
	        startActivity(intent);
	      }

	    });
	    
	    fbButton.setOnClickListener(new View.OnClickListener() {
		
				public void onClick(View v) {
		    	  try {
				        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/467839249945387"));
				        startActivity(intent);
				    } catch(Exception e) {
				        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pages/LPO-EZY/467839249945387")));
				    }
					
				}

		    });
	    
	    twitterButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("https://twitter.com/lpo_ezy"));
				startActivity(intent);
				
			}

	    });
	    
	    
	   
	    
	    
	    
	  }
}