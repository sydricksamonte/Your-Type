package com.blinkedup.transcription;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat; 

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
	
	private TextView timerValue;
	
	private long startTime = 0L;
	
	private Handler customHandler = new Handler();
	
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	
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
		
		timerValue = (TextView) findViewById(R.id.timerValue);
		mydb = new RecordingDB(this);
		
		 Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM:dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String strDate = sdf.format(c.getTime());
  
        Log.e("asd",strDate);
        
        if(mydb.insertContact("Sampling Record", strDate, "", 0, 0, 0, true, file_exts[currentFormat],"")) {
             Toast.makeText(getApplicationContext(), "Recording Added", Toast.LENGTH_SHORT).show();	
        }		
        else{
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();	
        }
        
	}

	private void setButtonHandlers() {
		((Button) findViewById(R.id.btnStart)).setOnClickListener(btnClick);
		((Button) findViewById(R.id.btnStop)).setOnClickListener(btnClick);
		((Button) findViewById(R.id.btnFormat)).setOnClickListener(btnClick);
	}

	private void enableButton(int id, boolean isEnable) {
		((Button) findViewById(id)).setEnabled(isEnable);
	}

	private void enableButtons(boolean isRecording) {
		enableButton(R.id.btnStart, !isRecording);
		enableButton(R.id.btnFormat, !isRecording);
		enableButton(R.id.btnStop, isRecording);
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
               
               if(mydb.insertContact(input.getEditableText().toString(), strDate, "", 0, 0, 0, true, file_exts[currentFormat],"")) {
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
		
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);
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
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecording() {
		
		timeSwapBuff += timeInMilliseconds;
		customHandler.removeCallbacks(updateTimerThread);
		
		if (null != recorder) {
			recorder.stop();
			recorder.reset();
			recorder.release();

			recorder = null;
		}

		 rename();
		 
	}
	
	private Runnable updateTimerThread = new Runnable() {

		public void run() {
			
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			
			updatedTime = timeSwapBuff + timeInMilliseconds;

			int secs = (int) (updatedTime / 1000);
			int mins = secs / 60;
			secs = secs % 60;
			int milliseconds = (int) (updatedTime % 1000);
			timerValue.setText("" + mins + ":"
					+ String.format("%02d", secs));
			customHandler.postDelayed(this, 0);
		}

	};

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
			case R.id.btnStart: {
				Toast.makeText(AudioRecordingActivity.this, "Start Recording",
						Toast.LENGTH_SHORT).show();

				enableButtons(true);
				startRecording();

				break;
			}
			case R.id.btnStop: {
				Toast.makeText(AudioRecordingActivity.this, "Stop Recording",
						Toast.LENGTH_SHORT).show();
				enableButtons(false);
				stopRecording();

				break;
			}
			case R.id.btnFormat: {
				displayFormatDialog();

				break;
			}
			}
		}
	};
}