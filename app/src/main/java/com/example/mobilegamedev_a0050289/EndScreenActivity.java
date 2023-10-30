package com.example.mobilegamedev_a0050289;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;

public class EndScreenActivity extends AppCompatActivity {
//Main menu activity

    Button restartButton;
    Button titleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);

        restartButton = findViewById(R.id.restartButton);
        restartButton.setOnClickListener(this::onTouchRestart);

        titleButton = findViewById(R.id.titleButton);
        titleButton.setOnClickListener(this::onTouchTitle);
    }

    public void onTouchRestart(View view)
    {
        //move to GameActivity when pressed
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void onTouchTitle(View view)
    {
        //move to MainActivity when pressed
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}