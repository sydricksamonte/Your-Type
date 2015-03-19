package com.blinkedup.yourtype;

import com.blinkedup.yourtype.InAppBilling;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import common.services.billing.IabHelper;
import common.services.billing.IabResult;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class InAppPurchase extends Activity implements InAppBilling.InAppBillingListener{
	private RelativeLayout	layout;
	private InAppBilling	inAppBilling;
	private TextView		msgBox;
	private float			mmToPixels;

	DateUtils dateFunc;
	ProgressDialog myPd_ring;
	RecordingDB mydb;
	ParseLoader pl;
	Logger log;
	// purchase request code
	private static final int	PURCHASE_REQUEST_CODE = 1;

	// product code
	private String	PRODUCT_SKU = "gold_01";
	String payType = "";

	// google assigned application public key
	// NOTE: You must replace this key with your own key
	private static final String	applicationPublicKey =
			"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAocl4YnJk2rohuBV9xJQG7vNkdXMPSVAm42CmZBEdgujpDeouIQPHYb9pwQqCXYauMcmeP4o1N6KxmF1aM7wgdcnXnp9eDPGVUIt3YhYAauue5r06fAKP91eV1Yzz4FPtG2/jy9P80Io5MH1J8qrZCyRwD3K6bgRYLDnYwqPF//ClTWga9itkjWPjiBe6IzguMrtX4MqkB2wYnnspvefnA9fXNTLobcYjZvqo4hWWLs0cjUPH3KIFbk1ObD8zZGBDkmZhmcfgsokLv8AtB+jbdnu3bWyhh5UHacwn/EIMScca+UVcA97CzVKwA5Ko2zQzmg26EA5nlCEI9wqWBwiSyQIDAQAB";
	// children views id
	private static final int	TITLE1_ID = 1;
	private static final int	TITLE2_ID = 2;
	private static final int	TITLE3_ID = 3;
	private static final int	BUY_BUTTON_ID = 4;
	private static final int	CONSUME_BUTTON_ID = 5;
	private static final int	MESSAGE_BOX_ID = 6;
	Button btnGold;
	Button btnSilver;
	Button btnBronze;
	
	Handler mHandler;
	int creditsLeft = 0;
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; goto parent activity.
	            this.finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState)
		{
		
		// call super class
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_in_app_purchase);
		btnGold = (Button)findViewById(R.id.buttonGold);
		btnSilver = (Button)findViewById(R.id.buttonSilver);
		btnBronze = (Button)findViewById(R.id.buttonBronze);
		dateFunc = new DateUtils();
		pl = new ParseLoader();
		log = new Logger();
		
		pl.initParse(this);
		
		if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
			 ActionBar actionBar = getActionBar();
			 actionBar.setHomeButtonEnabled(true);
			 actionBar.setDisplayHomeAsUpEnabled(true);
		}
		else{
			 Log.e("NOTICE","Device cannot handle ActionBar");
		}
	
		
		btnGold.setOnClickListener( new OnClickListener() {
			
			 @Override
	            public void onClick(View v) {
				 if (ParseUser.getCurrentUser() != null){
					 myPd_ring = new ProgressDialog(InAppPurchase.this);
					 myPd_ring.setTitle("Please wait");
					 myPd_ring.setMessage("Accessing Store...");
					 myPd_ring.setCancelable(false);
					 myPd_ring.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
						 @Override
						 public void onClick(DialogInterface dialog, int which) {
							 dialog.dismiss();
						 }
					 });
					 myPd_ring.show();
				 
					 PRODUCT_SKU = "gold_01";
					 btnGold.setEnabled(false);
					 buyProduct();
					 return;
				 }
				 else{
					 Toast.makeText(InAppPurchase.this, "Please log-in first", Toast.LENGTH_SHORT).show();
					 btnGold.setEnabled(true);
				 }
			 }
		});
		
		btnSilver.setOnClickListener( new OnClickListener() {
			
			 @Override
	            public void onClick(View v) {
				 if (ParseUser.getCurrentUser() != null){
					 myPd_ring = new ProgressDialog(InAppPurchase.this);
					 myPd_ring.setTitle("Please wait");
					 myPd_ring.setMessage("Accessing Store...");
					 myPd_ring.setCancelable(false);
					 myPd_ring.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
						 @Override
						 public void onClick(DialogInterface dialog, int which) {
							 dialog.dismiss();
						 }
					 });
					 myPd_ring.show();
				 
					 PRODUCT_SKU = "com.blinkedup.yourtype.silver";
					 btnGold.setEnabled(false);
					 buyProduct();
					 return;
				 }
				 else{
					 Toast.makeText(InAppPurchase.this, "Please log-in first", Toast.LENGTH_SHORT).show();
					 btnGold.setEnabled(true);
				 }
			 }
		});
		
		btnBronze.setOnClickListener( new OnClickListener() {
			
			 @Override
	            public void onClick(View v) {
				 if (ParseUser.getCurrentUser() != null){
					 myPd_ring = new ProgressDialog(InAppPurchase.this);
					 myPd_ring.setTitle("Please wait");
					 myPd_ring.setMessage("Accessing Store...");
					 myPd_ring.setCancelable(false);
					 myPd_ring.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
						 @Override
						 public void onClick(DialogInterface dialog, int which) {
							 dialog.dismiss();
						 }
					 });
					 myPd_ring.show();
				 
					 PRODUCT_SKU = "com.blinkedup.yourtype.bronze";
					 btnGold.setEnabled(false);
					 buyProduct();
					 return;
				 }
				 else{
					 Toast.makeText(InAppPurchase.this, "Please log-in first", Toast.LENGTH_SHORT).show();
					 btnGold.setEnabled(true);
				 }
			 }
		});
