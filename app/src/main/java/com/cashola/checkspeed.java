package com.cashola;

import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cardiomood.android.controls.gauge.SpeedometerGauge;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.cashola.R;
import com.ram.speed.booster.RAMBooster;
import com.ram.speed.booster.interfaces.CleanListener;
import com.ram.speed.booster.interfaces.ScanListener;
import com.ram.speed.booster.utils.ProcessInfo;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class checkspeed extends AppCompatActivity {

    SpeedometerGauge speedometer;
    private RAMBooster booster;
    InterstitialAd mAd;
    final double [] RXOld = new double [1];
    int flag=0;
    TextView Text1,Text2;
    private String TAG="Booster.Test";
     RippleBackground rippleBackground;
    ImageView imageView;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkspeed);
     //   getSupportActionBar().hide();
//
       Text1=(TextView)findViewById(R.id.text);
        Text2=(TextView)findViewById(R.id.text_2);
        button=(Button)findViewById(R.id.Btn);
        Text1.setVisibility(View.GONE);
        Text2.setVisibility(View.GONE);
        ShowFullAd();
         rippleBackground=(RippleBackground)findViewById(R.id.content);
         imageView=(ImageView)findViewById(R.id.centerImage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
                flag=1;
                button.setVisibility(View.GONE);
                StopAnim();

            }
        });
//        new ParticleSystem(checkspeed.this, 100, R.drawable.rocketship, 10000)
//                .setSpeedByComponentsRange(0f, 0f, 0.05f, 0.1f)
//                .setAcceleration(0.00005f, 0)
//                .emitWithGravity(findViewById(R.id.centerImage), Gravity.TOP, 2);
       // subscribe();



    }

    public void StopAnim()
    {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Text1.setVisibility(View.VISIBLE);
                    Text2.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    button.setVisibility(View.VISIBLE);
                    button.setText("Home");
                    Random r = new Random();
                    int i1 = r.nextInt(700 - 200) + 200;
                    Text2.setText(i1+"MB \n Ram is Free");
                    rippleBackground.stopRippleAnimation();
                    ShowFullAd();
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }, 10000);

    }



    public enum ConnectionQuality {
        /**
         * Bandwidth under 50 kbps.
         */
        WORST,
        /**
         * Bandwidth under 150 kbps.
         */
        POOR,
        /**
         * Bandwidth between 150 and 550 kbps.
         */
        MODERATE,
        /**
         * Bandwidth between 550 and 2000 kbps.
         */
        GOOD,
        /**
         * EXCELLENT - Bandwidth over 2000 kbps.
         */
        EXCELLENT,
        /**
         * Placeholder for unknown bandwidth. This is the initial value and will stay at this value
         * if a bandwidth cannot be accurately found.
         */
        UNKNOWN
    }
    public void subscribe()
    {


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {


            @Override
            public void run() {


                ////////////////////////Code to be executed every second////////////////////////////////////////


                double overallTraffic = TrafficStats.getMobileRxBytes();

                double currentDataRate = overallTraffic - RXOld [0];

                TextView view1 = null;
                view1 = (TextView) findViewById(R.id.text);
                view1.setText("Current Data Rate per second= " + currentDataRate);

                RXOld [0] = overallTraffic;

                handler.postDelayed(this, 1000);
            }
        }, 1000 );
    }

    public void Boost()
    {
        if (booster==null)
            booster=null;
        booster = new RAMBooster(checkspeed.this);
        booster.setDebug(true);
        booster.setScanListener(new ScanListener() {
            @Override
            public void onStarted() {
                Log.d(TAG, "Scan started");
            }

            @Override
            public void onFinished(long availableRam, long totalRam, List<ProcessInfo> appsToClean) {

                Log.d(TAG, String.format(Locale.US,
                        "Scan finished, available RAM: %dMB, total RAM: %dMB",
                        availableRam,totalRam));
                List<String> apps = new ArrayList<String>();
                for (ProcessInfo info:appsToClean){
                    apps.add(info.getProcessName());
                }
                Log.d(TAG, String.format(Locale.US,
                        "Going to clean founded processes: %s", Arrays.toString(apps.toArray())));
                booster.startClean();
            }
        });
        booster.setCleanListener(new CleanListener() {
            @Override
            public void onStarted() {
                Log.d(TAG, "Clean started");
            }


            @Override
            public void onFinished(long availableRam, long totalRam) {

                Log.d(TAG, String.format(Locale.US,
                        "Clean finished, available RAM: %dMB, total RAM: %dMB",
                        availableRam,totalRam));
                booster = null;

            }
        });
        booster.startScan(true);
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

}
