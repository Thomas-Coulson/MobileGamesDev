package com.example.mobilegamedev_a0050289;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
//Main menu activity

    Button startButton;
    Button scoresButton;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this::onTouchStartGame);

        scoresButton = findViewById(R.id.scoresButton);
        scoresButton.setOnClickListener(this::onTouchScores);

        mp = MediaPlayer.create(this, R.raw.selectsound);

    }

    public void onTouchStartGame(View view)
    {
        //play select sound
        mp.start();

        //move to GameActivity when pressed
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void onTouchScores(View view)
    {
        //play select sound
        mp.start();

        //move to Scores Activity when pressed
        Intent intent = new Intent(this, ScoresActivity.class);
        startActivity(intent);
    }

}