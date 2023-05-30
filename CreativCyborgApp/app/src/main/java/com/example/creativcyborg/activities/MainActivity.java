package com.example.creativcyborg.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.creativcyborg.activities.glass.SelectRoomOnGlassActivity;
import com.example.creativcyborg.activities.phone.LandingActivity;
import com.example.creativcyborg.activities.phone.introduction.IntroductionActivity;
import com.example.creativcyborg.R;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.MODEL.contains("Glass"))
        {
            Intent intent = new Intent(MainActivity.this, SelectRoomOnGlassActivity.class);
            MainActivity.this.startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(MainActivity.this, IntroductionActivity.class);
            MainActivity.this.startActivity(intent);
        }
    }
}