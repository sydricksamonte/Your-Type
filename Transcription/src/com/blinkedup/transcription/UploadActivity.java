package com.blinkedup.transcription;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class UploadActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		
		TextView tc_recId = (TextView)findViewById(R.id.recId);
	 	TextView tc_recName = (TextView)findViewById(R.id.recName);
		TextView tc_recStat = (TextView)findViewById(R.id.recStatDesc1);
		TextView tc_recDateAdd = (TextView)findViewById(R.id.recDateAdd);
	  	TextView tc_recDurat = (TextView)findViewById(R.id.recDurat);
		TextView tc_recFileType = (TextView)findViewById(R.id.recFileType);
	 	TextView tc_recOrigin = (TextView)findViewById(R.id.recOrigin);
	  	TextView tc_recPath = (TextView)findViewById(R.id.recPath);
	        
	 
	  	
		Intent intent = getIntent();
		String rec_id = (String) intent.getSerializableExtra("INTENT_UPLOAD_RECORDING_ID");
		tc_recId.setText(rec_id);
		
		String recName = (String) intent.getSerializableExtra("INTENT_UPLOAD_RECORDING_NAME");
		tc_recName.setText(recName);
		tc_recName.setGravity(Gravity.CENTER);


		
		String recDateAdd = (String) intent.getSerializableExtra("INTENT_UPLOAD_DATE_ADDED");
		tc_recDateAdd.setText(recDateAdd);
		
		String recStat = (String) intent.getSerializableExtra("INTENT_UPLOAD_STATUS");
		
		
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
		
		String recDurat = (String) intent.getSerializableExtra("INTENT_UPLOAD_DURATION");
		tc_recDurat.setText(recDurat);
		
		String recFileType = (String) intent.getSerializableExtra("INTENT_UPLOAD_FILE_TYPE");
		tc_recFileType.setText(recFileType);
		
		String recOrigin = (String) intent.getSerializableExtra("INTENT_UPLOAD_ORIGIN");
		tc_recOrigin.setText(recOrigin);
		
		String recPath = (String) intent.getSerializableExtra("INTENT_UPLOAD_PATH");
		tc_recPath.setText(recPath);
		
	}
	
	//Button btnPlayDyn = (Button)findViewById(R.id.btnPlay);
	//btnPlayDyn.setLayoutParams(new LayoutParams(300, 300));
	
	
}
