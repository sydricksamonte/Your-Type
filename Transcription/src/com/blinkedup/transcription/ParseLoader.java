package com.blinkedup.transcription;

import android.content.Context;

import com.parse.Parse;

public class ParseLoader {

		public void initParse(Context tx){
			Parse.initialize(tx, "fLvSSaHOIsoXBRBzDG21n4o8mH3sQIsKh9pLYu9X", "A9ETXfq7Oz5ARKaFbsSRfv6WUQ4l5ynGb4Tv8Ljz");
		}
}
