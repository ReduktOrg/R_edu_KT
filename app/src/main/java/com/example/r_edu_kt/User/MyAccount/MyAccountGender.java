package com.example.r_edu_kt.User.MyAccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.ForgetPassword.SetNewPassword;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MyAccountGender extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton selectedGender;
    String gender;
    ImageView back;
    Button update,cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_gender);

        //STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        update = findViewById(R.id.set_new_full_name);
        cancel = findViewById(R.id.cancel_new_full_name);
        radioGroup=findViewById(R.id.account_radio_group);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                gender=user.getGender();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyAccountGender.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender=findViewById(radioGroup.getCheckedRadioButtonId());
                String getgender = selectedGender.getText().toString();
                    final ProgressDialog pd = new ProgressDialog(MyAccountGender.this);
                    pd.setMessage("Updating your Gender");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference reference = database.getReference("Users");

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("gender", getgender);

                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MyAccountGender.this, "Gender updated successfully", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MyAccountGender.this, "could not update Gender", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //hooks
//        radioGroup=findViewById(R.id.account_radio_group);
//        selectedGender=findViewById(radioGroup.getCheckedRadioButtonId());
//        gender=getIntent().getStringExtra("gender");
//        selectedGender.setText(gender);

        back=findViewById(R.id.account_gender_back_btn);
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