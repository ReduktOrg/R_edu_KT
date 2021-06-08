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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class MyAccountBirthday extends AppCompatActivity {

    DatePicker datePicker;
    String date;
    ImageView back;
    Button update,cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_birthday);

        //STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        //hooks
        datePicker=findViewById(R.id.birthday_age_picker);
        update = findViewById(R.id.set_new_full_name);
        cancel = findViewById(R.id.cancel_new_full_name);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                date=user.getDate();
                String[] birth = date.split("/");
                int day = Integer.parseInt(birth[0]);
                int month = Integer.parseInt(birth[1]);
                int year = Integer.parseInt(birth[2]);
                datePicker.updateDate(year,month-1,day);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyAccountBirthday.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day=datePicker.getDayOfMonth();
                int month=datePicker.getMonth()+1;
                int year=datePicker.getYear();
                int currentYear= Calendar.getInstance().get(Calendar.YEAR);
                int isAgeValid=currentYear-year;
                if(isAgeValid<14){
                    Toast.makeText(MyAccountBirthday.this,"Age must be atleast 14 ",Toast.LENGTH_SHORT).show();
                }else{
                    String date=day+"/"+month+"/"+year;
                    final ProgressDialog pd = new ProgressDialog(MyAccountBirthday.this);
                    pd.setMessage("Updating your BirthDay");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    FirebaseDatabase database= FirebaseDatabase.getInstance();
                    final DatabaseReference reference= database.getReference("Users");

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("date",date);

                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MyAccountBirthday.this,"BirthDay updated successfully",Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MyAccountBirthday.this,"could not update BirthDay",Toast.LENGTH_SHORT).show();
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

        back=findViewById(R.id.account_birthday_back_btn);
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