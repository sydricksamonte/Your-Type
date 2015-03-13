package com.blinkedup.yourtype;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.util.Log;

public class DateUtils {
	public String convertStringToDate(String dateToConv){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date convertedDate = new Date();
		 String localTime = "";
		try {
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); 
			convertedDate = dateFormat.parse(dateToConv);
			
			localTime = convertedToLocale(convertedDate);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("DA",e.getLocalizedMessage());
		}
		
		return localTime;
	}
	
	private String convertedToLocale(Date localeDate){
		String converted;
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

	    DateFormat formatter = new SimpleDateFormat("dd MMM — hh:mm a");    
	    formatter.setTimeZone(TimeZone.getDefault());  

	    converted = formatter.format(localeDate.getTime());
		return converted;
	}
	
	public String convertedToInstall(){
		Date localeDate = new Date();
		String converted;
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

	    DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");    
	    formatter.setTimeZone(TimeZone.getDefault());  

	    converted = formatter.format(localeDate.getTime());
		return converted;
	}
	
	public String convIntToLength(String len){
		  int parsedLen = Integer.parseInt(len);
		  int hr = parsedLen/3600;
		  int rem = parsedLen%3600;
		  int mn = rem/60;
		  int sec = rem%60;
		  String hrStr = (hr<10 ? "0" : "")+hr;
		  String mnStr = (mn<10 ? "0" : "")+mn;
		  String secStr = (sec<10 ? "0" : "")+sec; 
		  
		  if (hr==0){
			  return  mnStr +":"+ secStr;
		  }
		  else{
			  return hrStr  +":"+ mnStr +":"+ secStr;
		  }
	}
	public String convIntBaseToLength(int len){
		  int parsedLen = len;
		  int hr = parsedLen/3600;
		  int rem = parsedLen%3600;
		  int mn = rem/60;
		  int sec = rem%60;
		  String hrStr = (hr<10 ? "0" : "")+hr;
		  String mnStr = (mn<10 ? "0" : "")+mn;
		  String secStr = (sec<10 ? "0" : "")+sec; 
		  
		  if (hr==0){
			  return  mnStr +":"+ secStr;
		  }
		  else{
			  return hrStr  +":"+ mnStr +":"+ secStr;
		  }
	}
	public String getDate(){
		Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String strDate = sdf.format(c.getTime());
        
        return strDate;
	}
	
	public String getRawDateStringFromParse(Date dateString){
	 //String dateString = "03/26/2012 11:49:00 AM";
		Log.e("sdgdfg", dateString.toString());
	
	    String strDate = "";
	       
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	        strDate = sdf.format(dateString.getTime());

	    return strDate;
	}
	
	public Date convertStringToRawDate(String dateString){
		 //String dateString = "03/26/2012 11:49:00 AM";
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		try {  
		    date = format.parse(dateString);  
		    System.out.println(date);  
		} catch (ParseException e) {  
		    // TODO Auto-generated catch block  
		    e.printStackTrace();  
		}
		    return date;
		}
	
}
