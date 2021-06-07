package com.example.r_edu_kt.User.MyAccount;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.ForgetPassword.SetNewPassword;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class MyAccountPassword extends AppCompatActivity {

    Button nextBtn,cancel;
    ImageView back;
    EditText current_passwordEt;
    TextView email;
    String current_password,mailid;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_password);

        //STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        nextBtn = findViewById(R.id.account_next_btn);
        cancel = findViewById(R.id.cancel_password_change);
        current_passwordEt = findViewById(R.id.full_name);
        email = findViewById(R.id.email);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mailid = mUser.getEmail();
        email.setText(mailid);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_password = current_passwordEt.getText().toString();
                if (TextUtils.isEmpty(current_password)) {
                    Toast.makeText(MyAccountPassword.this, "Required Password", Toast.LENGTH_SHORT).show();
                } else {
                    final ProgressDialog pd = new ProgressDialog(MyAccountPassword.this);
                    pd.setMessage("Checking your credentials");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    AuthCredential credential = EmailAuthProvider.getCredential(mailid,current_password);
                    mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                pd.dismiss();
                                Intent newPasswordIntent = new Intent(getApplicationContext(), SetNewPassword.class);
                                startActivity(newPasswordIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                            else {
                                pd.dismiss();
                                current_passwordEt.setError("password is wrong");
                                current_passwordEt.requestFocus();
                            }
                        }
                    });
                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        back=findViewById(R.id.account_password_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}