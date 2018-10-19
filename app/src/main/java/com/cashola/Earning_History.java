package com.cashola;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Earning_History extends AppCompatActivity {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private RecyclerView mRecyclerView;
    private EarningHistoryAdapter adapter;
    private ProgressBar progressBar;
    ProgressDialog pd;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView UserBalanceText,WalletBalanceText;
    String UserBalance,UserMobileNumber,currentDayIs;
    private List<FeedItem> feedsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earning__history);

        pref=getApplicationContext().getSharedPreferences("wapearnapp",MODE_PRIVATE);
        editor=pref.edit();
        UserMobileNumber=pref.getString("Email","");

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        GetAppInstallOffer();
    }

    public void GetAppInstallOffer() {


        pd = new ProgressDialog(Earning_History.this);
        pd.setMessage("Please Wait...");
        pd.show();
        RequestQueue MyRequestQueue = Volley.newRequestQueue(Earning_History.this);
        String url = "https://friends99.biz/Cashola/GetUserHistory.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                pd.dismiss();
                Log.i("ResponceClick", response.toString());
                parseResult(response.toString());
                adapter = new EarningHistoryAdapter(Earning_History.this, feedsList, mRecyclerView);
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
                MyData.put("mobile",UserMobileNumber);
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
                item.setTaskName(post.optString("task"));
                item.setTaskAmount(post.optString("earn"));

                //item.(post.optString("address"));

                feedsList.add(item);
                Log.i("AppImage:", post.optString("task"));
               // Log.i("Appurl:", post.optString("url"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
