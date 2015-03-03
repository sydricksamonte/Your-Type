package com.blinkedup.yourtype;

import android.media.MediaPlayer;

public class SingletonObject {
	 private SingletonObject()
	    {
	        // no code req'd
	    }

	 public static MediaPlayer getSingletonObject()
	  {
	    if (ref == null)
	        // it's ok, we can call this constructor
	        ref = new MediaPlayer();        
	    return ref;
	  }

	    private static MediaPlayer ref;
}
