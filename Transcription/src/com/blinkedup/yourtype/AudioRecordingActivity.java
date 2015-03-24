package com.blinkedup.yourtype;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.text.SimpleDateFormat; 

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;



public class AudioRecordingActivity extends Activity {
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private static final String AUDIO_RECORDER_FOLDER = "YourType";

	private MediaRecorder recorder = null;
	private int currentFormat = 0;
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,
			MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4,
			AUDIO_RECORDER_FILE_EXT_3GP };
	
	private TextView timerView;				// text on screen containing the time
	private TextView lapView;				// text on screen containing the lap times
	private long accumTime = 0;				// elapsed time in ms since start or reset pressed minus time the time stopped
	private long startTime = 0;				// time of day in ms that start of reset pressed
	private long lapStartTime = 0;			// time of day in ms that start, reset, or lap pressed
	private Handler timerHandler;			// handler called periodically by system timer
	private Laps laps;						// ArrayList containing the lap information
	final private long timerInterval = 100;	// time in ms between executions of the timer handler
	private boolean timerRunning = false;	// true if timer is running
	ToggleButton tglPlayPause;				//toggle play pause button
	
	 private long fileSize = 0;
	 private int progressBarStatus = 0;
	 private int progressStatusRemaining = 3360;
	 private int conversion;
	 private Handler progressBarHandler = new Handler();
	
	String strDefaultRecordingName;
	DateUtils dateFunc;
	RecordingDB mydb;
	Boolean isEnabledToAnimateIn;
	Boolean isEnabledToAnimateOut;
	
	View stopButton;
	
	private Animation animFadeIn;
    private Animation animFadeOut;
    private Animation animFadeRepeating;
    private ProgressBar progressBar;
	private int progressStatus = 0;
	private int maxTime = 3360;
	private TextView textView1;
	private TextView textView2;
	private TextView textView3;
	private Handler handler = new Handler();
	static boolean runThread = true;
    
	long timerEnd = 0;
	int showDialog = 0;
	
	boolean isMediaRecording = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setButtonHandlers();
		enableButtons(false);
		setFormatButtonCaption();

		mydb = new RecordingDB(this);
		dateFunc = new DateUtils();
		
		showDialog = 0;
     // Locate the view to the elapsed time on screen and initialize
        timerView = (TextView) findViewById(R.id.timerValue);
        timerView.setText(formatTime(0));
        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in);
        animFadeOut = AnimationUtils.loadAnimation(this, R.anim.anim_fade_out);
        animFadeRepeating = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in_repeating);
        // Initialize an ArrayList to hold the laps
        laps = new Laps();

        // Initialize a Handler for the System to periodically call
        timerHandler = new Handler();
        
        String strDate = dateFunc.getDate();
   
        tglPlayPause = (ToggleButton) findViewById(R.id.tglPlayPause);
        isEnabledToAnimateIn = true;
        isEnabledToAnimateOut = false;
        
        stopButton = findViewById(R.id.reset);
        
        stopButton.setVisibility(View.INVISIBLE);
    	tglPlayPause.setOnCheckedChangeListener(new OnCheckedChangeListener(){
    		public void onCheckedChanged(CompoundButton buttonView,
    				boolean isChecked) {
    	
    			long now = System.currentTimeMillis();
    			
    			if (tglPlayPause !=null){
    				if (isChecked){
    					
    					startRecording();
    					timerprogress();
    					
    					if (isEnabledToAnimateIn == true){
    						tglPlayPause.startAnimation(animFadeRepeating);
    						
    						stopButton.startAnimation(animFadeIn);
    						stopButton.setVisibility(View.VISIBLE);
    						isEnabledToAnimateIn = false;
    						isEnabledToAnimateOut = true;
    					}
    				}
    				else{
    					 // get time button pressed
            
    	   				stopButtonClick(now);		
    	   				stopRecording();
    	   				resetButtonClick(now);
    	   			
    	   		
    					
    				}
    			}
    		}
   		
    	});
         
        stopButton.setOnClickListener(new View.OnClickListener() {			
   			public void onClick(View v) {
   		        // get time button pressed
   				long now = System.currentTimeMillis();

   				stopButtonClick(now);		
   				stopRecording();
   				resetButtonClick(now);
   			
   			}
   		});
        //////////
		/*if(mydb.insertRecording("Kksdf sjf sjopdf spodf psdpof sdf gsrg er", strDate, "", total, 0, 0, true, file_exts[currentFormat],"","C://sdfsdsfd/" )) {
         Toast.makeText(getApplicationContext(), "Recording Added", Toast.LENGTH_SHORT).show(); 
        }  
        else{
         Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show(); 
        }*/
        //////////
        
        
        
        
	}
	


	
	private void timerprogress() {
		//start of progress bar
		//progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
    	progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		textView1 = (TextView) findViewById(R.id.textView1);
		textView3 = (TextView) findViewById(R.id.textView3);
		// Start long running operation in a background thread
		//maxTime = 3360;
		
	
		new Thread(new Runnable() {

            public void run() {
            	
            	
                timerEnd = System.currentTimeMillis() + maxTime * 1000;

                while (timerEnd >  System.currentTimeMillis()) {

                	progressStatus = maxTime - (int) (timerEnd - System.currentTimeMillis() ) / 1000;
                	
                	progressStatusRemaining = maxTime - progressStatus;
                    conversion = progressStatusRemaining/60;
                	
                    // Update the progress bar

                    handler.post(new Runnable() {
                        public void run() {                     
                        	if (isMediaRecording){
                        	progressBar.setProgress(progressStatus);  
                        	textView1.setText(progressStatusRemaining+"");
                        	if (conversion > 1){
                        		textView3.setText(conversion + " Minutes Remaining");
                        	}
                        	else{
                        		textView3.setText(conversion + " Minute Remaining");
                        	}
                       
                     	
                        	
                   	   if (conversion==0)
                       {
                    	   long now = System.currentTimeMillis();
                    	
        	   				stopButtonClick(now);		
        	   				stopRecording();
        	   				resetButtonClick(now); 
                       }
                        }
                        	else{
                        		timerEnd = System.currentTimeMillis() + maxTime * 1000;
                        		
                        		
                        	}
                        }                   
                    });

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                	
                        Log.w("App","Progress thread cannot sleep");
                       
                    }
                }
            	
            
            }
        }).start(); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// Method called when the start buttons pressed
	 private void startButtonClick(long now)
	 {
		 strDefaultRecordingName = "New Recording "+ String.valueOf( mydb.retrieveLastId() + 1);
		 // Remove any timers in progress
	     timerHandler.removeCallbacks(updateTimerTask);
	     
	     // Update the TOD the handler started and the TOD for the first lap started
	     startTime = now;
	     lapStartTime = now;
	     
	     // Note the accumTime (elapsed time) not reset since maybe restarting without reseting
	     
	     // Start timer
	     timerRunning = true;
	     timerHandler.postDelayed(updateTimerTask, timerInterval);
	 }

	 // Method called when the stop button pressed
	 private void stopButtonClick(long now)
	 {		
		tglPlayPause.startAnimation(animFadeIn);
	 	// ignore if stop button pressed when timer already stopped
	 	if (timerRunning == false)
	 	{
	 		return;
	 	}             

	 	// Stop the timer
	     timerHandler.removeCallbacks(updateTimerTask);       
	     accumTime = accumTime + (now - startTime); 
	     timerView.setText(formatTime(accumTime));   
	     timerRunning = false;
	 }

	 // Method called when the reset button pressed.    
	 private void resetButtonClick(long now)
	 {
	 	accumTime = 0;   	

	 	// remove all laps
	 	laps.clear();
	
	 	if (timerRunning)
	 	{
	         startTime = now;
	         lapStartTime = now;
	 	}
	 	
	 }
	
	 // task executed after timer expired
	 private Runnable updateTimerTask = new Runnable() 
	 {	
			public void run() 
			{
				// calculate the elapsed time since timer started and add to accumulated timer time
				long now = System.currentTimeMillis();
				accumTime = accumTime + (now - startTime);  
				
				// set the time the timer restarted
				startTime = now;
				        
				// updated the display of the accumulated time that the timer has been running
				timerView.setText(formatTime(accumTime));                   
				
				// restart the timer
				timerHandler.postDelayed(updateTimerTask, timerInterval);
				
			}  	
		
	 };

		// convert the time in milliseconds to a string of hours, minutes, seconds, and hundreds (thousands rounded up or down)
	 int total;	
	 int seconds = 0; 	
	 int minutes = 0; 
	 int minutesToSec = 0;
	 int hours = 0;
	 private String formatTime(long timeMs)
		{
			String retValue;
			
			//timeMs = timeMs + 5;
			
			seconds = (int) (timeMs / 1000); 	
			minutes = seconds / 60; 
			minutesToSec = minutes * 60;
			hours = minutes / 60;
			minutes = minutes % 60;
			seconds = seconds % 60;
			
			int hundreds = (int)(timeMs % 1000) / 10;
			
			// create the string of hours, minutes, seconds, and hundreds
			
			if (timeMs >= 3600000 ){
				retValue = String.format("%d:%02d:%02d", hours, minutes, seconds); 
			}
			else{
				retValue = String.format("%01d:%02d", minutes, seconds); 
			}
			
			total = (minutesToSec + seconds);
			return retValue;
		}
	 
	 // private class extending ArrayList to hold laps
	 private class Laps extends ArrayList<Long>
	 {   	
			private static final long serialVersionUID = 1L;

			// number of laps to display
			private static final int numberToDisplay = 30;
			
			// Method to format the laps for display or sending    	
	 	public String toString()
	 	{
	 		long totalTime = 0;
	 		
	     	// update display of laps
	     	String lapsString = "LAPS:";
	     	
	     	// display only the last laps
	     	int startLap = 0;
	     	int size = this.size();
	     	if (size > numberToDisplay)
	     	{
	     		startLap = size - numberToDisplay;
	     	}
	     	
	     	for (int i = 0; i < size; i++)
	     	{

	     		long lapTime = this.get(i);
	     		totalTime = totalTime + lapTime;        		
	     		
	     		// only print last numberToDisplay laps
	     		if (i >= startLap) 
	     		{
	     			// two laps per line
	     			if ((i%2) == (startLap%2))
	     			{
	     				lapsString = lapsString + "\n";
	     			}
	     			// display lap number and time

	     			lapsString = lapsString + "\t\t"+ String.format("%02d", i+1) + "\t" + formatTime(lapTime);
	     		}

	     	}
	     	
	     	// add the total time to the end of the laps
	     	lapsString = lapsString + "\n\n" + "Total Lap Times: " + formatTime(totalTime);
	     	
	     	// return the string with the last laps
	     	return lapsString;
	 	}
	 }
        
	

	private void setButtonHandlers() {
		((Button) findViewById(R.id.btnFormat)).setOnClickListener(btnClick);
	}

	private void enableButton(int id, boolean isEnable) {
		((Button) findViewById(id)).setEnabled(isEnable);
	}

	private void enableButtons(boolean isRecording) {

		enableButton(R.id.btnFormat, !isRecording);
	
	}

	private void setFormatButtonCaption() {
		((Button) findViewById(R.id.btnFormat))
				.setText(getString(R.string.audio_format) + " ("
						+ file_exts[currentFormat] + ")");
	}

	private String getFilename() {
		
		
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

	
		return (file.getAbsolutePath() + "/" +  strDefaultRecordingName + file_exts[currentFormat]);
	}
	
	private void rename() {
		if (showDialog == 0){
			showDialog = 1;
		final Context context = this;

		/* Alert Dialog Code Start*/ 	
    	AlertDialog.Builder alert = new AlertDialog.Builder(context);
    	alert.setTitle("Rename Recording"); //Set Alert dialog title here
    	alert.setMessage("File is saved on Recordings tab."); //Message here

        final EditText input = new EditText(context);
        alert.setView(input);
        input.setText(strDefaultRecordingName);
        String filepath = Environment.getExternalStorageDirectory().getPath();
 		final File file = new File(filepath, AUDIO_RECORDER_FOLDER);
 		final String strDate = dateFunc.getDate();
 		
    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    		 		
    			String srt = input.getEditableText().toString();
    			Intent in = new Intent(AudioRecordingActivity.this, TabHostActivity.class);
    			startActivity(in);
    	 
    			final String stripText = mydb.StripText(input.getEditableText().toString());
    			if (file != null && file.exists()) {
    				if (stripText.length() == 0){
    					Toast.makeText(getApplicationContext(), "Cannot Set Empty Text, Saved as "+strDefaultRecordingName, Toast.LENGTH_SHORT).show(); 
    				}
    				else if (mydb.countNameDuplicate(stripText) == 0){
    					File from = new File(getFilename().toString());
    					File to = new File(file.getAbsolutePath().toString() + "/" + stripText + file_exts[currentFormat]); 
    					from.renameTo(to);
                
    					if(mydb.insertRecording(stripText, strDate, "", total, 0, 0, true, file_exts[currentFormat],"",file.getAbsolutePath().toString() + "/" )) {
    						Toast.makeText(getApplicationContext(), "Recording "+stripText+" has been saved", Toast.LENGTH_SHORT).show(); 
    					}  
    					else{
    						if(mydb.insertRecording(strDefaultRecordingName, strDate, "", total, 0, 0, true, file_exts[currentFormat],"",file.getAbsolutePath().toString() + "/" )) {
    							Toast.makeText(getApplicationContext(), "Error renaming record, has been saved as "+strDefaultRecordingName, Toast.LENGTH_SHORT).show(); 
    						}  
    						else{
    							Toast.makeText(getApplicationContext(), "Cannot write on database", Toast.LENGTH_SHORT).show(); 
    						}
    					}
    				} 
    			}
    			else{
    				Toast.makeText(getApplicationContext(), "Cannot write on database", Toast.LENGTH_SHORT).show(); 
    			}
    		} 
    	}); 
    	alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    			showDialog = 0;
    			if(mydb.insertRecording(strDefaultRecordingName, strDate, "", total, 0, 0, true, file_exts[currentFormat],"",file.getAbsolutePath().toString() + "/" )) {
    				Toast.makeText(getApplicationContext(), "Recording saved as "+ strDefaultRecordingName, Toast.LENGTH_SHORT).show(); 
    				dialog.dismiss();
    		  	}  
  				else{
  					Toast.makeText(getApplicationContext(), "Cannot write on database", Toast.LENGTH_SHORT).show(); 
  					dialog.dismiss();
  				}
    			return;
    		}
    	}); 
    	//AlertDialog alertDialog = alert.create();
    	alert.show();	
	}
	}
	
	private void startRecording() {
		
		long now = System.currentTimeMillis();
		startButtonClick(now);
		recorder = new MediaRecorder();

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(output_formats[currentFormat]);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
		recorder.setOutputFile(getFilename());

		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);
		
		try {
			recorder.prepare();
			recorder.start();
			isMediaRecording = true;
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void defaultPlayer(){

		timerView.setText("0:00");
		isMediaRecording = false;
		progressBar.setProgress(0);
		textView3.setText("Tap red circle to start recording");
	}
	
	private void stopRecording() {
		if (null != recorder) {
			recorder.stop();
			recorder.reset();
			recorder.release();
			defaultPlayer();
			
			
			if (isEnabledToAnimateOut == true){
				stopButton.startAnimation(animFadeOut);
				stopButton.setVisibility(View.INVISIBLE);
				isEnabledToAnimateIn = true;
				isEnabledToAnimateOut = false;
			}
			
			recorder = null;
		 	tglPlayPause.setChecked(false);
		}
		rename();
	
	}
	
	private void displayFormatDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String formats[] = { "MPEG 4", "3GPP" };

		builder.setTitle(getString(R.string.choose_format_title))
				.setSingleChoiceItems(formats, currentFormat,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								currentFormat = which;
								setFormatButtonCaption();

								dialog.dismiss();
							}
						}).show();
	}

	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Toast.makeText(AudioRecordingActivity.this,
					"Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			Toast.makeText(AudioRecordingActivity.this,
					"Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
					.show();
		}
	};

	private View.OnClickListener btnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnFormat: {
				displayFormatDialog();

				break;
			}
			}
		}
	};
}