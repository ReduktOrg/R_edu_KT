package com.example.r_edu_kt.User.MyAccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.ForgetPassword.SetNewPassword;
import com.example.r_edu_kt.User.Login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;

public class MyAccountEmail extends AppCompatActivity {

    EditText emailId;
    String email_id,email;
    ImageView back;
    Button update,cancel;
    FirebaseAuth mAuth;
    private Task<Void> usertask;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_email);

        //STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        //hooks
        emailId=findViewById(R.id.account_email_et);
        update = findViewById(R.id.set_new_full_name);
        cancel = findViewById(R.id.cancel_new_full_name);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                email_id= user.getEmail();
                emailId.setText(email_id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyAccountEmail.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

       update.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               email = emailId.getText().toString();
               if (TextUtils.isEmpty(email)) {
                   emailId.setError("Required email");
                   emailId.requestFocus();
               } else {
                   mAuth = FirebaseAuth.getInstance();
                   mUser = mAuth.getCurrentUser();
                   emailId.clearFocus();
                   View view = LayoutInflater.from(MyAccountEmail.this).inflate(R.layout.authenticate_dialog,null);

                   Button authenticate=view.findViewById(R.id.auth);
                   Button canc=view.findViewById(R.id.canc);
                   final TextView Email = view.findViewById(R.id.email);
                   final EditText passwordEt = view.findViewById(R.id.password);

                   final String emailfrom_user = mUser.getEmail();

                   final AlertDialog dialog = new AlertDialog.Builder(MyAccountEmail.this)
                           .setView(view).setCancelable(false).create();
                   dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                   dialog.show();

                   Email.setText(emailfrom_user);

                   canc.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dialog.dismiss();
                       }
                   });

                   authenticate.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           final String passwordfrom_user = passwordEt.getText().toString();
                           if (TextUtils.isEmpty(passwordfrom_user)) {
                               passwordEt.setError("Enter your password");
                               passwordEt.requestFocus();
                           }
                           else{
                               final ProgressDialog pds = new ProgressDialog(MyAccountEmail.this);
                               pds.setMessage("Checking your credentials");
                               pds.setCanceledOnTouchOutside(false);
                               pds.show();

                               AuthCredential credential = EmailAuthProvider.getCredential(emailfrom_user,passwordfrom_user);
                               mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull @NotNull Task<Void> task) {
                                       if(task.isSuccessful()){
                                           dialog.dismiss();
                                           pds.dismiss();
                                           final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                           final HashMap<String, Object> hashMap = new HashMap<>();
                                           hashMap.put("email", email);

                                           usertask = mAuth.getCurrentUser().reload();
                                           usertask.addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void unused) {
                                                   final ProgressDialog pd = new ProgressDialog(MyAccountEmail.this);
                                                   pd.setMessage("Updating your Email");
                                                   pd.setCanceledOnTouchOutside(false);
                                                   pd.show();

                                                   mUser = mAuth.getCurrentUser();
                                                   mUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                           if (task.isSuccessful()) {
                                                               reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap)
                                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                               if (task.isSuccessful()) {
                                                                                   Toast.makeText(MyAccountEmail.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                                                                   pd.dismiss();
                                                                                   finish();
                                                                               } else {
                                                                                   Toast.makeText(MyAccountEmail.this, "could not update Email", Toast.LENGTH_SHORT).show();
                                                                                   pd.dismiss();
                                                                               }
                                                                           }
                                                                       });
                                                           } else {
                                                               Toast.makeText(MyAccountEmail.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                               pd.dismiss();
                                                           }
                                                       }
                                                   });
                                               }
                                           });
                                       }else {
                                           pds.dismiss();
                                           passwordEt.setError("password is wrong");
                                           passwordEt.requestFocus();
                                       }
                                   }
                               });
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

        back=findViewById(R.id.account_email_back_btn);
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