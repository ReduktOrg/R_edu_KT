package com.example.r_edu_kt.User.Register;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r_edu_kt.R;

import java.util.Calendar;

public class SignUp2ndClass extends AppCompatActivity {

    //variables
    ImageView backBtn;
    Button next;
    TextView titleText, login,sideImage;

    RadioGroup radioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2nd_class);

        changeStatusBarColor();

        //Hooks of sign-up page
        backBtn = findViewById(R.id.sign_up_back_button);
        next = findViewById(R.id.cirRegisterButton);
        login = findViewById(R.id.sign_up_login_button);
        titleText = findViewById(R.id.sign_up_title_text);
        sideImage = findViewById(R.id.sign_up_side_image);
        radioGroup=findViewById(R.id.radio_group);
        datePicker=findViewById(R.id.age_picker);
        //Hooks end
    }

    public void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));

        }
    }

    public void callNextSignupScreen(View view) {
        if( !validateGender() || !validateAge() ){
            return;
        }
        selectedGender=findViewById(radioGroup.getCheckedRadioButtonId());
        String gender=selectedGender.getText().toString();
        int day=datePicker.getDayOfMonth();
        int month=datePicker.getMonth()+1;
        int year=datePicker.getYear();

        String date=day+"/"+month+"/"+year;


        Intent intent = new Intent(getApplicationContext(), SignUp3rdClass.class);


        //transfer data of 1st signup and this signup to 3rd
        intent.putExtra("date",date);
        intent.putExtra("gender",gender);
        Intent prevIntent=getIntent();
        intent.putExtra("fullName",prevIntent.getStringExtra("fullName"));
        intent.putExtra("userName",prevIntent.getStringExtra("userName"));
        intent.putExtra("password",prevIntent.getStringExtra("password"));
        intent.putExtra("email",prevIntent.getStringExtra("email"));


        //Add Transition
        Pair[] pairs = new Pair[5];

        pairs[0] = new Pair<View, String>(backBtn, "transition_back_btn");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(login, "transition_login_btn");
        pairs[3] = new Pair<View, String>(titleText, "transition_title_text");
        pairs[4] = new Pair<View, String>(sideImage, "transition_side_image");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp2ndClass.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

    }

    public void onLoginClick(View view) {
        finish();
    }

    public  boolean validateGender(){
        if(radioGroup.getCheckedRadioButtonId()==-1){
            Toast.makeText(this,"Please Select Gender",Toast.LENGTH_SHORT).show();
            return  false;
        }
        return  true;
    }

    public  boolean validateAge(){
        //minimum 14 yrs age

        int currentYear= Calendar.getInstance().get(Calendar.YEAR);
        int userAge=datePicker.getYear();
        int isAgeValid=currentYear-userAge;
        if(isAgeValid<14){
            Toast.makeText(this,"Age must be atleast 14 ",Toast.LENGTH_SHORT).show();
            return  false;
        }
        return  true;

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}