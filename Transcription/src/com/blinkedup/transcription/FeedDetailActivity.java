package com.blinkedup.transcription;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FeedDetailActivity extends Activity{
	
	SeekBar seekBar;
	SeekBar seekVolume;
	
	private int maxVolume;
	private int curVolume;
	    
	ToggleButton tglPlayPause;
	
	MediaPlayer mediaPlayer;
	AudioManager audioManager;
	Music musicSelected;
	Handler seekHandler = new Handler();
	String recPath;
	String fileLoc;
	String recName; 
	String recFileType;
	
	Boolean isPlaying = false;
	TextView tc_recDurat;
	TextView tc_recDuratBack;
	DateUtils du;
	RecordingDB mydb;
	ImageButton dynBtnPlay;
	ImageButton dynBtnPause;
	ImageButton dynBtnDelete;
	ImageButton dynBtnRename;
	ImageButton dynBtnUpload;
	
	String rec_id;
	
	@Override
	public void onPause() {
	    super.onPause();
	    if (mediaPlayer != null){
	    mediaPlayer.stop();
	    mediaPlayer.reset();
	    mediaPlayer.release();
	    }
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feeddetail);
		 final Context context = this;
		du = new DateUtils();
		mydb = new RecordingDB(this);
		
		TextView tc_recId = (TextView)findViewById(R.id.recDetId);
	 	TextView tc_recName = (TextView)findViewById(R.id.recDetName);
		TextView tc_recStat = (TextView)findViewById(R.id.recDetStatDesc1);
		TextView tc_recDateAdd = (TextView)findViewById(R.id.recDetDateAdd);
	    tc_recDurat = (TextView)findViewById(R.id.recDetDurat);
	    tc_recDuratBack = (TextView)findViewById(R.id.recDetDuratBack);
		TextView tc_recDateFin = (TextView)findViewById(R.id.recDetDateFin);
	 	TextView tc_recDateUploaded = (TextView)findViewById(R.id.recDetDateUploaded);
		TextView tc_recFileType = (TextView)findViewById(R.id.recDetFileType);
	 	TextView tc_recOrigin = (TextView)findViewById(R.id.recDetOrigin);
	  	TextView tc_recPath = (TextView)findViewById(R.id.recDetPath);
	      
	  	Intent intent = getIntent();
		rec_id = (String) intent.getSerializableExtra("INTENT_RECORDING_ID");
		tc_recId.setText(rec_id);
		
		recName = (String) intent.getSerializableExtra("INTENT_RECORDING_NAME");
		tc_recName.setText(recName);
		tc_recName.setGravity(Gravity.CENTER);

		String recDateAdd = (String) intent.getSerializableExtra("INTENT_DATE_ADDED");
		tc_recDateAdd.setText(recDateAdd);
		
		String recStat = (String) intent.getSerializableExtra("INTENT_STATUS");
		String statDesc = "";
		Drawable img;
		
		if (recStat.equals("1")){
			statDesc = "Uploaded — Awaiting Process";
			img = getResources().getDrawable( R.drawable.colors_orange );
			img.setBounds( 0, 0, 12, 12 );
			tc_recStat.setCompoundDrawables( img, null, null, null );
		}
		else if (recStat.equals("2")){
			statDesc = "Transcription Done";
			img = getResources().getDrawable( R.drawable.colors_green );
			img.setBounds( 0, 0, 12, 12 );
			tc_recStat.setCompoundDrawables( img, null, null, null );
		}
		else{
			statDesc = "Waiting for Upload";
			img = getResources().getDrawable( R.drawable.colors_gray );
			img.setBounds( 0, 0, 12, 12 );
			tc_recStat.setCompoundDrawables( img, null, null, null );
		}
		tc_recStat.setText(statDesc);
		
		String recDurat = (String) intent.getSerializableExtra("INTENT_DURATION");
		tc_recDurat.setText(recDurat);
		
		Toast.makeText(getApplicationContext(), "You selected", Toast.LENGTH_LONG);
		String recDateFin = (String) intent.getSerializableExtra("INTENT_DATE_FINALIZED");
		tc_recDateFin.setText(recDateFin);
		
		String recDateUploaded = (String) intent.getSerializableExtra("INTENT_DATE_UPLOADED");
		tc_recDateUploaded.setText(recDateUploaded);
		
		recFileType = (String) intent.getSerializableExtra("INTENT_FILE_TYPE");
		tc_recFileType.setText(recFileType);
		
		String recOrigin = (String) intent.getSerializableExtra("INTENT_ORIGIN");
		tc_recOrigin.setText(recOrigin);
		
		recPath = (String) intent.getSerializableExtra("INTENT_PATH");
		fileLoc = recPath + recName  + recFileType;
		tc_recPath.setText(recPath + recName  + recFileType);
		Log.e("SSS",recPath + recName  + recFileType);
		dynBtnPlay = (ImageButton)findViewById(R.id.btnPlay);
		dynBtnPause = (ImageButton)findViewById(R.id.btnStop);
		dynBtnDelete = (ImageButton)findViewById(R.id.btnDelete);
		dynBtnRename = (ImageButton)findViewById(R.id.btnRename);
		dynBtnUpload = (ImageButton)findViewById(R.id.btnUpload);
	
			
			try {
				if (mediaPlayer == null){
			        // it's ok, we can call this constructor
					mediaPlayer = new MediaPlayer();  
				}
				mediaPlayer.setDataSource(fileLoc);
				mediaPlayer.prepare();
			
				 dynBtnPlay.setEnabled(true);
				 dynBtnPause.setEnabled(true);
				 dynBtnDelete.setEnabled(true);
				 dynBtnUpload.setEnabled(true);
				 dynBtnRename.setEnabled(true);
			
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(), "Cannot locate file",
						   Toast.LENGTH_LONG).show();
				 tc_recDuratBack.setText("00:00");
				 mediaPlayer = null;
				 dynBtnPlay.setEnabled(false);
				 dynBtnPause.setEnabled(false);
				 dynBtnUpload.setEnabled(false);
				 dynBtnRename.setEnabled(false);
			}
			catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(), e1.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			}
			catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(), e1.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			}
		
		
		
	   
			dynBtnPlay.setOnClickListener( new OnClickListener() {
			
			 @Override
	            public void onClick(View v) {
				 dynBtnPlay.setColorFilter(Color.argb(150, 155, 155, 155));
	                // TODO Auto-generated method stub
				 		if (mediaPlayer !=null){
								try {
									seekBar.setMax(mediaPlayer.getDuration());
									mediaPlayer.start();
							
									seekUpdate();
								}
								catch (IllegalStateException e1) {
									// TODO Auto-generated catch block
									Toast.makeText(getApplicationContext(), e1.getLocalizedMessage(), Toast.LENGTH_LONG).show();
								}
								catch (IllegalArgumentException e1) {
									// TODO Auto-generated catch block
									Toast.makeText(getApplicationContext(), e1.getLocalizedMessage(), Toast.LENGTH_LONG).show();
								}
								dynBtnPlay.setColorFilter(null);
				 		}
				 		
				}
			});
		
			dynBtnDelete.setOnClickListener( new OnClickListener() {
				 @Override
		            public void onClick(View v) {
		                // TODO Auto-generated method stub
					
					 try {
						 String fileName	= recName  + recFileType;
							
						 File file = new File(recPath,  fileName);
						 boolean deleted = file.delete();
						
						 	if(mydb.deleteRecording(rec_id)) {
						 		
						 		if (mediaPlayer != null){
						 			if (deleted){
						 				Toast.makeText(getApplicationContext(), "Recording Deleted", Toast.LENGTH_SHORT).show();
						 				Intent explicitBackIntent = new Intent(FeedDetailActivity.this,
						     	        		TabHostActivity.class);
						 				startActivity(explicitBackIntent);
						 			}  
						 		}
						 		else{
						 			Toast.makeText(getApplicationContext(), "Recording Deleted", Toast.LENGTH_SHORT).show(); 
						 			Intent explicitBackIntent = new Intent(FeedDetailActivity.this,
					     	        		TabHostActivity.class);
					 				startActivity(explicitBackIntent);
						 		}
						 	}
							else{
								Toast.makeText(getApplicationContext(), "Cannot locate recording profile", Toast.LENGTH_SHORT).show(); 
							}
					 }
					 catch (IllegalStateException e1) {
							// TODO Auto-generated catch block
							//Toast.makeText(getApplicationContext(), e1.getLocalizedMessage(), Toast.LENGTH_LONG).show();
					 }	
				 }       
		   });
			
			dynBtnRename.setOnClickListener( new OnClickListener() {
				 @Override
		            public void onClick(View v) {
		                // TODO Auto-generated method stub
					if (mediaPlayer != null){
						try {
						 	AlertDialog.Builder alert = new AlertDialog.Builder(context);
					    	alert.setTitle("Rename Recording"); //Set Alert dialog title here

					        // Set an EditText view to get user input 
					        final EditText input = new EditText(context);
					        alert.setView(input);

					    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					    		public void onClick(DialogInterface dialog, int whichButton) {
					
					    			String fileName	= recName  + recFileType;
					    			File file = new File(recPath,  fileName);
								
					    			if (file != null && file.exists()) {
					    				File to = new File(recPath + "/" + input.getEditableText().toString() + recFileType); 
					    				if (file.renameTo(to)){
					    					if(mydb.renameRecording(rec_id,input.getEditableText().toString() )) {
					    						Toast.makeText(getApplicationContext(), "Renamed Successfully", Toast.LENGTH_SHORT).show(); 
					    					}  
					    					else{
					    						Toast.makeText(getApplicationContext(), "Cannot Rename File", Toast.LENGTH_SHORT).show(); 
					    					}
					    				}
					    				else{
					    					Toast.makeText(getApplicationContext(), "Cannot Rename File", Toast.LENGTH_SHORT).show(); 
					    				}
					    			}
					    		}
					    	});
						}
						catch (IllegalStateException e1) {
							Toast.makeText(getApplicationContext(), e1.getLocalizedMessage(), Toast.LENGTH_LONG).show();
						}	
				    
					}
				 }
		   });
			 
			dynBtnPause.setOnClickListener( new OnClickListener() {
			 @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
				 try {
					 	if (mediaPlayer != null){
					 		mediaPlayer.pause();
					 	}
					 }
				 catch (IllegalStateException e1) {
						// TODO Auto-generated catch block
						//Toast.makeText(getApplicationContext(), e1.getLocalizedMessage(), Toast.LENGTH_LONG).show();
				}	
			 }
		});
		
	
		dynBtnUpload.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick(View v) {
            	if (mediaPlayer != null){
                // TODO Auto-generated method stub
            	TextView tc_recId = (TextView)findViewById(R.id.recDetId);
        	 	TextView tc_recName = (TextView)findViewById(R.id.recDetName);
        		TextView tc_recStat = (TextView)findViewById(R.id.recDetStatDesc1);
        		TextView tc_recDateAdd = (TextView)findViewById(R.id.recDetDateAdd);
        	  	TextView tc_recDurat = (TextView)findViewById(R.id.recDetDurat);
        		TextView tc_recFileType = (TextView)findViewById(R.id.recDetFileType);
        	 	TextView tc_recOrigin = (TextView)findViewById(R.id.recDetOrigin);
        	  	TextView tc_recPath = (TextView)findViewById(R.id.recDetPath);
        	      
        	  	Intent explicitIntent = new Intent(FeedDetailActivity.this,
     	        		UploadActivity.class);
     	      
     	        explicitIntent.putExtra("INTENT_UPLOAD_RECORDING_ID",tc_recId.getText().toString());
     			explicitIntent.putExtra("INTENT_UPLOAD_RECORDING_NAME",tc_recName.getText().toString());
     			explicitIntent.putExtra("INTENT_UPLOAD_DATE_ADDED",tc_recDateAdd.getText().toString());
     	
     			explicitIntent.putExtra("INTENT_UPLOAD_DURATION",tc_recDurat.getText().toString());
     			explicitIntent.putExtra("INTENT_UPLOAD_STATUS",tc_recStat.getText().toString());
     			explicitIntent.putExtra("INTENT_UPLOAD_ORIGIN",tc_recOrigin.getText().toString());
     			explicitIntent.putExtra("INTENT_UPLOAD_FILE_TYPE",tc_recFileType.getText().toString());
     		
     			explicitIntent.putExtra("INTENT_UPLOAD_PATH",tc_recPath.getText().toString());
     			startActivity(explicitIntent);
            	}
            }
        });
		

		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekVolume = (SeekBar) findViewById(R.id.volShow);
	
		//btnVolUp = (Button) findViewById(R.id.btnVolUp);
		//btnVolDown = (Button) findViewById(R.id.btnVolDown);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		initialiseVolumeSeekBar();
		
		
		try{
		if (mediaPlayer !=null){
			seekUpdate();
		}
		
		seekBar.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				seekChange(v);
				return false;
			}
		});
		}
		catch(Exception e){
			
		}
	}
	private void initialiseVolumeSeekBar() {

        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekVolume.setMax(maxVolume);
        seekVolume.setProgress(curVolume);
        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        	@Override
        	public void onStopTrackingTouch(SeekBar arg0) {
        	}

        	@Override
        	public void onStartTrackingTouch(SeekBar arg0) {

          	}

          	@Override
        	public void onProgressChanged(SeekBar arg0, int arg1,boolean arg2) {
          		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,arg1, 0);
          	}
      	});
    }
		
	private void seekChange(View v){
		if ((mediaPlayer !=null)){
			try{
				SeekBar sb= (SeekBar) v;
				mediaPlayer.seekTo(sb.getProgress());
				tc_recDurat.setText(du.convIntBaseToLength(mediaPlayer.getCurrentPosition() / 1000));
				tc_recDuratBack.setText(du.convIntBaseToLength((mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition())/1000));
				if (sb.getProgress() == 100){
					sb.setProgress(0);
				}
			}
			catch(Exception e){
				
			}
		}
	}
	
	Runnable run = new Runnable(){
		@Override
		public void run() {
			try{
				seekUpdate();
			}
			catch(Exception e){
			}
		}
	};
		
	private void seekUpdate(){
		try{
			if (mediaPlayer != null){
				seekBar.setProgress(mediaPlayer.getCurrentPosition());
				tc_recDurat.setText(du.convIntBaseToLength(mediaPlayer.getCurrentPosition() / 1000));
				tc_recDuratBack.setText(du.convIntBaseToLength((mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition())/1000));
				seekHandler.postDelayed(run, 1000);
			}
		}
		catch (Exception e){
		}
	}
}
