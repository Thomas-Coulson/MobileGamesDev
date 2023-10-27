package com.example.mobilegamedev_a0050289;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    GameView gameView;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //set gameview, and the swipe listner
        gameView = findViewById(R.id.gameView);
        gameView.setOnTouchListener(new OnSwipeTouchListener(GameActivity.this)
        {
            public void onSwipeTop() {
                gameView.movePlayerUp();
            }
            public void onSwipeRight() {
                gameView.movePlayerRight();
            }
            public void onSwipeLeft() {
                gameView.movePlayerLeft();
            }
            public void onSwipeBottom() {
                gameView.movePlayerDown();
            }
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this::onTouchBack);
    }


    public void onTouchBack(View view)
    {
        //move back to MainActivity when pressed
        finish();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (gameView != null)
            gameView.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        gameView.pause();
    }
}
