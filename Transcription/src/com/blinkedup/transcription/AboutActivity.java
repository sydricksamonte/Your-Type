package com.blinkedup.transcription;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class AboutActivity extends Activity{
	 @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_about);

	    ImageButton orderButton = (ImageButton)findViewById(R.id.imageButton1);

	    orderButton.setOnClickListener(new View.OnClickListener() {

	      @Override
	      public void onClick(View view) {
	        Intent intent = new Intent(AboutActivity.this, SendEmailActivity.class);
	        startActivity(intent);
	      }

	    });
	  }
}