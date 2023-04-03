package com.blogs.shiks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.blogs.shiks.ui.ViewWeb;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onStart(){
        super.onStart();
        CountDownTimer timer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long l) {
                //
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(MainActivity.this, ViewWeb.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}