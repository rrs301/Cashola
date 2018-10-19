package com.cashola;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cashola.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.MobileAds;

public class QuizResultPage extends AppCompatActivity {

    TextView ResultDesc,CreditDesc,AnsCorrect;
    ProgressDialog pd;
    SharedPreferences.Editor editor;

  //  private AdView adView1;
 // InterstitialAd mAd;
    SharedPreferences pref;
    private static final int ACTIVITY_RESULT_STATUS = 100;
    private static final int ACTIVITY_RESULT_PAY = 100;
    String UserName,MobileNumber;
    static int Amount;
    int AnsCorrectCount;
    String currentDayIs;
    String UserMobileNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result_page);
     //   MobileAds.initialize(this, String.valueOf(R.string.AdmobApppId));
        getSupportActionBar().hide();
        //AmountToPay=i.getIntExtra("AmountToPay");

        ResultDesc=(TextView)findViewById(R.id.resultdestext);
        CreditDesc=(TextView)findViewById(R.id.credittext);
        AnsCorrect=(TextView)findViewById(R.id.resultcount);
        Intent intent=getIntent();
        AnsCorrectCount=intent.getIntExtra("AnsCorrect",0);
        String Mode=intent.getStringExtra("Mode");

        //---------------------------------------Facebook Banner Ads-----------------------------------
//        adView1 = new AdView(this, "1289511521177434_1297511977044055", AdSize.BANNER_HEIGHT_50);
//        AdSettings.addTestDevice(String.valueOf(R.string.TestDevice));
//        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
//        adContainer.addView(adView1);
//
//        // Request an ad
//        adView1.loadAd();
        pref = this.getSharedPreferences("wapearnapp", MODE_PRIVATE);
        editor = pref.edit();
        UserMobileNumber=pref.getString("Email",null);

//        if(Mode.compareTo("Try")==0)
//        {
//            Amount=0;
//           // UpdateUserBalance();
//           // Toast.makeText(this,"You Played Demo Mode! No Result!!!", Toast.LENGTH_LONG).show();
//            Intent intent1=new Intent(this,MainActivity.class);
//            startActivity(intent1);
//            finish();
//        }
//        else {
//
//
//            AnsCorrect.setText(String.valueOf(AnsCorrectCount));
//           // UpdateUserBalance();
//        }
        //ShowFullAd();
        AnsCorrect.setText(String.valueOf(AnsCorrectCount));
        if(AnsCorrectCount>=2) {
            UpdateUserBalance("0.30", "Quizz");
        }
    }

    public void PlayMoreBtn(View view)
    {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

//    public void ShowFullAd()
//    {
//
//        mAd=new InterstitialAd(getApplicationContext());
//
//        mAd.setAdUnitId("ca-app-pub-9739036117517767/9583795531");
//        mAd.loadAd(new AdRequest.Builder().addTestDevice("E382009FE0B2B673EE51746088ACC0CD").build());
//        if (mAd.isLoaded()) {
//            mAd.show();
//        } else {
//            Log.d("TAG", "The interstitial wasn't loaded yet.");
//        }
//        mAd.setAdListener(new com.google.android.gms.ads.AdListener() {
//            @Override
//            public void onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//                if (mAd.isLoaded()) {
//                    mAd.show();
//                } else {
//                    Log.d("TAG", "The interstitial wasn't loaded yet.");
//                }
//                Log.i("Ads", "onAdLoaded");
//            }
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                // Code to be executed when an ad request fails.
//                Log.i("Ads", "onAdFailedToLoad");
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when the ad is displayed.
//                Log.i("Ads", "onAdOpened");
//            }
//            @Override
//            public void onAdClicked() {
//                // Code to be executed when the ad is displayed.
//
//
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                // Code to be executed when the user has left the app.
//
//
//                 //   UpdateUserBalance("5","Quizz");
//                if (AnsCorrectCount == 10) {
//                    ResultDesc.setText("Cogratualtions!!! You given 10 correct ans out of 10 question.");
//                    CreditDesc.setText("We credited Rs.0.25 in your wallet.");
//                    Amount = 10;
//                } else if (AnsCorrectCount == 9) {
//                    ResultDesc.setText("Not Bad! You given " + AnsCorrectCount + " correct ans out of 10 question.");
//                    CreditDesc.setText("We credited Rs.0.15 in your wallet.");
//                    Amount = 5;
//                } else {
//                    ResultDesc.setText(" You given " + AnsCorrectCount + " correct ans out of 10 question.");
//                    CreditDesc.setText("We credited Rs.0.15 in your wallet.");
//                    Amount = 1;
//                }
//
//            }
//
//
//            @Override
//            public void onAdClosed() {
//                // Code to be executed when when the interstitial ad is closed.
//                Log.i("Ads", "onAdClosed");
//            }
//        });
//
//    }
    public void UpdateUserBalance(final String TaskAmount, final String Task) {

        currentDayIs = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date());
        pd = new ProgressDialog(this);
        pd.setMessage("Sending Confirmation....");
        pd.show();
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "https://wapearn.in/wapearn_app/UpdateUserBalance.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                pd.dismiss();
                Log.i("ResponceIsL", response.toString());

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        Toast.makeText(getApplicationContext(), "Congratulation!!! Amount Credited!...", Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                }, 20000);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("ErrorFound",error.getMessage());
                pd.dismiss();
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
