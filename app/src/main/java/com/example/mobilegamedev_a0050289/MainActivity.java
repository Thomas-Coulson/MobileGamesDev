package com.example.mobilegamedev_a0050289;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
//Main menu activity

    Button startButton;
    Button scoresButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this::onTouchStartGame);

        scoresButton = findViewById(R.id.scoresButton);
        scoresButton.setOnClickListener(this::onTouchScores);
    }

    public void onTouchStartGame(View view)
    {
        //move to GameActivity when pressed
        //Log.d("onTouchStartGame", "Button Pressed");
        Intent intent = new Intent(this, GameActivity.class);
        //Log.d("onTouchStartGame", "Intent made");
        startActivity(intent);
    }

    public void onTouchScores(View view)
    {
        //move to Scores Activity when pressed
        Intent intent = new Intent(this, ScoresActivity.class);
        startActivity(intent);
    }

}