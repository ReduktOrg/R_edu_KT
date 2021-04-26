package com.example.r_edu_kt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.r_edu_kt.User.UserDashboard;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText nameEt,passwordEt;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for changing status bar icon color
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window=getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));

        }
        setContentView(R.layout.activity_login);
        nameEt=findViewById(R.id.editTextName);
        passwordEt=findViewById(R.id.editTextPaswword);
        loginButton=findViewById(R.id.cirLoginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    public void onLoginClick(View view) {
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
    void login(){
      final  String name,password;
        password=passwordEt.getText().toString();
        name=nameEt.getText().toString();

        if(TextUtils.isEmpty(name)){
            nameEt.setError("Enter your username");
            return;
        }
        else if(TextUtils.isEmpty(password)){
            passwordEt.setError("Enter your password");
            return;
        }

         DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users");

        Query checkUser=reference.orderByChild("name").equalTo(name);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   String passwordFromDB=snapshot.child(name).child("password").getValue(String.class);
                   if(password.equals(passwordFromDB)){
                       String nameFromDB=snapshot.child(name).child("name").getValue(String.class);
                       String phoneNoFromDB=snapshot.child(name).child("phoneNo").getValue(String.class);
                       String emailFromDB=snapshot.child(name).child("email").getValue(String.class);

                       Intent intent=new Intent(LoginActivity.this, UserDashboard.class);
                       startActivity(intent);

                   }
                   else{
                        passwordEt.setError("Wrong Password");
                        passwordEt.requestFocus();
                   }
               }
               else{
                   nameEt.setError("No such user exists");
                   nameEt.requestFocus();
               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void callForgetPassword(View view) {
        Intent i=new Intent(getApplicationContext(),ForgetPassword.class);
        startActivity(i);
//        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
}
