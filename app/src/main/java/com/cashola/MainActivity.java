package com.cashola;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.cashola.R;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TJConnectListener, TJPlacementListener,RewardedVideoAdListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView UserBalanceText,WalletBalanceText;
    String UserBalance,UserMobileNumber,currentDayIs;
    int TaskCount;
    ProgressDialog pd;
    String currentVersion;
    int backButtonCount;
    InterstitialAd mAd;
    RewardedVideoAd mRewardedVideoAd;
    TJPlacement p;
    String referCode;
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");
        pref=getApplicationContext().getSharedPreferences("wapearnapp",MODE_PRIVATE);
        editor=pref.edit();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        currentDayIs = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

//---------------------Tapjoy-------------------
        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");
        Tapjoy.connect(this, "4Yqlc2J_QkSiGQolpOjsZwECdwZ2Q4h6bleMkakRXHzPCpCUWpVgsKsKlYuO", connectFlags, this);


        UserBalanceText=(TextView)findViewById(R.id.UserBalance);
        UserMobileNumber=pref.getString("Email","");

       GetUserBalanceVolly();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        //----------------Admob App Id-----------------------
        MobileAds.initialize(getApplicationContext(), String.valueOf(R.string.AdmobApppId));
//        //----------------Load Banner----------------------------
         AdView adView = new AdView(getApplicationContext());
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId("ca-app-pub-3659822475267250/2962668900");
            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
