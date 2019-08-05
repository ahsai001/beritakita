package com.ahsailabs.beritakita.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;

import com.ahsailabs.beritakita.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        //cara 1:
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent nextPageIntent = new Intent(
                        SplashActivity.this,
                        MainActivity.class);
                startActivity(nextPageIntent);

                finish();
            }
        }, 3000);


        /*
        //cara 2:
        new CountDownTimer(3000,30){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent nextPageIntent = new Intent(
                        SplashActivity.this,
                        MainActivity.class);
                startActivity(nextPageIntent);

                finish();
            }
        }.start();
        */
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            if (Build.VERSION.SDK_INT >=16) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }
}
