package com.example.r_edu_kt.User.ForgetPassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.ForgetPassword.ForgetSuccessMessage;
import com.example.r_edu_kt.User.Login.LoginActivity;
import com.example.r_edu_kt.User.MyAccount.MyAccount;
import com.example.r_edu_kt.User.MyAccount.MyAccountEmail;
import com.example.r_edu_kt.User.MyAccount.MyAccountPassword;
import com.example.r_edu_kt.discussion_home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Set;

public class SetNewPassword extends AppCompatActivity {

    Button update;
    EditText new_pass,confirm_pass;
    FirebaseUser user,mUser;
    FirebaseAuth mAuth;
    private Task<Void> usertask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window=getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        update = findViewById(R.id.set_new_password_btn);
        new_pass = findViewById(R.id.editTextPaswword);
        confirm_pass = findViewById(R.id.editTextConfirmPaswword);
        user = FirebaseAuth.getInstance().getCurrentUser();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ori_pas = new_pass.getText().toString();
                String con_pas = confirm_pass.getText().toString();
                if(TextUtils.isEmpty(ori_pas)){
                    new_pass.setError("password required");
                    new_pass.requestFocus();
                    return;
                }
                else if(TextUtils.isEmpty(con_pas)){
                    confirm_pass.setError("confirm password required");
                    confirm_pass.requestFocus();
                    return;
                }

                else if(!ori_pas.equals(con_pas)){
                    confirm_pass.setError("passwords not matching");
                    confirm_pass.requestFocus();
                    return;
                }
                else {
                    mAuth = FirebaseAuth.getInstance();
                    mUser = mAuth.getCurrentUser();

                    final ProgressDialog pd = new ProgressDialog(SetNewPassword.this);
                    pd.setMessage("Updating your Password");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    final HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("password",ori_pas);

                    usertask = mAuth.getCurrentUser().reload();
                    usertask.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            mUser = mAuth.getCurrentUser();
                            mUser.updatePassword(ori_pas).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(SetNewPassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                            pd.dismiss();
                                                            Intent intent = new Intent(SetNewPassword.this,MyAccount.class);
                                                            startActivity(intent);
                                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                            finish();

                                                        } else {
                                                            Toast.makeText(SetNewPassword.this, "could not update Password", Toast.LENGTH_SHORT).show();
                                                            pd.dismiss();
                                                        }
                                                    }
                                                });
                                    }
                                    else {
                                        Toast.makeText(SetNewPassword.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                    }
                                }
                            });
                        }
                    });

                }
            }
        });
    }


    public void goToHomeFromSetNewPassword(View view) {
        onBackPressed();
    }

    public void setNewPasswordBtn(View view) {
        startActivity(new Intent(getApplicationContext(), ForgetSuccessMessage.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}