/*
		// get display metrics
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		
		// conversion factor mm to pixels
		mmToPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1.0F, displayMetrics);
		
		// text size and margin
		float textSize = 7.0F;
		float msgTextSize = 5.0F;
		int margin = (int) (4.0F * mmToPixels);
		
		// create layout
		layout = new RelativeLayout(this);
		
		// take all screen area
		layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

		// same background color
		layout.setBackgroundColor(Color.rgb(163, 176, 255));

		// center view at the center of the screen
		layout.setGravity(Gravity.CENTER);

		// create title 1
		

		// create buy button
	    createButton(BUY_BUTTON_ID, "Buy Product", textSize, margin).setOnClickListener(new OnClickListener()
	    	{
	    	// define on click listener to button
			public void onClick(View view)
				{
				buyProduct();
				return;
				}});
	    
	    // create consume button
	    createButton(CONSUME_BUTTON_ID, "Consume Product", textSize, margin).setOnClickListener(new OnClickListener()
	    	{
	    	// define on click listener to button
			public void onClick(View view)
				{
				Log.i("dfgdf","gdfg");
				//consumeProduct();
				return;
				}});
	    
		// create message box
		msgBox = createTextView(MESSAGE_BOX_ID, "Click either\nBuy Product button or\nConsume Product button", msgTextSize,  margin);

		// add layout to activity
		setContentView(layout);
		return;
		*/
		}

	// create child text view
	
	private TextView createTextView(int id, String text, float textSize, int margin)
		{
		TextView textView = new TextView(this);
	    textView.setId(id);
	   	textView.setText(text);
	   	textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSize);
		RelativeLayout.LayoutParams lp =
				new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		if(id > 0) lp.addRule(RelativeLayout.BELOW, id - 1);
		lp.setMargins(0, 0, 0, margin);
		layout.addView(textView, lp);
		return(textView);
		}

	// create child button
	private Button createButton(int id, String text, float textSize, int margin)
		{
		// create buy button
		Button button = new Button(this);
		button.setId(id);
		button.setText(text);
		button.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSize);
		int padding = (int) (4.0F * mmToPixels);
		button.setPadding(padding, 0, padding, 0);
		RelativeLayout.LayoutParams lp =
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		if(id > 0) lp.addRule(RelativeLayout.BELOW, id - 1);
		lp.setMargins(0, 0, 0, margin);
		layout.addView(button, lp);
		return(button);
		}
    
    private void buyProduct()
	{
	// first time
	if(inAppBilling == null)
		{
		// create in-app billing object
		inAppBilling = new InAppBilling(this, this, applicationPublicKey, PURCHASE_REQUEST_CODE);
		}
	
	// InAppBilling initialization
	// NOTE: if inAppBilling is already active, the call is ignored
	// you can use isActive() to test for class active,
	// or test the result value of the call to find out
	// if start service connection was done (true) or the class was active (false)
	inAppBilling.startServiceConnection(InAppBilling.ITEM_TYPE_ONE_TIME_PURCHASE,
				PRODUCT_SKU, InAppBilling.ACTIVITY_TYPE_PURCHASE);
	
	// exit
	return;
	}

    @Override
    public void inAppBillingBuySuccsess(){
    	
    	
    	if ( PRODUCT_SKU == "gold_01"){
    		payType = "GOLD";
    		creditsLeft = 9000;
    	}
    	else if ( PRODUCT_SKU == "com.blinkedup.yourtype.silver"){
    		payType = "SILVER";
    		creditsLeft = 4500;
    	}
    	else if ( PRODUCT_SKU == "com.blinkedup.yourtype.bronze"){
    		payType = "BRONZE";
    		creditsLeft = 1800;
    	}
		 
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
						 String strDate = dateFunc.getDate();
						 mydb.insertUpdate(strDate,mydb.addToCredits(creditsLeft));
						 myPd_ring.dismiss();
						 
						 new AlertDialog.Builder(InAppPurchase.this)
							 .setTitle("Purchase complete")
							 .setMessage("Transaction complete. "+ payType + " has been transfered to your account")
							 .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								 public void onClick(DialogInterface dialog, int which) {
									 Intent explicitBackIntent = new Intent(InAppPurchase.this,TabHostActivity.class);
									 startActivity(explicitBackIntent);
									 btnGold.setEnabled(true);
								}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
						 Log.e("SUCCESS","In App purchase successful");
						 log.pushToLog(2, "PAYMENT SUCCESSFULLY RECEIVED ON "+ PRODUCT_SKU);
					 }
					 else{
						 Log.e("FAILED",ex.getLocalizedMessage());
						 myPd_ring.dismiss();
					 }
				 }
			}); 
		 }
    		
    	return;
    	}

    @Override
    public void inAppBillingItemAlreadyOwned()
    	{
    	myPd_ring.dismiss();
    	enableButtons();
    	Log.e("ERROR","Product is already owned.\nPurchase was not initiated.");
    	return;
    	}

    @Override
    public void inAppBillingCanceled()
    	{
    	myPd_ring.dismiss();
    	enableButtons();
    	Log.e("ERROR","Purchase was canceled by user");
    	log.pushToLog(2, "TRANSACTION DISMISSED ON "+PRODUCT_SKU);
    	return;
    	}

    @Override
    public void inAppBillingConsumeSuccsess()
    	{
    	myPd_ring.dismiss();
    	
    	Log.e("ERROR","In App consume product successful");
    	enableButtons();
    	return;
    	}

    @Override
    public void inAppBillingItemNotOwned()
    	{
    	myPd_ring.dismiss();
    	 btnGold.setEnabled(true);
    	Log.e("ERROR","Product is not owned.\nConsume failed.");
    	enableButtons();
    	return;
    	}

    @Override
    public void inAppBillingFailure(String errorMessage)
    	{
    	myPd_ring.dismiss();
    	Log.e("ERROR","FAILED: " + errorMessage);
    	log.pushToLog(2, "TRANSACTION FAILURE ON "+ PRODUCT_SKU);
    	Toast.makeText(getApplicationContext(), "Transaction canceled due to failure. \n"+errorMessage, Toast.LENGTH_SHORT).show(); 
    	enableButtons();
    	return;
    	}

    // onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    	{
    	// purchase request code
    	if(inAppBilling != null && requestCode == PURCHASE_REQUEST_CODE)
    		{
    	    // Pass on the activity result to inAppBilling for handling
    		inAppBilling.onActivityResult(resultCode, data);
    		}
    	else
    		{
    		// default onActivityResult
    		super.onActivityResult(requestCode, resultCode, data);
    		}
    	return;
    	}

    // user pressed back button
    @Override
    public void onBackPressed()
    	{
    	// terminate activity and return RESULT_CANCELED
    	if(inAppBilling != null) inAppBilling.dispose();
    	finish();
    	return;
    	}

    // InAppBilling
    @Override
    public void onDestroy()
    	{
    	if(inAppBilling != null) inAppBilling.dispose();
    	super.onDestroy();
    	return;
    	}

    private void enableButtons(){
    	 btnGold.setEnabled(true);
    	 btnSilver.setEnabled(true);
    	 btnBronze.setEnabled(true);
    }
    
    private void disableButtons(){
    	 btnGold.setEnabled(false);
    	 btnSilver.setEnabled(false);
    	 btnBronze.setEnabled(false);
    }
	
	}
