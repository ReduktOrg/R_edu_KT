package com.example.r_edu_kt.User.Quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.UserDashboard;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class WonActivity extends AppCompatActivity {

    CircularProgressBar circularProgressBar;
    TextView message, resultText,exit;
    int correct, wrong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_won);

        //STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.quiz_blue));
        }

        correct = getIntent().getIntExtra("correct", 0);
        wrong = getIntent().getIntExtra("wrong", 0);

        circularProgressBar = findViewById(R.id.quiz_circularProgressBar);
        resultText = findViewById(R.id.quiz_result_text);
        message = findViewById(R.id.quiz_bottom_message);

        circularProgressBar.setProgress(correct);
        resultText.setText(correct + "/10");

        if (correct >= 5) {
            message.setText("Congratulations!!"+"\n"+"You had successfully completed this Quiz");
        } else {
            message.setText("Sorry! Kindly watch the videos again and comeback.");
        }


        exit=findViewById(R.id.quiz_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), UserDashboard.class));
    }
}