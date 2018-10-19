package com.cashola;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.cashola.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Facebook_Login extends AppCompatActivity {


    private static final int APP_REQUEST_CODE = 99;
    private FirebaseAnalytics analytics;
    private TextView txtResult;
    String UserReferCode="NA",currentTime;
    private static final String TAG ="Msg" ;
    private static final int REQUEST_INVITE = 100;

    SharedPreferences.Editor editor;
    SharedPreferences pref;
    String RandomReferCode,Gendar,CollageName;
    ProgressDialog pd;
    EditText Username;
    static int  flag=0;
    float Balance;
    String Email,Msg,User_Name,ReferCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook__login);
        getSupportActionBar().hide();
        pref=getApplicationContext().getSharedPreferences("wapearnapp",MODE_PRIVATE);
        editor=pref.edit();

        GetPermission();
        AccountKit.initialize(getApplicationContext());

        if(pref.getInt("Login",0)==1)
        {
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        currentTime = String.valueOf(Calendar.getInstance().getTime());
        RandomReferCode=getSaltString();
        //phoneLogin();
       // getAccount()
        FirebaseApp.initializeApp(Facebook_Login.this);
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        if (pendingDynamicLinkData != null) {
                            //init analytics if you want to get analytics from your dynamic links
                            analytics = FirebaseAnalytics.getInstance(Facebook_Login.this);

                            Uri deepLink = pendingDynamicLinkData.getLink();
                            //txtResult.append("\nonSuccess called " + deepLink.toString());
                            UserReferCode=deepLink.toString();
                            Log.i("userrefercodestring",UserReferCode);
                            UserReferCode=UserReferCode.substring(UserReferCode.length()-8);
                            Log.i("USerReferCode",UserReferCode);

                            //logic here, redeem code or whatever

                            FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData);
                            if (invite != null) {
                                String invitationId = invite.getInvitationId();
                                if (!TextUtils.isEmpty(invitationId))
                                    txtResult.append("\ninvitation Id " + invitationId);
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        txtResult.append("\nonFailure");
                    }
                });

    }

    public void GetPermission()
    {
        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.READ_SMS,
                        android.Manifest.permission.RECEIVE_SMS

                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }
//    public void phoneLogin() {
//        final Intent intent = new Intent(getApplicationContext(), AccountKitActivity.class);
//        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
//                new AccountKitConfiguration.AccountKitConfigurationBuilder(
//                        LoginType.PHONE,
//                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
//        // ... perform additional configuration ...
//        intent.putExtra(
//                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
//                configurationBuilder.build());
//        startActivityForResult(intent, APP_REQUEST_CODE);
//    }
protected String getSaltString() {
    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < 8) { // length of the random string.
        int index = (int) (rnd.nextFloat() * SALTCHARS.length());
        salt.append(SALTCHARS.charAt(index));
    }
    String saltStr = salt.toString();
    Log.i("RandomString Is",saltStr);
    return saltStr;

}
    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
             //   showErrorActivity(loginResult.getError());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                    getAccount();
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0,10));
                }

                getAccount();
            }

            // Surface the result to your user in an appropriate way.
            Toast.makeText(
                    this,
                    toastMessage,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
    public void MobileLogin(View v)
    {
        // Username.setVisibility(View.VISIBLE);

        final Intent intent = new Intent(getApplicationContext(), AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        configurationBuilder.setReadPhoneStateEnabled(true);
        configurationBuilder.setReceiveSMS(true);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);


    }
    private void getAccount() {
        Log.i("InAccount","Yes");
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                // Get Account Kit ID
                String accountKitId = account.getId();

                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                String phoneNumberString = phoneNumber.toString().trim();

                // Surface the result to your user in an appropriate way.
                Toast.makeText(
                        Facebook_Login.this,
                        phoneNumberString,
                        Toast.LENGTH_LONG)
                        .show();
                Email=phoneNumberString.replace("+","");
                User_Name="Admin";
                editor.putString("Username","Admin");
                editor.putString("UserEmail",Email);
                editor.putString("ReferCode",RandomReferCode);
                editor.putString("UserImageUrl","http://www.abc.net.au/news/image/8314104-1x1-940x940.jpg");
                editor.putInt("Login",1);
                editor.commit();
                VollyMethod();
            }

            @Override
            public void onError(final AccountKitError error) {
                Log.e("AccountKit", error.toString());
                // Handle Error
            }
        });
    }
    public void VollyMethod() {

        pd = new ProgressDialog(Facebook_Login.this);
        pd.setMessage("Verification...");
        pd.show();
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "https://friends99.biz/Cashola/CheckUserAndSave.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                pd.dismiss();
                Log.i("Responce Is",response.toString());
                if(response.toString().compareTo("1")==0)
                {
                   // editor.putFloat("Balance", 0);
                    editor.putString("Email",Email);
                    editor.putString("UserName",User_Name);
                    editor.putString("ReferCode",RandomReferCode);
                    editor.putString("Collage",CollageName);

                    editor.putString("Gender",Gendar);

                    editor.putInt("Login",1);
                    editor.commit();
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Welcome ...", Toast.LENGTH_SHORT).show();

                }
                else if(response.toString().compareTo("2")==0)
                {
                    Toast.makeText(getApplicationContext(),"User With This Model Already Regsiter", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    parseResult(response.toString());
                  //  editor.putFloat("Balance", Balance);
                    editor.putString("Email",Email);
                    editor.putString("UserName",User_Name);
                    editor.putString("ReferCode",ReferCode);
                    editor.putInt("Login",1);
                    editor.commit();
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Welcome Back!!!", Toast.LENGTH_SHORT).show();
                }
//                Log.i("This Random", String.valueOf(rand));
//                Intent intent=new Intent(getApplicationContext(),OTP_Check.class);
//                intent.putExtra("OTPCODE",String.valueOf(rand));
//                intent.putExtra("MobileNumber",String.valueOf(MobileNo));
//                intent.putExtra("UserName",String.valueOf(User_Name));
//                intent.putExtra("UserReferCode",String.valueOf(UserRefer));
//                intent.putExtra("DeviceId",String.valueOf(deviceId));

                //   startActivity(intent);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                //This code is executed if there is an error.
                Log.i("Error:",error.getMessage());
                Toast.makeText(Facebook_Login.this, "Try Again...", Toast.LENGTH_SHORT).show();

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("name", User_Name.replace(" ","%20"));
                MyData.put("mobile", Email);
                MyData.put("code", RandomReferCode);//Add the data you'd like to send to the server.
                MyData.put("userrefer", UserReferCode);
                MyData.put("joindate", currentTime);
                return MyData;
            }
        };
        MyRequestQueue.add(MyStringRequest);

    }

    private void parseResult(String result) {
        Log.i("InParse", "YesNONONO");
        //result=result.substring(1);
        try {
            Log.i("InParseIn", result);

            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("result");

            //feedsList = new ArrayList();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                //FeedItem item = new FeedItem();

                User_Name=post.optString("name");
                Email=post.optString("mobile");
                ReferCode=post.optString("refercode");
                Gendar=post.optString("gendar");
                CollageName=post.optString("collage");

                //   Balance= Float.parseFloat((post.optString("balance")));

                //feedsList.add(item);
                Log.i("C_Title:", post.optString("name"));
                // Log.i("Appurl:", post.optString("url"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
