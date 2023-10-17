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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this::onTouchStartGame);
    }

    public void onTouchStartGame(View view)
    {
        //move to GameActivity when pressed
        Log.d("onTouchStartGame", "Button Pressed");
        Intent intent = new Intent(this, GameActivity.class);
        Log.d("onTouchStartGame", "Intent made");
        startActivity(intent);
    }

}