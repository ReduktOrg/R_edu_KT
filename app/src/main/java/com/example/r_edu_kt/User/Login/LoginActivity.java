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
import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.User.ForgetPassword.ForgetPassword;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.Register.RegisterActivity;
import com.example.r_edu_kt.User.UserDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private FirebaseAuth mAuth;

    MediaPlayer loginSound;
    private FirebaseAuth.AuthStateListener authStateListener;

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

        mAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=mAuth.getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(LoginActivity.this, UserDashboard.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

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
            nameEt.setError("Enter your Email");
            nameEt.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Enter your password");
            passwordEt.requestFocus();
            return false;
        }
        passwordEt.onEditorAction(EditorInfo.IME_ACTION_DONE);

        mAuth.signInWithEmailAndPassword(name,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this,UserDashboard.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginActivity.this,"Welcome"+mAuth.getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();
                    loginSound.start();
                }
                else {
                    Toast.makeText(LoginActivity.this,"login failed"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        return true;
    }


    public void callForgetPassword(View view) {
        Intent i = new Intent(getApplicationContext(), ForgetPassword.class);
        startActivity(i);
//        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}
