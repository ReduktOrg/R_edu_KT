package com.example.r_edu_kt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {


    EditText nameEt,emailEt,phonenumEt,passwordEt;
    Button registerButton;

    FirebaseDatabase rootNode;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        changeStatusBarColor();


        nameEt=findViewById(R.id.editTextName);
        emailEt=findViewById(R.id.editTextEmail);
        phonenumEt=findViewById(R.id.editTextMobile);
        passwordEt=findViewById(R.id.editTextPaswword);
        registerButton=findViewById(R.id.cirRegisterButton);

        ProgressDialog progressDialog;

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }
    public void changeStatusBarColor()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window=getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));

        }
    }
    public void  onLoginClick(View view)
    {
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    void register(){
        rootNode =FirebaseDatabase.getInstance();
        reference=rootNode.getReference("users");
       final String name,email,password,phoneNo;
        name=nameEt.getText().toString();
        email=emailEt.getText().toString();
        password=passwordEt.getText().toString();
        phoneNo=phonenumEt.getText().toString();

         if(TextUtils.isEmpty(name)){
            passwordEt.setError("Enter your name");
            return;
        }
        else if(TextUtils.isEmpty(email)){
            emailEt.setError("Enter your mail");
            return;
        }
        else if(TextUtils.isEmpty(phoneNo)){
             passwordEt.setError("Enter your password1");
             return;
         }
        else if(TextUtils.isEmpty(password)){
            passwordEt.setError("Enter your password1");
            return;
        }


        Query checkUser=reference.orderByChild("name").equalTo(name);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(RegisterActivity.this,"user already exists",Toast.LENGTH_SHORT).show();
                }
                else{
                    userHelperClass helperClass=new userHelperClass(name,email,phoneNo,password);
                    reference.child(name).setValue(helperClass);
                    Toast.makeText(RegisterActivity.this,"Register Successfull",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
