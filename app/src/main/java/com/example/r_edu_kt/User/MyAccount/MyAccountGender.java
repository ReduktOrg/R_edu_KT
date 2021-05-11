package com.example.r_edu_kt.User.MyAccount;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.r_edu_kt.R;

public class MyAccountGender extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton selectedGender;
    String gender;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_gender);

        //STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        //hooks
//        radioGroup=findViewById(R.id.account_radio_group);
//        selectedGender=findViewById(radioGroup.getCheckedRadioButtonId());
//        gender=getIntent().getStringExtra("gender");
//        selectedGender.setText(gender);

        back=findViewById(R.id.account_gender_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}