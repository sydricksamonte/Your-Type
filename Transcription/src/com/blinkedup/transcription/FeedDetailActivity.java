package com.blinkedup.transcription;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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
	Boolean isPlaying = false;
	TextView tc_recDurat;
	TextView tc_recDuratBack;
	DateUtils du;
	
	@Override
	public void onPause() {
	    super.onPause();
	    mediaPlayer.stop();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feeddetail);
		
		du = new DateUtils();
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
		String rec_id = (String) intent.getSerializableExtra("INTENT_RECORDING_ID");
		tc_recId.setText(rec_id);
		
		String recName = (String) intent.getSerializableExtra("INTENT_RECORDING_NAME");
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
		
		String recFileType = (String) intent.getSerializableExtra("INTENT_FILE_TYPE");
		tc_recFileType.setText(recFileType);
		
		String recOrigin = (String) intent.getSerializableExtra("INTENT_ORIGIN");
		tc_recOrigin.setText(recOrigin);
		
		recPath = (String) intent.getSerializableExtra("INTENT_PATH");
		tc_recPath.setText(recPath);
		//LoadMusic();
		if (mediaPlayer == null){
	        // it's ok, we can call this constructor
			mediaPlayer = new MediaPlayer();  
		
		Log.e("EEEE","CALLED!!!!!!!!!!!");
		}
		try {
			mediaPlayer.setDataSource(recPath);
			mediaPlayer.prepare();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), e1.getLocalizedMessage(), Toast.LENGTH_LONG);
		}
		
		ImageButton mClickButton1 = (ImageButton)findViewById(R.id.btnPlay);
		mClickButton1.setOnClickListener( new OnClickListener() {
			 @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
				 if (mediaPlayer !=null){
						try {
							
						
							seekBar.setMax(mediaPlayer.getDuration());
							mediaPlayer.start();
							
							seekUpdate();
		
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						
				}
			 }
	   });
		
		ImageButton mClickButton2 = (ImageButton)findViewById(R.id.btnStop);
		mClickButton2.setOnClickListener( new OnClickListener() {
			 @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
				 mediaPlayer.pause();
			 }
	            
	   });
		
		ImageButton mClickButton5 = (ImageButton)findViewById(R.id.btnUpload);
		mClickButton5.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	Log.e("OI","sdfsdf");
            	TextView tc_recId = (TextView)findViewById(R.id.recId);
        	 	TextView tc_recName = (TextView)findViewById(R.id.recName);
        		TextView tc_recStat = (TextView)findViewById(R.id.recStatDesc1);
        		TextView tc_recDateAdd = (TextView)findViewById(R.id.recDateAdd);
        	  	TextView tc_recDurat = (TextView)findViewById(R.id.recDurat);
        		TextView tc_recFileType = (TextView)findViewById(R.id.recFileType);
        	 	TextView tc_recOrigin = (TextView)findViewById(R.id.recOrigin);
        	  	TextView tc_recPath = (TextView)findViewById(R.id.recPath);
        	      
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
        });
		

		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekVolume = (SeekBar) findViewById(R.id.volShow);
	
		//btnVolUp = (Button) findViewById(R.id.btnVolUp);
		//btnVolDown = (Button) findViewById(R.id.btnVolDown);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		initialiseVolumeSeekBar();
		
		if (mediaPlayer !=null) seekUpdate();
		
		seekBar.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				seekChange(v);
				return false;
			}
		});
	}
	private void initialiseVolumeSeekBar() {

        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekVolume.setMax(maxVolume);
        seekVolume.setProgress(curVolume);
        seekVolume
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar arg0) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar arg0) {

                    }

                    @Override
                    public void onProgressChanged(SeekBar arg0, int arg1,
                            boolean arg2) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                arg1, 0);
                    }
                });
    }
		
	private void seekChange(View v){
		if ((mediaPlayer !=null)){
				SeekBar sb= (SeekBar) v;
				mediaPlayer.seekTo(sb.getProgress());
				tc_recDurat.setText(du.convIntBaseToLength(mediaPlayer.getCurrentPosition() / 1000));
				tc_recDuratBack.setText(du.convIntBaseToLength((mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition())/1000));
				if (sb.getProgress() == 100){
					sb.setProgress(0);
				}
		}
	}

	Runnable run = new Runnable(){
		@Override
		public void run() {
			seekUpdate();
		}
	};
		
	private void seekUpdate(){
		seekBar.setProgress(mediaPlayer.getCurrentPosition());
		tc_recDurat.setText(du.convIntBaseToLength(mediaPlayer.getCurrentPosition() / 1000));
		tc_recDuratBack.setText(du.convIntBaseToLength((mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition())/1000));
		seekHandler.postDelayed(run, 1000);
	}
}
