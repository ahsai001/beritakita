package com.ahsailabs.beritakita.pages.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.ahsailabs.beritakita.R;
import com.ahsailabs.beritakita.pages.home.HomeActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        //cara 1:
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HomeActivity.start(SplashActivity.this);
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
                HomeActivity.start(SplashActivity.this);
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
