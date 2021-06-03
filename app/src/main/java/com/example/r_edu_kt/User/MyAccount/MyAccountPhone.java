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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.discussion_home;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MyAccountPhone extends AppCompatActivity {

    EditText accountPhone;
    String account_phone;
    ImageView back;
    Button update,cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_phone);

        //STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        //hooks
        accountPhone=findViewById(R.id.account_phone_number_et);
        update = findViewById(R.id.set_new_phone_number);
        cancel = findViewById(R.id.cancel_new_full_name);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                account_phone=user.getPhoneNo();
                accountPhone.setText(account_phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyAccountPhone.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account_phone = accountPhone.getText().toString();
                if(TextUtils.isEmpty(account_phone)){
                   accountPhone.setError("Required PhoneNumber");
                   accountPhone.requestFocus();
                }else if(account_phone.length() != 10){
                    accountPhone.setError("Phone number must be 10 digits");
                    accountPhone.requestFocus();
                }
                else {
                    final ProgressDialog pd = new ProgressDialog(MyAccountPhone.this);
                    pd.setMessage("Updating your PhoneNumber");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    FirebaseDatabase database= FirebaseDatabase.getInstance();
                    final DatabaseReference reference= database.getReference("Users");

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("phoneNo",account_phone);

                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MyAccountPhone.this,"PhoneNumber updated successfully",Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MyAccountPhone.this,"could not update PhoneNumber",Toast.LENGTH_SHORT).show();
                            pd.dismiss();
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

        back=findViewById(R.id.account_phone_back_btn);
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