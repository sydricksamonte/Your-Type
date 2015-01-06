package com.blinkedup.transcription;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class AndroidChronometer extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main1);
        
        final Chronometer myChronometer = (Chronometer)findViewById(R.id.chronometer);
        Button buttonStart = (Button)findViewById(R.id.buttonstart);
        Button buttonStop = (Button)findViewById(R.id.buttonstop);
        Button buttonReset = (Button)findViewById(R.id.buttonreset);
        
        buttonStart.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myChronometer.start();
			}});
        
        buttonStop.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myChronometer.stop();
				
			}});
        
        buttonReset.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myChronometer.setBase(SystemClock.elapsedRealtime());

			}});
        
        
    }
}