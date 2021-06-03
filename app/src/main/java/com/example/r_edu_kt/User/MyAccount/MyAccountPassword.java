package com.example.r_edu_kt.User.MyAccount;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.ForgetPassword.SetNewPassword;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyAccountPassword extends AppCompatActivity {

    Button nextBtn,cancel;
    ImageView back;
    EditText current_passwordEt;
    String current_password;

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

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_password = current_passwordEt.getText().toString();
                if (TextUtils.isEmpty(current_password)) {
                    Toast.makeText(MyAccountPassword.this, "Required Password", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            String password = user.getPassword();
                            if(current_password.equals(password)){
                                Intent newPasswordIntent = new Intent(getApplicationContext(), SetNewPassword.class);
                                startActivity(newPasswordIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                            else {
                                current_passwordEt.setError("Entered password is not matching with current password");
                                current_passwordEt.requestFocus();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MyAccountPassword.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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