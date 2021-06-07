package com.example.r_edu_kt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r_edu_kt.User.Login.LoginActivity;
import com.example.r_edu_kt.User.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class account_recovery extends AppCompatActivity {
    Button submit,cancel;
    TextView email;
    EditText password;
    String mail,pass;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_recovery);

        submit = findViewById(R.id.submit);
        cancel = findViewById(R.id.cancel);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        Intent prevIntent = getIntent();
        mail = prevIntent.getStringExtra("mail");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if(mail != null){
            email.setText(mail);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = password.getText().toString();
                if(TextUtils.isEmpty(pass)){
                    password.setError("password required");
                    password.requestFocus();
                }
                else {
                    final ProgressDialog progressDialog = new ProgressDialog(account_recovery.this);
                    progressDialog.setMessage("Recovering your email");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    AuthCredential credential = EmailAuthProvider.getCredential(mail, pass);
                    mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(account_recovery.this,"Succesfully recovered your mail, now you can create your account",Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(account_recovery.this, RegisterActivity.class));
                                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    }
                                });
                            }
                            else {
                                password.setError("wrong password");
                                password.requestFocus();
                            }
                        }
                    });
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
    }
}