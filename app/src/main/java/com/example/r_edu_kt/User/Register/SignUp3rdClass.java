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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r_edu_kt.R;
import com.hbb20.CountryCodePicker;

public class SignUp3rdClass extends AppCompatActivity {


    //variables
    ImageView backBtn;
    Button next;
    TextView titleText, login, sideImage;

    EditText phoneNumberEt;
    CountryCodePicker countryCodePicker;

    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3rd_class);

        changeStatusBarColor();

        phoneNumberEt = findViewById(R.id.editTextMobile);
        countryCodePicker=findViewById(R.id.country_code_picker);
        relativeLayout = findViewById(R.id.signup_3rd_screen_scroll_view);


    }

    public void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void callVerifyOTPScreen(View view) {

        if (!validatePhoneNumber()) {
            return;
        }
        Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);

        Intent prevIntent = getIntent();

        intent.putExtra("phoneNumber", phoneNumberEt.getText().toString());
        intent.putExtra("fullName", prevIntent.getStringExtra("fullName"));
        intent.putExtra("userName", prevIntent.getStringExtra("userName"));
        intent.putExtra("password", prevIntent.getStringExtra("password"));
        intent.putExtra("email", prevIntent.getStringExtra("email"));
        intent.putExtra("gender", prevIntent.getStringExtra("gender"));
        intent.putExtra("date", prevIntent.getStringExtra("date"));




//Add Transition
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(relativeLayout, "transition_OTP_screen");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp3rdClass.this, pairs);
            startActivity(intent, options.toBundle());
            Toast.makeText(this,"phone number is "+phoneNumberEt.getText().toString(), Toast.LENGTH_SHORT).show();
        } else {
            startActivity(intent);
            Toast.makeText(this,"phone number is "+phoneNumberEt.getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onLoginClick(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUp2ndClass.class);

        //Add Transition
        Pair[] pairs = new Pair[5];

        pairs[0] = new Pair<View, String>(backBtn, "transition_back_btn");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(login, "transition_login_btn");
        pairs[3] = new Pair<View, String>(titleText, "transition_title_text");
        pairs[4] = new Pair<View, String>(sideImage, "transition_side_image");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp3rdClass.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public boolean validatePhoneNumber() {

        String val = phoneNumberEt.getText().toString();

        if (val.isEmpty()) {
            phoneNumberEt.setError("Field Cannot be empty");
            phoneNumberEt.requestFocus();
            return false;
        } else if (val.length() != 10) {
            phoneNumberEt.setError("Phone number must be 10 digits");
            phoneNumberEt.requestFocus();
            return false;
        }
        phoneNumberEt.setError(null);
        return true;
    }
}