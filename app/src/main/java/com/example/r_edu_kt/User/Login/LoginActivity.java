package com.example.r_edu_kt.User.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.r_edu_kt.Common.ProgressButton;
import com.example.r_edu_kt.User.ForgetPassword.ForgetPassword;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.Register.RegisterActivity;
import com.example.r_edu_kt.User.UserDashboard;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText nameEt, passwordEt;

    View buttonView;
    ProgressButton progressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for changing status bar icon color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));

        }
        setContentView(R.layout.activity_login);
        nameEt = findViewById(R.id.editTextName);
        passwordEt = findViewById(R.id.editTextPaswword);


        //loading
        buttonView = findViewById(R.id.progressLoginButton);
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressButton = new ProgressButton(LoginActivity.this, buttonView);

                progressButton.buttonActivated();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!login()) {
                            progressButton.buttonFinished();
                        }
                    }
                }, 500);
            }
        });
    }

    public void onLoginClick(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }

    public boolean login() {
        final String name, password;
        password = passwordEt.getText().toString();
        name = nameEt.getText().toString();

        if (TextUtils.isEmpty(name)) {
            nameEt.setError("Enter your username");
            nameEt.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Enter your password");
            passwordEt.requestFocus();
            return false;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser = reference.orderByChild("name").equalTo(name);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String passwordFromDB = snapshot.child(name).child("password").getValue(String.class);
                    if (password.equals(passwordFromDB)) {
                        String nameFromDB = snapshot.child(name).child("name").getValue(String.class);
                        String phoneNoFromDB = snapshot.child(name).child("phoneNo").getValue(String.class);
                        String emailFromDB = snapshot.child(name).child("email").getValue(String.class);

                        Intent intent = new Intent(LoginActivity.this, UserDashboard.class);
                        startActivity(intent);

                    } else {
                        passwordEt.setError("Wrong Password");
                        passwordEt.requestFocus();
                        progressButton.buttonFinished();
                    }
                } else {
                    nameEt.setError("No such user exists");
                    nameEt.requestFocus();
                    progressButton.buttonFinished();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return true;
    }

    public void callForgetPassword(View view) {
        Intent i = new Intent(getApplicationContext(), ForgetPassword.class);
        startActivity(i);
//        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
}
