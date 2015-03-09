package com.blinkedup.yourtype;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class Logger {
	public  void pushToLog(int typeNo, String transType){
		 ParseObject LogObj = new ParseObject("Log");
		 LogObj.put("transType", transType);
		 LogObj.put("subsType", typeNo);
		 LogObj.put("userId", ParseUser.getCurrentUser());
		 {
			 LogObj.saveInBackground(new SaveCallback() {
				 @Override
				 public void done(ParseException ex) {
					 if (ex == null){
						 Log.e("SUCCESS","WRITE TO LOG SUCCESS");
					 }
					 else{
						 Log.e("FAILED",ex.getLocalizedMessage());
					}
				 }
			 }); 
			 return;
		 }
	}
}
