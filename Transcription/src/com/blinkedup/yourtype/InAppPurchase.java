package com.blinkedup.yourtype;

import java.util.Date;
import java.util.List;

import com.blinkedup.yourtype.InAppBilling;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ArrayAdapter;
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
	
	//for parse query of pricing
	ArrayAdapter<String> adapter;
	ParseObject parseObj;
	ParseQuery<ParseObject> pqueryObj;
	List<String> data;
	String obj;
	String restName1, restName2, restName3, restName4, restName5, restName6;
	AlertDialog aDial;
	
	TextView tvGold;
	TextView tvSilver;
	TextView tvBronze;
	TextView tvDescription_Gold;
	TextView tvDescription_Silver;
	TextView tvDescription_Bronze;
	
	@Override
	// back button start.
//	public boolean onOptionsItemSelected(MenuItem item) {
//	    switch (item.getItemId()) {
//	        case android.R.id.home:
	            // app icon in action bar clicked; goto parent activity.
//	            this.finish();
//	            return true;
//	        default:
//	            return super.onOptionsItemSelected(item);
//	    }
//	}
	
	@SuppressLint("HandlerLeak")
//	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// call super class
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_in_app_purchase);
		btnGold = (Button)findViewById(R.id.buttonGold);
		btnSilver = (Button)findViewById(R.id.buttonSilver);
		btnBronze = (Button)findViewById(R.id.buttonBronze);
		
		tvGold = (Button)findViewById(R.id.buttonGold);
		tvSilver = (Button)findViewById(R.id.buttonSilver);
		tvBronze = (Button)findViewById(R.id.buttonBronze);
		tvDescription_Gold = (TextView) findViewById(R.id.textView1);
		tvDescription_Silver = (TextView) findViewById(R.id.textView2);
		tvDescription_Bronze = (TextView) findViewById(R.id.textView3);
		
		dateFunc = new DateUtils();
		pl = new ParseLoader();
		log = new Logger();
		mHandler=new Handler(); //for parse query of pricing
		pl.initParse(this);
		
		mydb = new RecordingDB(this);
		
		mHandler = new Handler()
		{
		    public void handleMessage(Message msg)
		    {
		    	aDial =  new AlertDialog.Builder(InAppPurchase.this)
          		 .setTitle("")
          		 .setMessage("")
          		 .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          			 public void onClick(DialogInterface dialog, int which) {
          				InAppPurchase.this.finish();
          			}
          		 }).setIcon(android.R.drawable.ic_dialog_alert).create();
		    	
		    	if (msg.what == 1){
		    		if (!isOnline()) { Toast.makeText(getApplicationContext(), "Please connect to Internet.", 3).show(); return; };
		    	}
		    	else if (msg.what == 2){
		    		aDial.setTitle("Error in connection");
           			aDial.setMessage("Cannot connect to server. \nPlease try again later.");
           			aDial.show();
		    	}
		    	else if (msg.what == 3){
		    		aDial.setTitle("Error");
           			aDial.setMessage("Network error encountered");
           			aDial.show();
           		}
		    }
		};
		
		if (mydb.getPackagesCheck()){
			String strDateUpdate = mydb.getPackageDateInfo("Gold");
			Date dateUpdate =  dateFunc.convertStringToRawDate(strDateUpdate);
			
			if (!dateFunc.checkIfToday(dateUpdate)){
				Log.e("","ssssssss1");
				runProgress();
				addData();
			}
			else{
				Log.e("","xfdgxd");
				if (mydb.getPackagesCheck()){
					getOfflinePrice();
					addData();
				}
				else{
					mHandler.sendEmptyMessage(1);
				}
			}
		}
		else{
			runProgress();
			addData();
		}
	}
	
	public void runProgress(){
		
		myPd_ring = ProgressDialog.show(InAppPurchase.this, "Please wait", "Retrieving package prices", true);
			myPd_ring.setCancelable(false);
		        
			new Thread(new Runnable() {  
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try{
						if (Network.isNetworkAvailable(InAppPurchase.this)){
							InAppPurchase.this.runOnUiThread(new Runnable() {
								@Override	
								public void run() {
									ParseQuery<ParseObject> query = ParseQuery.getQuery("Pricing");
									query.whereExists("Gold");
									query.whereExists("Silver");
									query.whereExists("Bronze");

									query.findInBackground(new FindCallback<ParseObject>() {
										@Override
										public void done(List<ParseObject> questions, ParseException e) {
											if(e == null){
												mydb.deletePrices();
												for (ParseObject question : questions) {
													restName1 = question.getString("Gold");
													restName2 = question.getString("Silver");
													restName3 = question.getString("Bronze");
													restName4 = question.getString("Description_Gold");
													restName5 = question.getString("Description_Silver");
													restName6 = question.getString("Description_Bronze");
													String strDate = dateFunc.getDate();
													
													Log.e("cccccccc",strDate);
													try{
														mydb.insertPrice("Gold", strDate, Float.parseFloat(restName1), restName4);
														mydb.insertPrice("Silver", strDate, Float.parseFloat(restName2), restName5);
														mydb.insertPrice("Bronze", strDate, Float.parseFloat(restName3), restName6);
													}
													catch(Exception ex){
													}
													
													tvGold.setText("USD "+restName1);
													tvSilver.setText("USD "+restName2);
													tvBronze.setText("USD "+restName3);
													tvDescription_Gold.setText(restName4);
													tvDescription_Silver.setText(restName5);
													tvDescription_Bronze.setText(restName6);
													myPd_ring.dismiss();
												}       
											} 
											else {
												myPd_ring.dismiss();
												mHandler.sendEmptyMessage(2);
											}
										}
									});
								}
							});
						}
						else
						{
							myPd_ring.dismiss();
							if (mydb.getPackagesCheck()){
								getOfflinePrice();
							}
							else{
								mHandler.sendEmptyMessage(1);
							}
						}
					}
					catch(Exception e){
						myPd_ring.dismiss();
						mHandler.sendEmptyMessage(3);
					}
				}
			}).start();
	}
	public void addData() {
		
		 // back button continuation.
		
//		if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
//			 ActionBar actionBar = getActionBar();
//			 actionBar.setHomeButtonEnabled(true);
//			 actionBar.setDisplayHomeAsUpEnabled(true);
//		}
//{
//			 Log.e("NOTICE","Device cannot handle ActionBar");
//		}
	
		
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
	}

	// create child text view
	public void getOfflinePrice(){
		tvGold.setText("USD "+ mydb.getPackagePriceInfo("Gold"));
		tvSilver.setText("USD "+ mydb.getPackagePriceInfo("Silver"));
		tvBronze.setText("USD "+ mydb.getPackagePriceInfo("Bronze"));
		tvDescription_Gold.setText(mydb.getPackageDescInfo("Gold"));
		tvDescription_Silver.setText(mydb.getPackageDescInfo("Silver"));
		tvDescription_Bronze.setText(mydb.getPackageDescInfo("Bronze")); 
	}
	
	public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if ((netInfo != null) && (netInfo.isConnected()))  return true; ;
        return false;
                        	}
	
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
