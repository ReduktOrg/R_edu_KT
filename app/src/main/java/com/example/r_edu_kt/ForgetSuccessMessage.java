package com.example.r_edu_kt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ForgetSuccessMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_success_message);
    }

    public void backToLogin(View view) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }
}