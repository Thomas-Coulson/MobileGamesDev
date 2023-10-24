package com.example.mobilegamedev_a0050289;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    GameView gameView;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.gameView);
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
