package com.example.mobilegamedev_a0050289;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoresActivity extends AppCompatActivity{

    Button backButton;
    Button resetButton;

    TextView score1View;
    TextView score2View;
    TextView score3View;
    TextView score4View;
    TextView score5View;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);



        score1View = findViewById(R.id.score1_textView);
        score2View = findViewById(R.id.score2_textView);
        score3View = findViewById(R.id.score3_textView);
        score4View = findViewById(R.id.score4_textView);
        score5View = findViewById(R.id.score5_textView);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this::onTouchBack);

        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(this::onTouchReset);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int defaultValue = 0;
        int highscore1 = sharedPref.getInt("highscore1_key", defaultValue);
        int highscore2 = sharedPref.getInt("highscore2_key", defaultValue);
        int highscore3 = sharedPref.getInt("highscore3_key", defaultValue);
        int highscore4 = sharedPref.getInt("highscore4_key", defaultValue);
        int highscore5 = sharedPref.getInt("highscore5_key", defaultValue);

        score1View.setText(Integer.toString(highscore1));
        score2View.setText(Integer.toString(highscore2));
        score3View.setText(Integer.toString(highscore3));
        score4View.setText(Integer.toString(highscore4));
        score5View.setText(Integer.toString(highscore5));

    }

    public void onTouchBack(View view)
    {
        finish();
    }

    public void onTouchReset(View view)
    {        SharedPreferences.Editor editor = sharedPref.edit();
             editor.putInt("highscore1_key", 0);
             editor.putInt("highscore2_key", 0);
             editor.putInt("highscore3_key", 0);
             editor.putInt("highscore4_key", 0);
             editor.putInt("highscore5_key", 0);
             editor.apply();

        int defaultValue = 0;
        int highscore1 = sharedPref.getInt("highscore1_key", defaultValue);
        int highscore2 = sharedPref.getInt("highscore2_key", defaultValue);
        int highscore3 = sharedPref.getInt("highscore3_key", defaultValue);
        int highscore4 = sharedPref.getInt("highscore4_key", defaultValue);
        int highscore5 = sharedPref.getInt("highscore5_key", defaultValue);

        score1View.setText(Integer.toString(highscore1));
        score2View.setText(Integer.toString(highscore2));
        score3View.setText(Integer.toString(highscore3));
        score4View.setText(Integer.toString(highscore4));
        score5View.setText(Integer.toString(highscore5));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }
}
