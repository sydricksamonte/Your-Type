package com.blinkedup.transcription;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.text.SimpleDateFormat; 

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import com.pheelicks.utils.TunnelPlayerWorkaround;
import com.pheelicks.visualizer.VisualizerView;
import com.pheelicks.visualizer.renderer.BarGraphRenderer;
import com.pheelicks.visualizer.renderer.CircleBarRenderer;
import com.pheelicks.visualizer.renderer.CircleRenderer;
import com.pheelicks.visualizer.renderer.LineRenderer;


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
	
	 private MediaPlayer mPlayer;
	  private MediaPlayer mSilentPlayer;  /* to avoid tunnel player issue */
	  private VisualizerView mVisualizerView;

	
	//private RecordingDB recordingDBoperation;
	RecordingDB mydb;
	/*public void addUser(View view) {

	

	}*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setButtonHandlers();
		enableButtons(false);
		setFormatButtonCaption();

		mydb = new RecordingDB(this);
		
		 Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM:dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String strDate = sdf.format(c.getTime());
  
        Log.e("asd",strDate);
        
        if(mydb.insertContact("Sampling Record", strDate, "", 3605, 0, 0, true, file_exts[currentFormat],"")) {
             Toast.makeText(getApplicationContext(), "Recording Added", Toast.LENGTH_SHORT).show();	
        }		
        else{
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();	
        }
        
     // Locate the view to the elapsed time on screen and initialize
        timerView = (TextView) findViewById(R.id.timerValue);
        timerView.setText(formatTime(0));
        
        // Initialize an ArrayList to hold the laps
        laps = new Laps();

        // Locate the view to the laps on screen and initialize
 //       lapView = (TextView) findViewById(R.id.laps);
      //  lapView.setText(laps.toString());
        
        // Initialize a Handler for the System to periodically call
        timerHandler = new Handler();
        
        
        tglPlayPause = (ToggleButton) findViewById(R.id.tglPlayPause);
    	tglPlayPause.setOnCheckedChangeListener(new OnCheckedChangeListener(){
    		public void onCheckedChanged(CompoundButton buttonView,
    				boolean isChecked) {
    			long now = System.currentTimeMillis();
    			
    			if (tglPlayPause !=null){
    				if (isChecked)
    					
    					startRecording();
    						
    					
    				else
    			
    				stopButtonClick(now);
    			}
   		}
   		
   	});
        
     // Bind the method to be called when the reset button pressed        
        View stopButton = findViewById(R.id.reset);
        stopButton.setOnClickListener(new View.OnClickListener() {			
   			public void onClick(View v) {
   		        // get time button pressed
   				long now = System.currentTimeMillis();
   				stopButtonClick(now);		
   				stopRecording();
   				resetButtonClick(now);
   			}
   		});

        
    }
	
	
	//Start Visualizer
	
	@Override
	  protected void onResume()
	  {
	    super.onResume();
	    initTunnelPlayerWorkaround();
	    init();
	  }

	  @Override
	  protected void onPause()
	  {
	    cleanUp();
	    super.onPause();
	  }

	  @Override
	  protected void onDestroy()
	  {
	    cleanUp();
	    super.onDestroy();
	  }

	  private void init()
	  {
	    mPlayer = MediaPlayer.create(this, R.raw.test);
	    mPlayer.setLooping(true);
	   // mPlayer.start();
		  mPlayer.stop();
		  
		  recorder = new MediaRecorder();

	    // We need to link the visualizer view to the media player so that
	    // it displays something
	    mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
	    mVisualizerView.link(recorder);

	    // Start with just line renderer
	    addLineRenderer();
	  }

	  private void cleanUp()
	  {
	    if (recorder != null)
	    {
	      mVisualizerView.release();
	      recorder.release();
	      recorder = null;
	    }
	    
	    if (mSilentPlayer != null)
	    {
	      mSilentPlayer.release();
	      mSilentPlayer = null;
	    }
	  }
	  
	  // Workaround (for Galaxy S4)
	  //
	  // "Visualization does not work on the new Galaxy devices"
	  //    https://github.com/felixpalmer/android-visualizer/issues/5
	  //
	  // NOTE: 
	  //   This code is not required for visualizing default "test.mp3" file,
	  //   because tunnel player is used when duration is longer than 1 minute.
	  //   (default "test.mp3" file: 8 seconds)
	  //
	  private void initTunnelPlayerWorkaround() {
	    // Read "tunnel.decode" system property to determine
	    // the workaround is needed
	    if (TunnelPlayerWorkaround.isTunnelDecodeEnabled(this)) {
	      mSilentPlayer = TunnelPlayerWorkaround.createSilentMediaPlayer(this);
	    }
	  }

	  // Methods for adding renderers to visualizer
	  private void addBarGraphRenderers()
	  {
	    Paint paint = new Paint();
	    paint.setStrokeWidth(50f);
	    paint.setAntiAlias(true);
	    paint.setColor(Color.argb(200, 56, 138, 252));
	    BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(16, paint, false);
	    mVisualizerView.addRenderer(barGraphRendererBottom);

	    Paint paint2 = new Paint();
	    paint2.setStrokeWidth(12f);
	    paint2.setAntiAlias(true);
	    paint2.setColor(Color.argb(200, 181, 111, 233));
	    BarGraphRenderer barGraphRendererTop = new BarGraphRenderer(4, paint2, true);
	    mVisualizerView.addRenderer(barGraphRendererTop);
	  }

	  private void addCircleBarRenderer()
	  {
	    Paint paint = new Paint();
	    paint.setStrokeWidth(8f);
	    paint.setAntiAlias(true);
	    paint.setXfermode(new PorterDuffXfermode(Mode.LIGHTEN));
	    paint.setColor(Color.argb(255, 222, 92, 143));
	    CircleBarRenderer circleBarRenderer = new CircleBarRenderer(paint, 32, true);
	    mVisualizerView.addRenderer(circleBarRenderer);
	  }

	  private void addCircleRenderer()
	  {
	    Paint paint = new Paint();
	    paint.setStrokeWidth(3f);
	    paint.setAntiAlias(true);
	    paint.setColor(Color.argb(255, 222, 92, 143));
	    CircleRenderer circleRenderer = new CircleRenderer(paint, true);
	    mVisualizerView.addRenderer(circleRenderer);
	  }

	  private void addLineRenderer()
	  {
	    Paint linePaint = new Paint();
	    linePaint.setStrokeWidth(1f);
	    linePaint.setAntiAlias(true);
	    linePaint.setColor(Color.argb(88, 0, 128, 255));

	    Paint lineFlashPaint = new Paint();
	    lineFlashPaint.setStrokeWidth(5f);
	    lineFlashPaint.setAntiAlias(true);
	    lineFlashPaint.setColor(Color.argb(188, 255, 255, 255));
	    LineRenderer lineRenderer = new LineRenderer(linePaint, lineFlashPaint, true);
	    mVisualizerView.addRenderer(lineRenderer);
	  }

	  // Actions for buttons defined in xml
	  public void startPressed(View view) throws IllegalStateException, IOException
	  {
	   
		  recorder.prepare();
		  recorder.start();
	  }

	  public void stopPressed(View view)
	  {
		  recorder.stop();
	  }

	  public void barPressed(View view)
	  {
	    addBarGraphRenderers();
	  }

	  public void circlePressed(View view)
	  {
	    addCircleRenderer();
	  }

	  public void circleBarPressed(View view)
	  {
	    addCircleBarRenderer();
	  }

	  public void linePressed(View view)
	  {
	    addLineRenderer();
	  }

	  public void clearPressed(View view)
	  {
	    mVisualizerView.clearRenderers();
	  }
	
	
	//End Visualizer
	
    
	// Method called when the start buttons pressed
	 private void startButtonClick(long now)
	 {       
		 
		 
		 
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
	 	// ignore if stop button pressed when timer already stopped
	 	if (timerRunning == false)
	 	{
	 		return;
	 	}             

	 	// Stop the timer
	     timerHandler.removeCallbacks(updateTimerTask);       

	  
	 	
			// calculate the elapsed time since timer started and add to accumulated timer time
	     // note that if the timer pops after we have started this method then startTime will 
	     // 		 be greater than now resulting in any additional time being subtracted off
			accumTime = accumTime + (now - startTime); 
			// updated the display of the accumulated time
			timerView.setText(formatTime(accumTime));   
			        
	     // indicate timer stopped
	     timerRunning = false;
	 }

	 // Method called when the reset button pressed.    
	 private void resetButtonClick(long now)
	 {
	 	// Set the elapsed time to 0..
	 	accumTime = 0;   	

	 	// remove all laps
	 	laps.clear();
	 //	timerView.setText(formatTime(accumTime));
	
	 	
	
	 	
	 	// Initialize the elapsed time
	//	timerView.setText(formatTime(0));
	 	
	     // Update the TOD the handler started and the TOD the first lap started
	 	// Note that this is only necessary if reset pressed while timer running
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
	 private String formatTime(long timeMs)
		{
			String retValue;
			
			// add 5 ms to round thousands up or down (i.e. 5ms -> 0.01)
			timeMs = timeMs + 5;
			
			// seconds equals milliseconds / 1000
			int seconds = (int) (timeMs / 1000); 	
			// minutes equals second / 60
			int minutes = seconds / 60; 
			int minutesToSec = minutes * 60;
			// hours equal minutes / 60
			int hours = minutes / 60;
			// find the left over minutes
			minutes = minutes % 60;
			// find the left over seconds
			seconds = seconds % 60;
			// calculate the hundreds rounding the thousands up or down after previously adding 5
			int hundreds = (int)(timeMs % 1000) / 10;
			
			// create the string of hours, minutes, seconds, and hundreds
			retValue = String.format("%d:%02d:%02d", hours, minutes, seconds);       
			
			total = (minutesToSec + seconds);
			System.out.println(total);
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

		
		return (file.getAbsolutePath() + "/" + "REC1" + file_exts[currentFormat]);	

	
	}
	
	
	private void rename() {
		 String srt = "";
		// this context will use when we work with Alert Dialog
				final Context context = this;

		/* Alert Dialog Code Start*/ 	
    	AlertDialog.Builder alert = new AlertDialog.Builder(context);
    	alert.setTitle("Alert Dialog With EditText"); //Set Alert dialog title here
    	alert.setMessage("Enter Your Name Here"); //Message here

        // Set an EditText view to get user input 
        final EditText input = new EditText(context);
        alert.setView(input);

    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    	 //You will get as string input data in this variable.
    	 // here we convert the input to a string and show in a toast.
    		 		
    	 String srt = input.getEditableText().toString();
    	 Toast.makeText(context,srt,Toast.LENGTH_LONG).show();    
    	 
    	 String filepath = Environment.getExternalStorageDirectory().getPath();
 		File file = new File(filepath, AUDIO_RECORDER_FOLDER);
     	//	File oldfile =new File(getFilename());
     	//  File newfile =new File(file.getAbsolutePath() + "/" + "im_new" + file_exts[currentFormat]);
     	 
     	  if (file != null && file.exists()) {
               File from = new File(getFilename().toString());
               File to = new File(file.getAbsolutePath().toString() + "/" + input.getEditableText().toString() + file_exts[currentFormat]); 
               from.renameTo(to);
               
           //	ArrayAdapter adapter = (ArrayAdapter) getListAdapter();

    		//EditText text = (EditText) findViewById(R.id.editText1);

    		//adapter.add(rec);
               
               Calendar c = Calendar.getInstance();
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM:dd HH:mm:ss");
               String strDate = sdf.format(c.getTime());
               
               if(mydb.insertContact(input.getEditableText().toString(), strDate, "", total, 0, 0, true, file_exts[currentFormat],"")) {
                    Toast.makeText(getApplicationContext(), "Recording Added", Toast.LENGTH_SHORT).show();	
               }		
               else{
                   Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();	
                }	
           }

    	 
    	} // End of onClick(DialogInterface dialog, int whichButton)
    }); //End of alert.setPositiveButton
    	alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    		  dialog.cancel();
    	  }
    }); //End of alert.setNegativeButton
    	AlertDialog alertDialog = alert.create();
    	alertDialog.show();
    	
    	

	}
	
	private void startRecording() {
		long now = System.currentTimeMillis();
		startButtonClick(now);
		recorder = new MediaRecorder();
		
		mVisualizerView.clearRenderers();
		addCircleRenderer();
		

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(output_formats[currentFormat]);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
		recorder.setOutputFile(getFilename());

		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);

		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecording() {
		
		mVisualizerView.clearRenderers();
		
		if (null != recorder) {
			recorder.stop();
			recorder.reset();
			recorder.release();

			recorder = null;
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