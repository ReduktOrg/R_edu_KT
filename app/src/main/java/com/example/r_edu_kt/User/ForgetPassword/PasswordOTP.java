package com.example.r_edu_kt.User.ForgetPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.r_edu_kt.R;

public class PasswordOTP extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_o_t_p);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window=getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }
    }

    public void callNextScreenFromOTP(View view) {
        startActivity(new Intent(getApplicationContext(), SetNewPassword.class));
    }

    public void callForgetPassword(View view) {
        startActivity(new Intent(getApplicationContext(), ForgetPassword.class));
    }
}