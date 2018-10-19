package com.cashola;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cashola.R;

public class QuizzHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz_home_page);
        getSupportActionBar().hide();
    }

//    public void PlayBtn(View view)
//    {
//        Intent intent=new Intent(this,PaytmentGetway.class);
//        intent.putExtra("Mode","Game");
//        startActivity(intent);
//    }
    public void Instruction(View view)
    {
//        Intent intent=new Intent(this, WebViewActivity.class);
//        intent.putExtra("UrlToLoad","https://earnapp.tk/QuizzInstruction.html");
//        startActivity(intent);
    }
    public void TryMode(View view)
    {
        Intent intent=new Intent(this,Quizz.class);
        intent.putExtra("Mode","Ques");
        startActivity(intent);
    }
}
