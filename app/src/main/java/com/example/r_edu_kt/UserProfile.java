package com.example.r_edu_kt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity {
    String name,email,password,phoneNo;

    TextView showTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        showTv=findViewById(R.id.userdata);
        showData();//you can put it in comments

    }
    void showData(){
        Intent intent=getIntent();
        name=intent.getStringExtra("name");
        email=intent.getStringExtra("email");
        password=intent.getStringExtra("password");
        phoneNo=intent.getStringExtra("phoneNo");

        showTv.setText("Hi!\n"+name+"\n"+email+"\n"+password+"\n"+phoneNo);

    }
}
