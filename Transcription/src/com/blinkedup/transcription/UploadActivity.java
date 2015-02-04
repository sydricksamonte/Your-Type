package com.blinkedup.transcription;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;

public class UploadActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		
		//TextView tc_recName = (TextView)findViewById(R.id.recUploadName);
		EditText tc_recName = (EditText)findViewById(R.id.txtDesc);
		Intent intent = getIntent();
		String rec_id = (String) intent.getSerializableExtra("INTENT_UPLOAD_RECORDING_ID");
		String recName = (String) intent.getSerializableExtra("INTENT_UPLOAD_RECORDING_NAME");
		String recDateAdd = (String) intent.getSerializableExtra("INTENT_UPLOAD_DATE_ADDED");
		String recStat = (String) intent.getSerializableExtra("INTENT_UPLOAD_STATUS");
		String recDurat = (String) intent.getSerializableExtra("INTENT_UPLOAD_DURATION");
		String recFileType = (String) intent.getSerializableExtra("INTENT_UPLOAD_FILE_TYPE");
		String recPath = (String) intent.getSerializableExtra("INTENT_UPLOAD_PATH");
		tc_recName.requestFocus();
		
	}
	
	//Button btnPlayDyn = (Button)findViewById(R.id.btnPlay);
	//btnPlayDyn.setLayoutParams(new LayoutParams(300, 300));
	
	
}
