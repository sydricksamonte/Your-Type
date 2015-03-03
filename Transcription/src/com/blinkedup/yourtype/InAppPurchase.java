package com.blinkedup.yourtype;

import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class InAppPurchase extends Activity {

    protected static final String SKU = "android.test.purchased";

    private static final String PUBKEY = "0011223344";

    protected static final int BUY_REQUEST_CODE = 12345;
    
   /* private IabHelper buyHelper;

    private Button butConsume;
    private Button butBuy;
    private Purchase purchase;
    private Button butUpdate;
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /* setContentView(R.layout.in_app_purchase);

        butUpdate = (Button) findViewById(R.id.button_update);
        
        butBuy = (Button) findViewById(R.id.button_buy);
        butBuy.setEnabled(false);
        
        butConsume = (Button) findViewById(R.id.button_consume);
        butConsume.setEnabled(false);
        
        buyHelper = new IabHelper(this, PUBKEY);
        
        butUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        
        butConsume.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buyHelper.consumeAsync(purchase, new OnConsumeFinishedListener() {
                    @Override
                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                        if(result.isSuccess()) {
                            Toast.makeText(InAppPurchase.this, "Purchase consumed!", Toast.LENGTH_SHORT).show();
                            
                            try {
                                // Small HACK: Give the system some time to realize the consume... without the sleep here,
                                // you have to press "Update" to see that the item can be bought again... 
                                Thread.sleep(1000);
                                update();
                            } catch(Exception e) {
                                // ignored
                            }
                            
                        } else {
                            Toast.makeText(InAppPurchase.this, "Error consuming: "+result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        
        buyHelper.startSetup(new OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                update();
            }
        });

        butBuy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buyHelper.launchPurchaseFlow(InAppPurchase.this, SKU, BUY_REQUEST_CODE, new OnIabPurchaseFinishedListener() {
                    @Override
                    public void onIabPurchaseFinished(IabResult result, Purchase info) {
                        if(result.isSuccess()) {
                            Toast.makeText(InAppPurchase.this, "Thanks for buying!", Toast.LENGTH_SHORT).show();
                            update();
                        }
                    }
                });
            }
        });
        */
    }

    private void update() {
    	 /* ArrayList<String> moreSkus = new ArrayList<String>();
        moreSkus.add(SKU);
        buyHelper.queryInventoryAsync(true, moreSkus, new QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                if(result.isSuccess()) {
                    SkuDetails details = inv.getSkuDetails(SKU);
                    String price = details.getPrice();
                    
                    TextView tvPrice = (TextView)InAppPurchase.this.findViewById(R.id.textview_price);
                    tvPrice.setText(price);

                    purchase = inv.getPurchase(SKU);
                    
                    if(purchase!=null) {
                        butBuy.setEnabled(false);
                        butConsume.setEnabled(true);
                    } else {
                        butBuy.setEnabled(true);
                        butConsume.setEnabled(false);
                    }
                    
                    Toast.makeText(InAppPurchase.this, "Successful got inventory!", Toast.LENGTH_SHORT).show();
                    
                } else {
                    Toast.makeText(InAppPurchase.this, "Error getting inventory!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        */
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* buyHelper.handleActivityResult(requestCode, resultCode, data);
        */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* buyHelper.dispose();
        */
    }
}
