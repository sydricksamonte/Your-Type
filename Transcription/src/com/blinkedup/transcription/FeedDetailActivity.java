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
import android.view.View;
import android.view.View.OnClickListener;
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
	Button btnVolUp, btnVolDown;
	ToggleButton tglPlayPause;
	
	MediaPlayer mediaPlayer;
	AudioManager audioManager;
	Music musicSelected;
	Handler seekHandler = new Handler();
	String recPath;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feeddetail);
		
		TextView tc_recId = (TextView)findViewById(R.id.recId);
	 	TextView tc_recName = (TextView)findViewById(R.id.recName);
		TextView tc_recStat = (TextView)findViewById(R.id.recStatDesc1);
		TextView tc_recDateAdd = (TextView)findViewById(R.id.recDateAdd);
	  	TextView tc_recDurat = (TextView)findViewById(R.id.recDurat);
		TextView tc_recDateFin = (TextView)findViewById(R.id.recDateFin);
	 	TextView tc_recDateUploaded = (TextView)findViewById(R.id.recDateUploaded);
		TextView tc_recFileType = (TextView)findViewById(R.id.recFileType);
	 	TextView tc_recOrigin = (TextView)findViewById(R.id.recOrigin);
	  	TextView tc_recPath = (TextView)findViewById(R.id.recPath);
	        
	 
	  	
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
		
		Log.w("oida!",recPath);
		mediaPlayer = new MediaPlayer();
		
		ImageButton mClickButton1 = (ImageButton)findViewById(R.id.btnPlay);
		mClickButton1.setOnClickListener( new OnClickListener() {
			 @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
				 
				 try {
					
					mediaPlayer.setDataSource(recPath);
					mediaPlayer.prepare();
					seekBar.setMax(mediaPlayer.getDuration());
					mediaPlayer.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
					
					//Toast.makeText(getApplicationContext(),  e.printStackTrace(), Toast.LENGTH_LONG);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
					
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
					
				}
					
					
				
			 }
	            
	   });
		
		ImageButton mClickButton2 = (ImageButton)findViewById(R.id.btnStop);
		mClickButton2.setOnClickListener( new OnClickListener() {
			 @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
				 mediaPlayer.stop();
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
	
		//btnVolUp = (Button) findViewById(R.id.btnVolUp);
		//btnVolDown = (Button) findViewById(R.id.btnVolDown);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		
	}
	private void seekChange(View v){
		if (mediaPlayer !=null && mediaPlayer.isPlaying()){
			SeekBar sb= (SeekBar) v;
			mediaPlayer.seekTo(sb.getProgress());
		}
	}
	private void LoadMusic(){
	
		
	}
	
	Runnable run = new Runnable(){

		@Override
		public void run() {

			seekUpdate();
			
			
		}
		
	};
	
	private void seekUpdate(){
		seekBar.setProgress(mediaPlayer.getCurrentPosition());
		seekHandler.postDelayed(run, 1000);
	}
	
//Button btnPlayDyn = (Button)findViewById(R.id.btnPlay);
	//btnPlayDyn.setLayoutParams(new LayoutParams(300, 300));
	
	
}
