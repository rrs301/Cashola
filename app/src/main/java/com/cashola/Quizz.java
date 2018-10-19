package com.cashola;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.cashola.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Quizz extends AppCompatActivity {

    Button Op1Btn,Op2Btn,Op3Btn,Op4Btn;
    ProgressDialog pd;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    private static final int ACTIVITY_RESULT_STATUS = 100;
    private static final int ACTIVITY_RESULT_PAY = 100;
    String UserName,MobileNumber;
    CountDownTimer countDownTimer;
    int AmountToPay;
    InterstitialAd mAd;
    String CorrectAns,Op1,Op2,Op3,Op4,Question;
    TextView Times,QuestionText,QuestionNumber;
    RelativeLayout relativeLayout;
    String Mode;

    static int QuesCount=0,CorrectAnsCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);
        getSupportActionBar().hide();
        QuesCount=0;
        CorrectAnsCount=0;
        pref = getApplicationContext().getSharedPreferences("wapearnapp", MODE_PRIVATE);
        editor = pref.edit();

        MobileAds.initialize(this, String.valueOf(R.string.AdmobApppId));

        //---------------------------------------Facebook Banner Ads-----------------------------------
//        adView1 = new com.facebook.ads.AdView(this, "1289511521177434_1297511977044055", com.facebook.ads.AdSize.BANNER_HEIGHT_50);
//        AdSettings.addTestDevice(String.valueOf(R.string.TestDevice));
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
//        adContainer.addView(adView1);
//
//        // Request an ad
//        adView1.loadAd();


        //AmountToPay=i.getIntExtra("AmountToPay");
        UserName = pref.getString("UserName", "-");
        MobileNumber = pref.getString("Email", "-");
        Intent intent=getIntent();
        Mode =intent.getStringExtra("Mode");

