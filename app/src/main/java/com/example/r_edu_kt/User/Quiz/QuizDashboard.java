package com.example.r_edu_kt.User.Quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.UserDashboard;
import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.example.r_edu_kt.User.Quiz.QuizIntro.list;

public class QuizDashboard extends AppCompatActivity {

    CountDownTimer countDownTimer;
    int timerValue = 20;
    RoundedHorizontalProgressBar progressBar;
    List<ModelClass> allQuetionsList;
    ModelClass modelclass;
    int index = 0;
    TextView card_quetion, optiona, optionb, optionc, optiond,exit;
    CardView cardOA, cardOB, cardOC, cardOD;
    int correctCount = 0;
    int wrongCount = 0;
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_dashboard);

        //STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.quiz_blue));
        }

        hooks();
        allQuetionsList = list;
        Collections.shuffle(allQuetionsList);
        modelclass = list.get(index);

        cardOA.setBackgroundColor(getResources().getColor(R.color.white));
        cardOB.setBackgroundColor(getResources().getColor(R.color.white));
        cardOC.setBackgroundColor(getResources().getColor(R.color.white));
        cardOD.setBackgroundColor(getResources().getColor(R.color.white));
        nextBtn.setClickable(false);



        //countdown timer
        countDownTimer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerValue = timerValue - 1;
                progressBar.setProgress(timerValue);
            }

            @Override
            public void onFinish() {
                Dialog dialog = new Dialog(QuizDashboard.this);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                dialog.setContentView(R.layout.time_out_dialog);


                dialog.findViewById(R.id.btn_tryagain).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(QuizDashboard.this, QuizIntro.class);
                        startActivity(intent);
                    }
                });

                dialog.show();

            }
        }.start();

        setAllData();

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

    private void setAllData() {

        card_quetion.setText(modelclass.getQuestion());
        optiona.setText(modelclass.getoA());
        optionb.setText(modelclass.getoB());
        optionc.setText(modelclass.getoC());
        optiond.setText(modelclass.getoD());
        timerValue = 20;
        countDownTimer.cancel();
        countDownTimer.start();
    }

    private void hooks() {

        progressBar = findViewById(R.id.quiz_timer);
        card_quetion = findViewById(R.id.quiz_card_question);
        optiona = findViewById(R.id.quiz_option_a);
        optionb = findViewById(R.id.quiz_option_b);
        optionc = findViewById(R.id.quiz_option_c);
        optiond = findViewById(R.id.quiz_option_d);

        cardOA = findViewById(R.id.cardA);
        cardOB = findViewById(R.id.cardB);
        cardOC = findViewById(R.id.cardC);
        cardOD = findViewById(R.id.cardD);

        nextBtn = findViewById(R.id.quiz_next_btn);
        exit=findViewById(R.id.quiz_exit_btn);

    }

    public void Correct(CardView cardView) {

        cardView.setBackgroundColor(getResources().getColor(R.color.green));

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correctCount++;
                if (index < list.size() - 1) {
                    index++;
                    modelclass = list.get(index);
                    resetColor();
                    setAllData();
                    enableButton();
                } else {
                    GameWon();
                }
            }
        });
    }


    public void Wrong(CardView cardOA) {

        cardOA.setBackgroundColor(getResources().getColor(R.color.red));
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrongCount++;
                if (index < list.size() - 1) {
                    index++;
                    modelclass = list.get(index);
                    resetColor();
                    setAllData();
                    enableButton();

                } else {
                    GameWon();
                }
            }
        });
    }

    private void GameWon() {
        Intent intent = new Intent(QuizDashboard.this, WonActivity.class);
        intent.putExtra("correct", correctCount);
        intent.putExtra("wrong", wrongCount);
        startActivity(intent);
    }

    public void enableButton() {
        cardOA.setClickable(true);
        cardOB.setClickable(true);
        cardOC.setClickable(true);
        cardOD.setClickable(true);
    }

    public void disableButton() {
        cardOA.setClickable(false);
        cardOB.setClickable(false);
        cardOC.setClickable(false);
        cardOD.setClickable(false);
    }

    public void resetColor() {
        cardOA.setBackgroundColor(getResources().getColor(R.color.white));
        cardOB.setBackgroundColor(getResources().getColor(R.color.white));
        cardOC.setBackgroundColor(getResources().getColor(R.color.white));
        cardOD.setBackgroundColor(getResources().getColor(R.color.white));
    }

    public void OptionAclick(View view) {
        disableButton();
        nextBtn.setClickable(true);
        if (modelclass.getoA().equals(modelclass.getAns())) {
            cardOA.setCardBackgroundColor(getResources().getColor(R.color.green));

            if (index < list.size()) {
                Correct(cardOA);

            } else {
                GameWon();
            }

        } else {
            Wrong(cardOA);
        }
    }

    public void OptionBclick(View view) {
        disableButton();
        nextBtn.setClickable(true);
        if (modelclass.getoB().equals(modelclass.getAns())) {
            cardOB.setCardBackgroundColor(getResources().getColor(R.color.green));

            if (index < list.size()) {
                Correct(cardOB);

            } else {
                GameWon();
            }

        } else {
            Wrong(cardOB);
        }
    }

    public void OptionCclick(View view) {
        disableButton();
        nextBtn.setClickable(true);
        if (modelclass.getoC().equals(modelclass.getAns())) {
            cardOC.setCardBackgroundColor(getResources().getColor(R.color.green));

            if (index < list.size()) {
                Correct(cardOC);

            } else {
                GameWon();
            }

        } else {
            Wrong(cardOC);
        }
    }

    public void OptionDclick(View view) {
        disableButton();
        nextBtn.setClickable(true);
        if (modelclass.getoD().equals(modelclass.getAns())) {
            cardOD.setCardBackgroundColor(getResources().getColor(R.color.green));

            if (index < list.size()) {

                Correct(cardOD);

            } else {
                GameWon();
            }

        } else {
            Wrong(cardOD);
        }
    }
}