package com.blinkedup.yourtype;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.util.Log;

public class Network {
public static boolean isNetworkAvailable(Context context){
		
	    HttpGet httpGet = new HttpGet("http://www.google.com");
	    HttpParams httpParameters = new BasicHttpParams();
	    // Set the timeout in milliseconds until a connection is established.
	    // The default value is zero, that means the timeout is not used.
	    int timeoutConnection = 1000;
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	    // Set the default socket timeout (SO_TIMEOUT)
	    // in milliseconds which is the timeout for waiting for data.
	    int timeoutSocket = 2000;
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

	    DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
	    try{
	        Log.e("INTERNET", "Checking network connection...");
	        httpClient.execute(httpGet);
	        Log.e("INTERNET", "Connection OK");
	        return true;
	    }
	    catch(ClientProtocolException e){
	        e.printStackTrace();
	        Log.e("INTERNET", "Connection FAILED");
	        return false;
	    }
	    catch(IOException e){
	        e.printStackTrace();
	        Log.e("INTERNET", "Connection FAILED");
	        return false;
	    }
	    
	    //Log.d("INTERNET", "Connection unavailable");
	}
}
