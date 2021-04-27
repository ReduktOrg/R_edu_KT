package com.example.r_edu_kt.User.Register;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.Login.LoginActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    //variables
    ImageView backBtn;
    Button next;
    TextView titleText, login, sideImage;

    EditText fullnameEt, usernameEt, emailEt, passwordEt;
    Button registerButton;

    FirebaseDatabase rootNode;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        changeStatusBarColor();

        //Hooks of sign-up page
        backBtn = findViewById(R.id.sign_up_back_button);
        next = findViewById(R.id.cirRegisterButton);
        login = findViewById(R.id.sign_up_login_button);
        titleText = findViewById(R.id.sign_up_title_text);
        sideImage = findViewById(R.id.sign_up_side_image);
        //Hooks end


        fullnameEt = findViewById(R.id.editTextName);
        usernameEt = findViewById(R.id.editTextUserName);
        emailEt = findViewById(R.id.editTextEmail);
        passwordEt = findViewById(R.id.editTextPaswword);
        registerButton = findViewById(R.id.cirRegisterButton);

        ProgressDialog progressDialog;

//        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                register();
//            }
//        });
    }

    public void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));

        }
    }

    public void onLoginClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void callNextSignupScreen(View view) {
        if (!validateFullName() || !validateUsername() || !validateEmail() || !validatePassword())
            return;

        Intent intent = new Intent(getApplicationContext(), SignUp2ndClass.class);

        intent.putExtra("fullName", fullnameEt.getText().toString());
        intent.putExtra("userName", usernameEt.getText().toString());
        intent.putExtra("password", passwordEt.getText().toString());
        intent.putExtra("email", emailEt.getText().toString());
        //Add Transition
        Pair[] pairs = new Pair[5];

        pairs[0] = new Pair<View, String>(backBtn, "transition_back_btn");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(login, "transition_login_btn");
        pairs[3] = new Pair<View, String>(titleText, "transition_title_text");
        pairs[4] = new Pair<View, String>(sideImage, "transition_side_image");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

    }

    public boolean validateFullName() {
        String val = fullnameEt.getText().toString().trim();

        if (val.isEmpty()) {
            fullnameEt.setError("Field Cannot be empty");
            fullnameEt.requestFocus();
            return false;
        } else if (val.length() < 5) {
            fullnameEt.setError("Full name should be greater than 5");
            fullnameEt.requestFocus();
            return false;
        } else {
            fullnameEt.setError(null);
        }
        return true;
    }

    public boolean validateUsername() {
        String val = usernameEt.getText().toString().trim();

        if (val.isEmpty()) {
            usernameEt.setError("Field Cannot be empty");
            usernameEt.requestFocus();
            return false;
        } else if (val.length() < 5) {
            usernameEt.setError("Username should be greater than 5");
            usernameEt.requestFocus();
            return false;
        } else if (val.length() > 20) {
            usernameEt.setError("Username is too large!");
            usernameEt.requestFocus();
            return false;
        } else {
            usernameEt.setError(null);
//            usernameEt.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validateEmail() {
        String val = emailEt.getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if (val.isEmpty()) {
            emailEt.setError("Field Cannot be empty");
            emailEt.requestFocus();
            return false;
        } else if (!val.matches(checkEmail)) {
            emailEt.setError("Invalid Email!");
            emailEt.requestFocus();
            return false;
        } else {
            emailEt.setError(null);
//            emailEt.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validatePassword() {
        String val = passwordEt.getText().toString();


        if (val.isEmpty()) {
            passwordEt.setError("Field Cannot be empty");
            passwordEt.requestFocus();
            return false;
        } else if (val.length() < 6) {
            passwordEt.setError("Password should contain atleast 6 characters");
            passwordEt.requestFocus();
            return false;
        } else {
            passwordEt.setError(null);
//            password.setErrorEnabled(false);
        }
        return true;
    }
}
