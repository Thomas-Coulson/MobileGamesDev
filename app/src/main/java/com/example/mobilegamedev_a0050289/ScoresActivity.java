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

    TextView score1View;
    TextView score2View;
    TextView score3View;
    TextView score4View;
    TextView score5View;

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

        //moving to using an array and text file for high scores
        score1View.setText("No Score");
        score2View.setText("No Score");
        score3View.setText("No Score");
        score4View.setText("No Score");
        score5View.setText("No Score");

    }

    public void onTouchBack(View view)
    {
        finish();
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
