package com.example.mobileapp_haveat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(){

            @Override
            public void run() {
                try {
                    sleep(3000);//screen freezes for 3 seconds
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally{//And then intens to login activity to login or register users.
                    Intent welcomeIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(welcomeIntent);
                }
            }
        };
        thread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}