//        //----------------Admob App Id-----------------------
//        MobileAds.initialize(this, "ca-app-pub-7919542238309223~4127483635");
//        //----------------Load Banner----------------------------
//        AdView adView = new AdView(this);
//        adView.setAdSize(AdSize.SMART_BANNER);
//        adView.setAdUnitId("ca-app-pub-7919542238309223/6016292138");
//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(String.valueOf(R.string.TestDevice)).build();
//        mAdView.loadAd(adRequest);



        Op1Btn=(Button)findViewById(R.id.op1Btn);
        Op2Btn=(Button)findViewById(R.id.op2Btn);
        Op3Btn=(Button)findViewById(R.id.op3Btn);
        Op4Btn=(Button)findViewById(R.id.op4Btn);
        Times=(TextView)findViewById(R.id.Timer);

        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);

        QuestionText=(TextView)findViewById(R.id.question);
        QuestionNumber=(TextView)findViewById(R.id.QuestionNumber);
        QuestionNumber.setText("Q1.");
        countDownTimer =new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                Times.setText("Time:"+(millisUntilFinished / 1000));
            }

            public void onFinish() {
                Toast.makeText(getApplicationContext(),"Times Up", Toast.LENGTH_SHORT).show();

//                if(Mode.compareTo("Try")==0)
//                {
//                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
//                    Toast.makeText(getApplicationContext(),"You Played Demo Mode! No Result!!!",Toast.LENGTH_LONG).show();
//                    startActivity(intent);
//                    finish();
//                }
//                else
//                {
//                    Intent intent=new Intent(getApplicationContext(),QuizResultPage.class);
//                    intent.putExtra("AnsCorrect",CorrectAnsCount);
//                    intent.putExtra("Mode",Mode);
//                    startActivity(intent);
//                    finish();
//                }
                Intent intent=new Intent(getApplicationContext(),QuizResultPage.class);
                intent.putExtra("AnsCorrect",CorrectAnsCount);
                intent.putExtra("Mode",Mode);
                startActivity(intent);
                finish();

            }

        }.start();
        GetQuestion();
    }



    public void SetQuestion()
    {
        QuesCount=QuesCount+1;
        Op1Btn.setText("A. "+Op1);
        Op2Btn.setText("B. "+Op2);
        Op3Btn.setText("C. "+Op3);
        Op4Btn.setText("D. "+Op4);
        QuestionText.setText(Question);
        QuestionNumber.setText("Q."+QuesCount);

        findViewById(R.id.op1Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("BTNNAME",Op1Btn.getText().toString());
                if(Op1Btn.getText().toString().compareTo("A. "+CorrectAns)==0)
                {
                    Op1Btn.setBackgroundColor(Color.GREEN);
                    CorrectAnsCount=CorrectAnsCount+1;
                    Log.i("CorrectAns", String.valueOf(CorrectAnsCount));
                }
                else
                {
                    Op1Btn.setBackgroundColor(Color.RED);
                }
                Op2Btn.setClickable(false);
                Op3Btn.setClickable(false);
                Op4Btn.setClickable(false);

            }
        });
        findViewById(R.id.op2Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Op2Btn.getText().toString().compareTo("B. "+CorrectAns)==0)
                {
                    Op2Btn.setBackgroundColor(Color.GREEN);
                    CorrectAnsCount=CorrectAnsCount+1;

                }
                else
                {
                    Op2Btn.setBackgroundColor(Color.RED);
                }
                Op1Btn.setClickable(false);
                Op3Btn.setClickable(false);
                Op4Btn.setClickable(false);
            }
        });
        findViewById(R.id.op3Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Op3Btn.getText().toString().compareTo("C. "+CorrectAns)==0)
                {
                    Op3Btn.setBackgroundColor(Color.GREEN);
                    CorrectAnsCount=CorrectAnsCount+1;

                }
                else
                {
                    Op3Btn.setBackgroundColor(Color.RED);
                }
                Op1Btn.setClickable(false);
                Op2Btn.setClickable(false);
                Op4Btn.setClickable(false);
            }

        });
        findViewById(R.id.op4Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Op4Btn.getText().toString().compareTo("D. "+CorrectAns)==0)
                {
                    Op4Btn.setBackgroundColor(Color.GREEN);
                    CorrectAnsCount=CorrectAnsCount+1;

                }
                else
                {
                    Op4Btn.setBackgroundColor(Color.RED);
                }
                Op1Btn.setClickable(false);
                Op2Btn.setClickable(false);
                Op3Btn.setClickable(false);
            }
        });

    }

    public void NextQuestion(View view)
    {
        ShowFullAd();
        Op1Btn.setBackgroundColor(Color.parseColor("#6cffffff"));
        Op2Btn.setBackgroundColor(Color.parseColor("#6cffffff"));
        Op3Btn.setBackgroundColor(Color.parseColor("#6cffffff"));
        Op4Btn.setBackgroundColor(Color.parseColor("#6cffffff"));
        Op1Btn.setClickable(true);
        Op2Btn.setClickable(true);
        Op3Btn.setClickable(true);
        Op4Btn.setClickable(true);
        Random r = new Random();
        int i1 = r.nextInt(5 - 1) + 1;
        if(i1==1||i1==6) {
           // ShowFullAd();
            relativeLayout.setBackgroundColor(Color.parseColor("#1ABC9C"));
        }
        else if(i1==2) {
           // ShowFullAd();
            relativeLayout.setBackgroundColor(Color.parseColor("#E74C3C"));
        }
        else if(i1==3) {
          //  ShowFullAd();
            relativeLayout.setBackgroundColor(Color.parseColor("#F39C12"));
        }
        else if(i1==4) {
           // ShowFullAd();
            relativeLayout.setBackgroundColor(Color.parseColor("#8E44AD"));
        }
        else if(i1==5) {
          //  ShowFullAd();
            relativeLayout.setBackgroundColor(Color.parseColor("#3498DB"));
        }
        if(QuesCount==10)
        {
            countDownTimer.cancel();
            Toast.makeText(this,"Click On this Ad To credit Amount", Toast.LENGTH_SHORT).show();
//            if(Mode.compareTo("Try")==0)
//            {
//                Intent intent=new Intent(this,MainActivity.class);
//                Toast.makeText(this,"You Played Demo Mode! No Result!!!",Toast.LENGTH_LONG).show();
//                startActivity(intent);
//                finish();
//            }
//            else
//            {
//                Intent intent=new Intent(this,QuizResultPage.class);
//                intent.putExtra("AnsCorrect",CorrectAnsCount);
//                intent.putExtra("Mode",Mode);
//                startActivity(intent);
//                finish();
//            }
               Intent intent=new Intent(this,QuizResultPage.class);
                intent.putExtra("AnsCorrect",CorrectAnsCount);
                intent.putExtra("Mode",Mode);
                startActivity(intent);
                finish();

        }
        else {
            GetQuestion();
            Log.i("CorrectAns", String.valueOf(CorrectAnsCount));
        }
    }
    public void GetQuestion() {

        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait");
        pd.show();
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "http://earnapp.tk/AppBackend/Quizz/GetQuestion.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseResult(response.toString());
                pd.dismiss();
                Log.i("NewResponce jjIS:",response.toString());
                Log.i("UserCoinIs:",response.toString());

                SetQuestion();



            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                 pd.dismiss();
                //This code is executed if there is an error.
                //Log.i("Error Is :::",error.getMessage());
                Toast.makeText(getApplicationContext(), "Try Again...", Toast.LENGTH_SHORT).show();

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();

                return MyData;
            }
        };
        MyRequestQueue.add(MyStringRequest);
    }

    private void parseResult(String result) {
        Log.i("InParse", "YesNONONO");
        try {
            //   Log.i("InParseIn", "Yes");

            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("result");

            // feedsList = new ArrayList();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                //    FeedItem item = new FeedItem();

                Question=post.optString("question");
                Op1=post.optString("op1");
                Op2=post.optString("op2");
                Op3=post.optString("op3");
                Op4=post.optString("op4");
                CorrectAns=post.optString("correct");


                //item.(post.optString("address"));

                //  feedsList.add(item);
                Log.i("AppImage:", post.optString("correct"));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void ShowFullAd()
    {

        mAd=new InterstitialAd(getApplicationContext());

        mAd.setAdUnitId("ca-app-pub-3659822475267250/8869601709");
        mAd.loadAd(new AdRequest.Builder().addTestDevice("E382009FE0B2B673EE51746088ACC0CD").build());
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

            }


            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                Log.i("Ads", "onAdClosed");
            }
        });

    }

}