//////////////////////////APPCURRENT VERSION///////////////////////////////////////////////////////
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.i("Current Verison:", currentVersion);
        new GetVersionCode().execute();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        WalletBalanceText=(TextView)headerView.findViewById(R.id.WalletBalance);


        TJPlacementListener placementListener = this;
        TJPlacement p = Tapjoy.getPlacement("AppLaunch", placementListener);

        if(Tapjoy.isConnected()) {
            p.requestContent();
        } else {
            Log.d("Wapearn", "Tapjoy SDK must finish connecting before requesting content.");
        }

    }

    public void GetUserBalanceVolly() {

//        pd = new ProgressDialog(this);
//        pd.setMessage("Please Wait...");
//        pd.show();
        // Log.i("InGetUser",UserEmail);

        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "https://friends99.biz/Cashola/GetUserBalance.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.

                Log.i("Balance jjIS:",response.toString());
//                editor.putInt("WalletAmount", Integer.parseInt(response.toString()));
//                editor.commit();
//                CoinsEarn= Integer.parseInt(response.toString());
            //    String[] balance = response.toString().split("-");
                Log.i("Balance Is:",response.toString());
                NumberFormat formatter = NumberFormat.getNumberInstance();
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);
                UserBalance=formatter.format(Float.parseFloat(response.toString()));
                String BalanceIs="\u20B9 "+UserBalance;

                UserBalanceText.setText(BalanceIs);
                String WalletBalanceTextIs="Wallet Balance: "+BalanceIs;
                WalletBalanceText.setText(WalletBalanceTextIs);

                //  pd.dismiss();
                // NotificationSend();
                // UserBalanceText.setText(String.valueOf(UserEarnCoin));
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //  pd.dismiss();
                //This code is executed if there is an error.
                //     Log.i("ErroInConnection:",error.getMessage());
                // Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                //  Log.i("Error jjIS:",error.getMessage());

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                Log.i("MobileIs",UserMobileNumber);
                MyData.put("mobile", UserMobileNumber.trim());
                //MyData.put("balance", String.valueOf(BalanceUpdate));
                return MyData;
            }
        };
        MyRequestQueue.add(MyStringRequest);
    }
    public void CheckSpeed(View view)
    {
        Intent intent=new Intent(this,checkspeed.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Tapjoy.onActivityStart(this);
    }

    //session end
    @Override
    protected void onStop() {
        Tapjoy.onActivityStop(this);
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_gallery) {
           GetClickCount("CheckIn");

        }
        if(id==R.id.PlayAndEarn)
        {
          //  Toast.makeText(this,"Play Earn Here",Toast.LENGTH_SHORT).show();
           GetClickCount("Video");

//            if (mRewardedVideoAd.isLoaded()) {
//                mRewardedVideoAd.show();
//            }
        }
        if(id==R.id.AppWall)
        {
            Toast.makeText(getApplicationContext(),"Loading...Please Wait...",Toast.LENGTH_LONG).show();
            TJPlacementListener placementListener = this;
            p = Tapjoy.getPlacement("AppWall", placementListener);
            if(Tapjoy.isConnected()) {
                p.requestContent();
            } else {
                Log.d("Wapearn", "Tapjoy SDK must finish connecting before requesting content.");
            }
            ShowFullAd();
        }
        if(id==R.id.paytmwallet)
        {
            GetUserBalanceVolly();
            if(Float.parseFloat(UserBalance)>=100.0) {

                ShowFullAd();
                Intent intent = new Intent(this, PaytmTransfer.class);
                intent.putExtra("UserWallet",Float.parseFloat(UserBalance));
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this,"Low Wallet balance, Need Minimum Rs.100",Toast.LENGTH_SHORT).show();

            }
        }
        if(id==R.id.nav_share)
        {
            referCode = pref.getString("ReferCode", "RAH3047");
            SendInvite();
        }
        if(id==R.id.history)
        {
            Intent intent=new Intent(this,Earning_History.class);
            startActivity(intent);
        }
        if(id==R.id.nav_send)
        {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","cash99india@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Cashola App");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Write your Problem Here");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }

        return false;
    }
    public void SendInvite() {

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String Body = "Hi friends, Download Cashola App and Earn Paytm cash";
        String shareBody = Body.replaceAll("<[^>]*>", "");

        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Download Cashola App, Best Earning App");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "" + shareBody + "Download App Now " + buildDynamicLink());
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    private String buildDynamicLink(/*String link, String description, String titleSocial, String source*/) {
        //more info at https://firebase.google.com/docs/dynamic-links/create-manually

//        String path = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setDynamicLinkDomain("cf32x.app.goo.gl")
//                .setLink(Uri.parse("http://innocruts.com/"))
//                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build()) //com.melardev.tutorialsfirebase
//                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().setTitle("Share this App").setDescription("blabla").build())
//                .setGoogleAnalyticsParameters(new DynamicLink.GoogleAnalyticsParameters.Builder().setSource("AndroidApp").build())
//                .buildDynamicLink().getUri().toString();
//
//        return path;
        return "https://cashola.page.link/?" +
                "link=" + /*link*/
                "https://cashola.com/"+referCode +
                "&apn=" + /*getPackageName()*/
                "com.cashola";//+
        //  return "http://easypanda.ml and Enter Refercode as "+referCode;
//                "&st=" + /*titleSocial*/
//                "Share+this+App" +
//                "&sd=" + /*description*/
//                "looking+to+learn+how+to+use+Firebase+in+Android?+this+app+is+what+you+are+looking+for." +
//                "&utm_source=" + /*source*/
//                "GT568N";
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if (backButtonCount >= 1) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            // ShowFullAd();
        } else {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }
    public void GetClickCount(final String Task) {


        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Sending Confirmation....");
        pd.show();
        RequestQueue MyRequestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://friends99.biz/Cashola/GetClickCount.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                pd.dismiss();
                Log.i("ResponceClick", response.toString());
                TaskCount=Integer.parseInt(response.toString());
                if(Task.compareTo("CheckIn")==0) {
                    if (TaskCount == 0) {
                        //  DailyTask();
                        ShowFullAd();
                        UpdateUserBalance("0.25","CheckIn");
                    } else {Toast.makeText(MainActivity.this,"You Already Check In",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(Task.compareTo("SpinWheel")==0)
                {
                    if (TaskCount <= 0) {
                        Intent intent=new Intent(getApplicationContext(),SpeenWheel.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this,"You Already Spin Wheel",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(Task.compareTo("Video")==0)
                {
                    if (TaskCount <= 0) {
                        Toast.makeText(getApplicationContext(), "Please Wait....Video is Loading", Toast.LENGTH_LONG).show();

                        mRewardedVideoAd.loadAd("ca-app-pub-3659822475267250/1474752448",
                                new AdRequest.Builder().addTestDevice("B8391EB6F96DF89F51011980B61E388D").build());
                    } else {
                        Toast.makeText(MainActivity.this,"You Already Watch Todays Video",Toast.LENGTH_SHORT).show();
                    }
                }

                //completeclick.setText(String.valueOf(ClickCountIs));


            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                //This code is executed if there is an error.
                Log.i("ErrorFound",error.getMessage());
                // Toast.makeText(getApplicationContext(), "Try Again...", Toast.LENGTH_SHORT).show();

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                Log.i("InMap:", "Yes");
                MyData.put("mobile", UserMobileNumber.trim());
                MyData.put("task", Task);
                // MyData.put("balance", "1");
                Log.i("CurrentDateIs",String.valueOf(currentDayIs));
                MyData.put("currentDate",String.valueOf(currentDayIs));//Add the data you'd like to send to the server.
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


    }
    public void UpdateUserBalance(final String TaskAmount, final String Task) {

      //  currentDayIs = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date());
//        pd = new ProgressDialog(getApplicationContext());
//        pd.setMessage("Sending Confirmation....");
//        pd.show();
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://friends99.biz/Cashola/UpdateUserBalance.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //  pd.dismiss();
                Log.i("ResponceIsL", response.toString());
                Toast.makeText(getApplicationContext(), "Congratulation!!! Amount Credited!...", Toast.LENGTH_LONG).show();

                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
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
    @Override
    public void onConnectSuccess() {

    }

    @Override
    public void onConnectFailure() {

    }

    @Override
    public void onRequestSuccess(TJPlacement tjPlacement) {

    }

    @Override
    public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {

    }

    @Override
    public void onContentReady(TJPlacement tjPlacement) {
        if(tjPlacement.isContentReady()) {
            tjPlacement.showContent();
        }
        else {
            //handle situation where there is no content to show, or it has not yet downloaded.
        }
    }

    @Override
    public void onContentShow(TJPlacement tjPlacement) {

    }

    @Override
    public void onContentDismiss(TJPlacement tjPlacement) {

    }

    @Override
    public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {

    }

    @Override
    public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        UpdateUserBalance("0.25","Video");
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView mRecyclerView;
        private InstallAppAdapter adapter;
        private ProgressBar progressBar;
        ProgressDialog pd;
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        TextView UserBalanceText,WalletBalanceText;
        String UserBalance,UserMobileNumber,currentDayIs;
        private List<FeedItem> feedsList;
        SwipeRefreshLayout mSwipeRefreshLayout;
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            pref=getContext().getSharedPreferences("wapearnapp",MODE_PRIVATE);
            editor=pref.edit();
            UserMobileNumber=pref.getString("Email","");

            mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            //----------------Admob App Id-----------------------
            MobileAds.initialize(getContext(), String.valueOf(R.string.AdmobApppId));
            mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeToRefresh);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ((MainActivity) getActivity()).GetUserBalanceVolly();
                    GetAppInstallOffer();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

            //----------------Load Banner----------------------------
//            AdView adView = new AdView(getContext());
//            adView.setAdSize(AdSize.SMART_BANNER);
//            adView.setAdUnitId("ca-app-pub-9859005529851767/5352939232");
//            AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            mAdView.loadAd(adRequest);

//            UserWalletBalance=(TextView)rootView.findViewById(R.id.walletBalanceIs);
//            ReferBalance=(TextView)rootView.findViewById(R.id.ReferBalanceIs);
            //mRecyclerView.setLayoutManager(layoutManager);
//            progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
//            progressBar.setVisibility(View.VISIBLE);
            GetAppInstallOffer();
            return rootView;
        }

        public void GetAppInstallOffer() {


            pd = new ProgressDialog(getContext());
            pd.setMessage("Sending Confirmation....");
            pd.show();
            RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
            String url = "https://friends99.biz/Cashola/GetAppInstallOffer.php";
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //This code is executed if the server responds, whether or not the response contains data.
                    //The String 'response' contains the server's response.
                    pd.dismiss();
                    Log.i("ResponceClick", response.toString());
                    parseResult(response.toString());
                    adapter = new InstallAppAdapter(getContext(), feedsList, mRecyclerView);
                    mRecyclerView.setAdapter(adapter);
                    //completeclick.setText(String.valueOf(ClickCountIs));


                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    //This code is executed if there is an error.
                    Log.i("ErrorFound",error.getMessage());
                    // Toast.makeText(getApplicationContext(), "Try Again...", Toast.LENGTH_SHORT).show();

                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();

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


        }

        private void parseResult(String result) {
            Log.i("InParse", "YesNONONO");
            try {
                //   Log.i("InParseIn", "Yes");

                JSONObject response = new JSONObject(result);
                JSONArray posts = response.optJSONArray("result");

                feedsList = new ArrayList();

                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);
                    FeedItem item = new FeedItem();
                    item.setAppInstallImage(post.optString("image"));
                    item.setAppInstallText(post.optString("title"));
                    String Url=post.optString("url");

                    if(Url.contains("{clickid}")) {
                        item.setAppInstallUrl(Url.replace("{clickid}",UserMobileNumber));
                        Log.i("AppurlISNew:", Url.replace("{clickid}",UserMobileNumber));

                    }
                    else if(Url.contains("{click_id}"))
                    {
                        item.setAppInstallUrl(Url.replace("{click_id}",UserMobileNumber));
                        Log.i("AppurlISNew00:", Url.replace("{clickid}",UserMobileNumber));

                    }
                    else
                    {
                        item.setAppInstallUrl(Url);
                    }


                    item.setAppInstallDescription(post.optString("description"));
                    item.setAppInstallPayout(post.optString("payout"));
                    //item.(post.optString("address"));

                    feedsList.add(item);
                    Log.i("AppImage:", post.optString("image"));
                    Log.i("Appurl:", post.optString("url"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public static class PlaceholderFragmentTask extends Fragment  implements TJConnectListener,TJPlacementListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView mRecyclerView;
        private InstallAppAdapter adapter;
        private ProgressBar progressBar;

        SharedPreferences.Editor editor;
        SharedPreferences pref;
        String UserMobileNumber,currentDayIs;
        ProgressDialog pd;
        TJPlacement  p;
        private List<FeedItem> feedsList;
        int TaskCount;
        public PlaceholderFragmentTask() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragmentTask newInstance(int sectionNumber) {
            PlaceholderFragmentTask fragment = new PlaceholderFragmentTask();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_task, container, false);


            Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
            connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");
            Tapjoy.connect(getContext(), "4Yqlc2J_QkSiGQolpOjsZwECdwZ2Q4h6bleMkakRXHzPCpCUWpVgsKsKlYuO", connectFlags, this);

            pref=getContext().getSharedPreferences("wapearnapp",MODE_PRIVATE);
            editor=pref.edit();


            UserMobileNumber=pref.getString("Email",null);
            currentDayIs = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

            TJPlacementListener placementListener = this;
            p = Tapjoy.getPlacement("GiftBox", placementListener);

//            mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
//
//            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            UserWalletBalance=(TextView)rootView.findViewById(R.id.walletBalanceIs);
//            ReferBalance=(TextView)rootView.findViewById(R.id.ReferBalanceIs);
            //mRecyclerView.setLayoutManager(layoutManager);
//            progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
//            progressBar.setVisibility(View.VISIBLE);
            //GetAppInstallOffer();

            //----------------Admob App Id-----------------------
        MobileAds.initialize(getContext(), String.valueOf(R.string.AdmobApppId));
        //----------------Load Banner----------------------------
//        AdView adView = new AdView(getContext());
//        adView.setAdSize(AdSize.SMART_BANNER);
//        adView.setAdUnitId("ca-app-pub-9859005529851767/4594773210");
//        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);


            rootView.findViewById(R.id.Offer_1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetClickCount("Quizz");
                }
            });
            rootView.findViewById(R.id.Offer_2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetClickCount("SpinWheel");
                }
            });
            rootView.findViewById(R.id.Offer_3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getContext(),"GiftBox is Loading, Please Wait...",Toast.LENGTH_LONG).show();
                    if(Tapjoy.isConnected()) {
                        p.requestContent();
                    } else {
                        Log.d("MasterIncome", "Tapjoy SDK must finish connecting before requesting content.");
                    }
                }
            });
            return rootView;
        }

        public void GetAppInstallOffer() {


            pd = new ProgressDialog(getContext());
            pd.setMessage("Sending Confirmation....");
            pd.show();
            RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
            String url = "https://incrts.tk/HulkCashApp_New/BackendFiles/GetAppInstallOffer.php";
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //This code is executed if the server responds, whether or not the response contains data.
                    //The String 'response' contains the server's response.
                    pd.dismiss();
                    Log.i("ResponceClick", response.toString());
                    parseResult(response.toString());
                    adapter = new InstallAppAdapter(getContext(), feedsList, mRecyclerView);
                    mRecyclerView.setAdapter(adapter);
                    //completeclick.setText(String.valueOf(ClickCountIs));


                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    //This code is executed if there is an error.
                    Log.i("ErrorFound",error.getMessage());
                    // Toast.makeText(getApplicationContext(), "Try Again...", Toast.LENGTH_SHORT).show();

                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();

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


        }

        private void parseResult(String result) {
            Log.i("InParse", "YesNONONO");
            try {
                //   Log.i("InParseIn", "Yes");

                JSONObject response = new JSONObject(result);
                JSONArray posts = response.optJSONArray("result");

                feedsList = new ArrayList();

                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);
                    FeedItem item = new FeedItem();
                    item.setAppInstallImage(post.optString("image"));
                    item.setAppInstallText(post.optString("title"));
                    String Url=post.optString("url");

                    if(Url.contains("&subid=")||Url.contains("&subid1=")||Url.contains("&sub_id=")||Url.contains("&USER_ID=")) {
                        item.setAppInstallUrl(Url);
                    }
                    else
                    {
                        item.setAppInstallUrl(Url);
                    }


                    item.setAppInstallDescription(post.optString("description"));
                    item.setAppInstallPayout(post.optString("payout"));
                    //item.(post.optString("address"));

                    feedsList.add(item);
                    Log.i("AppImage:", post.optString("image"));
                    Log.i("Appurl:", post.optString("url"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Check Task Avaiable or Not--------------------------------------------

        public void GetClickCount(final String Task) {


            pd = new ProgressDialog(getContext());
            pd.setMessage("Sending Confirmation....");
            pd.show();
            RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
            String url = "https://friends99.biz/Cashola/GetClickCount.php";
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //This code is executed if the server responds, whether or not the response contains data.
                    //The String 'response' contains the server's response.
                    pd.dismiss();
                    Log.i("ResponceClick", response.toString());
                    TaskCount=Integer.parseInt(response.toString());
                    if(Task.compareTo("CheckIn")==0) {
                        if (TaskCount == 0) {
                          //  DailyTask();
                        } else {Toast.makeText(getContext(),"You Already Check In",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if(Task.compareTo("SpinWheel")==0)
                    {
                        if (TaskCount <= 0) {
                            Intent intent=new Intent(getContext(),SpeenWheel.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(),"You Already Spin the wheel",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if(Task.compareTo("Quizz")==0)
                    {
                        if (TaskCount <= 0) {
                          //  WatchVideo();
                            Intent intent=new Intent(getContext(),QuizzHomePage.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(),"You Already Play Daily Quizz",Toast.LENGTH_SHORT).show();
                        }
                    }
                    //completeclick.setText(String.valueOf(ClickCountIs));


                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    //This code is executed if there is an error.
                    Log.i("ErrorFound",error.getMessage());
                    // Toast.makeText(getApplicationContext(), "Try Again...", Toast.LENGTH_SHORT).show();

                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    Log.i("InMap:", "Yes");
                    MyData.put("mobile", UserMobileNumber.trim());
                    MyData.put("task", Task);
                    // MyData.put("balance", "1");
                    Log.i("CurrentDateIs",String.valueOf(currentDayIs));
                    MyData.put("currentDate",String.valueOf(currentDayIs));//Add the data you'd like to send to the server.
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


        }
        @Override
        public void onConnectSuccess() {

        }

        @Override
        public void onConnectFailure() {

        }

        @Override
        public void onRequestSuccess(TJPlacement tjPlacement) {

        }

        @Override
        public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {

        }

        @Override
        public void onContentReady(TJPlacement tjPlacement) {
            if(tjPlacement.isContentReady()) {
                tjPlacement.showContent();
            }
            else {
                Toast.makeText(getContext(),"No Offers Avaialable",Toast.LENGTH_SHORT).show();
                //handle situation where there is no content to show, or it has not yet downloaded.
            }
        }

        @Override
        public void onContentShow(TJPlacement tjPlacement) {

        }

        @Override
        public void onContentDismiss(TJPlacement tjPlacement) {

        }

        @Override
        public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {

        }

        @Override
        public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {

        }
    }
    public static class InviteFrdFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private static final String TAG = "RecyclerViewExample";
        TextView InviteText, ReferText, ReferCode;
        String UserName, referCode;


        String MainUser,ReferUser;
        String UserReferString;
        private static final String ARG_SECTION_NUMBER = "section_number";
        Context context;
        SharedPreferences.Editor editor;
        SharedPreferences pref;
        public InviteFrdFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static InviteFrdFragment newInstance(int sectionNumber) {
            InviteFrdFragment fragment = new InviteFrdFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_friend__refer, container, false);
            pref = getContext().getSharedPreferences("wapearnapp", MODE_PRIVATE);
            editor = pref.edit();
            InviteText = (TextView)rootView.findViewById(R.id.inviteText);
            ReferText = (TextView) rootView.findViewById(R.id.ReferText);
            new AsyncHttpTask().execute();
            referCode = pref.getString("ReferCode", "RAH3047");
//            referCode = sharedPreferences.getString("ReferCode", null);

            InviteText.setText("Invite your friend to Cashola App");

            ReferCode = (TextView) rootView.findViewById(R.id.ReferCode);
            ReferCode.setText("Refer Code: " +
                    referCode.toUpperCase());

            rootView.findViewById(R.id.ShortUrlbtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendInvite();
                }
            });

            //----------------Admob App Id-----------------------
            MobileAds.initialize(getContext(), String.valueOf(R.string.AdmobApppId));
            //----------------Load Banner----------------------------
//            AdView adView = new AdView(getContext());
//            adView.setAdSize(AdSize.SMART_BANNER);
//            adView.setAdUnitId(String.valueOf(R.string.AdmobBannerId));
//            AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
//            AdRequest adRequest = new AdRequest.Builder().addTestDevice("B8391EB6F96DF89F51011980B61E388D").build();
//            mAdView.loadAd(adRequest);

//            Typeface face = Typeface.createFromAsset(getContext().getAssets(),
//                    "fonts/Raleway-Regular.ttf");
//            InviteText.setTypeface(face);
//            ReferText.setTypeface(face);
//            ReferCode.setTypeface(face);
            return rootView;
        }



        public void SendInvite() {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String Body = "Hi friends, Download Wapearn App and Earn Paytm cash";
            String shareBody = Body.replaceAll("<[^>]*>", "");

            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Download Wapearn App, Best Earning App");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "" + shareBody + "Download App Now " + buildDynamicLink());
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        }

        private String buildDynamicLink(/*String link, String description, String titleSocial, String source*/) {
            //more info at https://firebase.google.com/docs/dynamic-links/create-manually

//        String path = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setDynamicLinkDomain("cf32x.app.goo.gl")
//                .setLink(Uri.parse("http://innocruts.com/"))
//                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build()) //com.melardev.tutorialsfirebase
//                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().setTitle("Share this App").setDescription("blabla").build())
//                .setGoogleAnalyticsParameters(new DynamicLink.GoogleAnalyticsParameters.Builder().setSource("AndroidApp").build())
//                .buildDynamicLink().getUri().toString();
//
//        return path;
            return "https://cashola.page.link/?" +
                    "link=" + /*link*/
                    "https://cashola.com/"+referCode +
                    "&apn=" + /*getPackageName()*/
                    "com.cashola";//+
          //  return "http://easypanda.ml and Enter Refercode as "+referCode;
//                "&st=" + /*titleSocial*/
//                "Share+this+App" +
//                "&sd=" + /*description*/
//                "looking+to+learn+how+to+use+Firebase+in+Android?+this+app+is+what+you+are+looking+for." +
//                "&utm_source=" + /*source*/
//                "GT568N";
        }

        public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

            final ProgressDialog ringProgressDialog = ProgressDialog.show(getContext(), "Please wait ...", "Do Not Touch...", true);

            @Override
            protected void onPreExecute() {
                ringProgressDialog.setCancelable(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                        } catch (Exception e) {
                        }

                        ringProgressDialog.dismiss();
                    }
                }).start();

            }

            @Override
            protected Integer doInBackground(String... params) {
                Integer result = 0;
                HttpURLConnection urlConnection;
                Log.i("In DOIN:", "Yes");
                try {


                    URL url = new URL("https://friends99.biz/Cashola/FriendRefer.php");
                    //  Log.i("URLI:","http://freetym.tk/AppBackend/rechargeApi.php?mobile="+RMobileNumber+"&op="+ROpCode+"&amnt="+RAmount+"&uid="+Uid);

                    urlConnection = (HttpURLConnection) url.openConnection();
                    int statusCode = urlConnection.getResponseCode();

                    // 200 represents HTTP OK
                    if (statusCode == 200) {
                        BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            response.append(line);
                        }
                        String ResponceIs = response.toString();
                        UserReferString=response.toString();
                        MainUser= ResponceIs.substring(0,1);
                        ReferUser=ResponceIs.substring(2,3);
                        Log.i("CODEIS:",MainUser+ReferUser);
                        result = 1; // Successful
                    } else {
                        result = 0; //"Failed to fetch data!";
                    }
                } catch (Exception e) {
                    // Log.d(TAG, e.getLocalizedMessage());
                }
                return result; //"Failed to fetch data!";
            }

            @Override
            protected void onPostExecute(Integer result) {
                // Download complete. Let us update UI
                //    progressBar.setVisibility(View.GONE);
                ringProgressDialog.dismiss();
                //  progressDialog.dismiss();
                if (result == 1) {
                    // Status="SUCCESS";

//                ReferText.setText("It's simple, just invite a friend to join Adlink "+
//                        "and a earn  Rs."+MainUser+"  would be " +
//                        "added to your wallet when 10 friend joins.");
                    ReferText.setText(UserReferString);
//                adapter = new CoupanDealAdapter(context, feedsList, mRecyclerView);
//                mRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0)
            {
                return PlaceholderFragment.newInstance(position + 1);
            }
            else if(position==1)
            {
                return PlaceholderFragmentTask.newInstance(position + 1);

            }
            else
            {
                return InviteFrdFragment.newInstance(position + 1);

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Install App";
                case 1:
                    return "Daily Task";
                case 2:
                    return "Invite";
            }
            return null;
        }
    }


    ////////////////////Check For App Updte////////////////////////////////////////
    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                    //show dialog
                    Toast.makeText(getApplicationContext(), "Update Your App", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/details?id=com.cashola"));
                    startActivity(intent);
                }
            }
            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/details?id=com.innocruts.payer"));
//            startActivity(intent);
        }
    }
}
