package com.example.mobilegamedev_a0050289;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

    //shake sensor variables
    private SensorManager sensorManager;
    private float accel; //acceleration without gravity
    private float accelCurrent; // current acceleration with gravity
    private float accelLast; //last acceleration with gravity
    private int shakeThreshold = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //set gameview, and the swipe listner
        gameView = findViewById(R.id.gameView);

        gameView.setGameActivity(this);

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

        //set up sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        accel = 0.0f;
        accelCurrent = SensorManager.GRAVITY_EARTH;
        accelLast = SensorManager.GRAVITY_EARTH;
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

    private final SensorEventListener sensorListener = new SensorEventListener()
    {
        public void onSensorChanged(SensorEvent event)
        {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            accelLast = accelCurrent;
            accelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = accelCurrent - accelLast;
            accel = accel * 0.9f + delta; // add low cut filter

            if(accel > shakeThreshold)
            {
                gameView.onShakeDetected();
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        if (gameView != null)
            gameView.resume();
        //register sensor listener
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause()
    {
        //pause sensor listener
        sensorManager.unregisterListener(sensorListener);
        super.onPause();
        gameView.pause();
    }
}
