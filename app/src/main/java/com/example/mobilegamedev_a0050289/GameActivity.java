package com.example.mobilegamedev_a0050289;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    GameView gameView;
    //Button backButton;
    //public TextView coinText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //coinText = findViewById(R.id.CoinCount);

        //set gameview, and the swipe listner
        gameView = findViewById(R.id.gameView);
        //gameView.setActivity(this);
        //gameView.setCoinText(coinText);
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

        //backButton = findViewById(R.id.backButton);
        //backButton.setOnClickListener(this::onTouchBack);



        //Log.v("WindowSize", "WindowSize = " + gameView.getWidth() + "," + gameView.getHeight());
    }


    public void onTouchBack(View view)
    {
        //move back to MainActivity when pressed
        finish();
    }

    public void onTouchEnd(View view)
    {
        //cycle game
        Intent intent = new Intent(this, EndScreenActivity.class);
        startActivity(intent);
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
