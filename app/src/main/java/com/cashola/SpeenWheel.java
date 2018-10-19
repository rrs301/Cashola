package com.cashola;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.cashola.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;


public class SpeenWheel extends AppCompatActivity {

    //private OnLuckyWheelReachTheTarget luckyWheelReachTheTarget;
    int random;
    int UserEarnCoin;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    private String MobileNumber;
    String currentDayIs;
    ProgressDialog pd;
    InterstitialAd mAd;;
    String UserMobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speen_wheel);
        try {
            pref = this.getSharedPreferences("wapearnapp", MODE_PRIVATE);
            editor = pref.edit();
            UserMobileNumber=pref.getString("Email",null);

//            UserEarnCoin = pref.getInt("UserEarnCoin", 0);
//            Log.i("UserEarnCOin:", String.valueOf(UserEarnCoin));
        }

        catch (Exception e)
        {

        }
        ShowFullAd();
        final List<WheelItem> wheelItems = new ArrayList<>();

        wheelItems.add(new WheelItem(Color.parseColor("#0987f5"), BitmapFactory.decodeResource(getResources(), R.drawable.monettap)));

        wheelItems.add(new WheelItem(Color.parseColor("#16caf2"), BitmapFactory.decodeResource(getResources(), R.drawable.money_bg)));

        wheelItems.add(new WheelItem(Color.parseColor("#0987f5"), BitmapFactory.decodeResource(getResources(), R.drawable.moneybag)));

        wheelItems.add(new WheelItem(Color.parseColor("#16caf2"), BitmapFactory.decodeResource(getResources(), R.drawable.money_bg)));
        wheelItems.add(new WheelItem(Color.parseColor("#0987f5"), BitmapFactory.decodeResource(getResources(), R.drawable.money_bg)));

        wheelItems.add(new WheelItem(Color.parseColor("#16caf2"), BitmapFactory.decodeResource(getResources(), R.drawable.money_bg)));

        wheelItems.add(new WheelItem(Color.parseColor("#0987f5"), BitmapFactory.decodeResource(getResources(), R.drawable.money_bg)));
        wheelItems.add(new WheelItem(Color.parseColor("#16caf2"), BitmapFactory.decodeResource(getResources(), R.drawable.money_bg)));

//   addWheelItems(WheelItems);
        // rotateWheelTo(2);
        final LuckyWheel lw = (LuckyWheel) findViewById(R.id.lwv);
        lw.addWheelItems(wheelItems);

        //  lw.rotateWheelTo(1);
        Random r = new Random();
        random=r.nextInt(100-10)+10;
        lw.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @Override
            public void onReachTarget() {

                Random r = new Random();
                random=r.nextInt(100-10)+10;
                float winamount= (float)random/100;
                Log.i("Random is", String.valueOf((float)random/100));

                Toast.makeText(SpeenWheel.this, "You Win "+winamount+" Rupay", Toast.LENGTH_LONG).show();
                Log.i("UserCoins", String.valueOf(winamount));
                UpdateUserBalance(String.valueOf(winamount),"SpinWheel");
                ShowFullAd();
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lw.rotateWheelTo(random);
            }
        });

    }

    public void ShowFullAd()
    {
        mAd=new InterstitialAd(this);

        mAd.setAdUnitId("ca-app-pub-3659822475267250/8869601709");
        mAd.loadAd(new AdRequest.Builder().addTestDevice("B8391EB6F96DF89F51011980B61E388D").build());
        if (mAd.isLoaded()) {
            mAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
        mAd.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                if (mAd.isLoaded()) {
                    mAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
                Log.i("Ads", "onAdLoaded");
            }
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.i("Ads", "onAdOpened");
            }
            @Override
            public void onAdClicked() {
                // Code to be executed when the ad is displayed.


            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
//                int JokeCount=pref.getInt("JokeCount",0);
//
//                if(JokeCount==10)
//                {
//                    UpdateUserBalance("4","Joke");
//                    editor.putInt("JokeCount",0).commit();
//                }
//                else {
//
//                }
            }


            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                Log.i("Ads", "onAdClosed");
            }
        });

    }

    public void UpdateUserBalance(final String TaskAmount, final String Task) {

        currentDayIs = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date());
//        pd = new ProgressDialog(getApplicationContext());
//        pd.setMessage("Sending Confirmation....");
//        pd.show();
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://wapearn.in/wapearn_app/UpdateUserBalance.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
              //  pd.dismiss();
                Log.i("ResponceIsL", response.toString());
                Toast.makeText(getApplicationContext(), "Congratulation!!! Amount Credited!...", Toast.LENGTH_LONG).show();


//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        // Actions to do after 10 seconds
//                        Toast.makeText(getApplicationContext(), "Congratulation!!! Amount Credited!...", Toast.LENGTH_LONG).show();
//                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
//                        startActivity(intent);
//                    }
//                }, 20000);

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("ErrorFound",error.getMessage());
              //  pd.dismiss();
                //This code is executed if there is an error.
                // Toast.makeText(getApplicationContext(), "Try Again...", Toast.LENGTH_SHORT).show();

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                Log.i("UserMobieIs:", UserMobileNumber);
                MyData.put("mobile",UserMobileNumber.trim());
                MyData.put("balance", TaskAmount);
                MyData.put("task",Task);
                MyData.put("currentDate", String.valueOf(currentDayIs));//Add the data you'd like to send to the server.
                // MyData.put("paytmnumber", UserPaytmNumber);//Add the data you'd like to send to the server.


                return MyData;
            }
        };
//                if(status)
//                {

        MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(2000),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyRequestQueue.add(MyStringRequest);

        // requestQueue.add(stringRequest);
//                    i++;
//                    status =false;
//
//                }
//            }while(i>0);
    }

}
