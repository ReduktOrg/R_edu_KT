package com.example.r_edu_kt.User.Register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.Login.LoginActivity;

public class VerifyOTP extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window=getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.button_blue));

        }
    }

    public void callNextScreenFromOTP(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public void backToSignUp1(View view) {
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
    }
}