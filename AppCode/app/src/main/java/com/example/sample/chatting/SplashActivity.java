package com.example.sample.chatting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.example.sample.chatting.ui.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    String TAG = "SplashActivity";
    public Activity activity = SplashActivity.this;
    public Context context = SplashActivity.this;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (Build.VERSION.SDK_INT >= 16) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.w("Handler", "Called");
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 3000);
    }
}
