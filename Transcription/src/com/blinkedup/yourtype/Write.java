package com.blinkedup.yourtype;
import android.content.Context;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class Write {
	Boolean result;
	RecordingDB mydb;
	DateUtils dateFunc;
	/*
	public boolean insertToParseAndDB(String payType, int creditsLeft, Context cx){
		result = false;
		mydb = new RecordingDB(cx);
		dateFunc = new DateUtils();
		ParseObject AudioRec = new ParseObject("Credit");
			AudioRec.put("payType", payType);
			AudioRec.put("creditsLeft", creditsLeft);
			AudioRec.put("isActive", true);
			AudioRec.put("subsType", 2);
			AudioRec.put("UserId", ParseUser.getCurrentUser());
			{
			AudioRec.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException ex) {
					if (ex == null){
						result = true;
						String strDate = dateFunc.getDate();
						mydb.insertUpdate(strDate,60);
					}
					else{
						result = false;
						Log.e("",ex.getLocalizedMessage());
					}
				}
			}); 
			}
		return result;
	}
	*/
}
