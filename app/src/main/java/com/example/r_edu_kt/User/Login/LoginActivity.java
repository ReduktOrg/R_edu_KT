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
import android.widget.TextView;
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

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText nameEt, passwordEt;
    TextView tv;
    String Uname="",userid;

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
                    userid = user.getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                    Intent intent = new Intent(LoginActivity.this, UserDashboard.class);
                    startActivity(intent);
                    finish();
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            Uname = user.getUserName();
                            Toast.makeText(LoginActivity.this,"Welcome "+Uname+"!",Toast.LENGTH_SHORT).show();
                            loginSound.start();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        };

        //login sound
        loginSound=MediaPlayer.create(this,R.raw.alert);


        nameEt = findViewById(R.id.editTextName);
        passwordEt = findViewById(R.id.editTextPaswword);
        tv = findViewById(R.id.textView);


        //loading
        buttonView = findViewById(R.id.progressLoginButton);
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressButton = new ProgressButton(LoginActivity.this, buttonView);

                progressButton.buttonActivated();
                if (!login()) {
                    progressButton.buttonFinished();
                    tv.setText("Login");
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
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this,UserDashboard.class);
                    startActivity(intent);
                    finish();
                    userid = mAuth.getCurrentUser().getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            Uname = user.getUserName();
                            Toast.makeText(LoginActivity.this,"Welcome "+Uname+"!",Toast.LENGTH_SHORT).show();
                            loginSound.start();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    final String email_error = "com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.";
                    final String password_error = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password.";
                    String exception = task.getException().toString();
                    if(email_error.equals(exception)){
                        nameEt.setError("Entered email is wrong");
                        nameEt.requestFocus();
                        progressButton.buttonFinished();
                        tv.setText("Login");
                    }
                    else if (password_error.equals(exception)){
                        passwordEt.setError("Entered password is wrong");
                        passwordEt.requestFocus();
                        progressButton.buttonFinished();
                        tv.setText("Login");
                    }
                    else {
                        Toast.makeText(LoginActivity.this,exception,Toast.LENGTH_SHORT).show();
                        progressButton.buttonFinished();
                        tv.setText("Login");
                    }
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
