package com.example.r_edu_kt.User.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

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

    MediaPlayer loginSound;

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

        //login sound
        loginSound=MediaPlayer.create(this,R.raw.alert);


        nameEt = findViewById(R.id.editTextName);
        passwordEt = findViewById(R.id.editTextPaswword);


        //loading
        buttonView = findViewById(R.id.progressLoginButton);
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressButton = new ProgressButton(LoginActivity.this, buttonView);

                progressButton.buttonActivated();
                if (!login()) {
                    progressButton.buttonFinished();
                }
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
        passwordEt.onEditorAction(EditorInfo.IME_ACTION_DONE);
        Query checkUser=FirebaseDatabase.getInstance().getReference("Users").orderByChild("userName").equalTo(name);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    nameEt.setError(null);
                    nameEt.requestFocus();
                    String systemPassword=snapshot.child(name).child("password").getValue(String.class);
                    if(systemPassword.equals(password)){
                        passwordEt.setError(null);
                        //passwordEt.requestFocus();

                        Intent intent = new Intent(LoginActivity.this, UserDashboard.class);
                        String fullName,email,phoneNumber,gender,date;
                        fullName=snapshot.child(name).child("fullName").getValue(String.class);
                        email=snapshot.child(name).child("email").getValue(String.class);
                        phoneNumber=snapshot.child(name).child("phoneNumber").getValue(String.class);
                        date=snapshot.child(name).child("date").getValue(String.class);
                        gender=snapshot.child(name).child("gender").getValue(String.class);
                        intent.putExtra("fullName",fullName);
                        intent.putExtra("userName",name);
                        intent.putExtra("password",password);
                        intent.putExtra("email",email);
                        intent.putExtra("phoneNumber",phoneNumber);
                        intent.putExtra("gender",gender);
                        intent.putExtra("date",date);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "your phone number is "+phoneNumber, Toast.LENGTH_SHORT).show();
//                        Toast.makeText(LoginActivity.this, "Welcome "+fullName+"!", Toast.LENGTH_SHORT).show();
                        loginSound.start();
                    }
                    else{
                        passwordEt.setError("Wrong Password");
                        passwordEt.requestFocus();
                        progressButton.buttonFinished();
//                        Toast.makeText(LoginActivity.this,"Password does not match",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    nameEt.setError("No such user exists");
                    nameEt.requestFocus();
                    progressButton.buttonFinished();
//                    Toast.makeText(LoginActivity.this, "No such user exists!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this,"Database Error ",Toast.LENGTH_SHORT).show();
            }
        });


        return  true;

    }

    public void callForgetPassword(View view) {
        Intent i = new Intent(getApplicationContext(), ForgetPassword.class);
        startActivity(i);
//        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
}
