package com.blinkedup.transcription;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class FeedDetailActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		TextView result;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feeddetail);
		result = (TextView) findViewById(R.id.resultView);
		Intent intent = getIntent();
		String sum = (String) intent.getSerializableExtra("RECID");
		result.setText(sum);
	}
}
