package com.example.r_edu_kt.User.Register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.Login.LoginActivity;
import com.example.r_edu_kt.User.userHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {

    PinView pinFromUser;
    String CodeBySystem;
    TextView msgTv;

    String phoneNo,fullName,userName,password,dateOfBirth,email,gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.button_blue));
        }

        pinFromUser = findViewById(R.id.pin_view);

        msgTv=findViewById(R.id.msg);
        phoneNo = "+91"+getIntent().getStringExtra("phoneNumber");
        fullName=getIntent().getStringExtra("fullName");
        userName=getIntent().getStringExtra("userName");
        password=getIntent().getStringExtra("password");
        dateOfBirth=getIntent().getStringExtra("date");
        email=getIntent().getStringExtra("email");
        gender=getIntent().getStringExtra("gender");


        msgTv.setText("Enter otp sent to \n"+phoneNo);
        Toast.makeText(VerifyOTP.this,phoneNo+fullName+userName+password+gender+dateOfBirth+email,Toast.LENGTH_LONG).show();

        sendVerificationCodeToUser(phoneNo);
    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    CodeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code=phoneAuthCredential.getSmsCode();
                    if(code!=null){
                        pinFromUser.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(CodeBySystem,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(VerifyOTP.this, "Verificaiton Completed", Toast.LENGTH_SHORT).show();
                            storeNewUsersData();
                            //      startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(VerifyOTP.this, "Verification not completed! Try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void callNextScreenFromOTP(View view) {
        //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        String code=pinFromUser.getText().toString();
        if(!code.isEmpty()){
            verifyCode(code);
        }
    }

    public void backToSignUp1(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity .class));
    }

    public  void storeNewUsersData(){
        FirebaseDatabase rootNode=FirebaseDatabase.getInstance();
        DatabaseReference reference= rootNode.getReference("Users");
        userHelperClass addNewUser=new userHelperClass(fullName,userName,email,phoneNo,password,dateOfBirth,gender);
        reference.child(userName).setValue(addNewUser);
    }

}