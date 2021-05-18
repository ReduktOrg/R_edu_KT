package com.example.r_edu_kt.User.Register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r_edu_kt.User.UserDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.r_edu_kt.R;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp3rdClass extends AppCompatActivity {
    private CircleImageView profileImage;
    private Uri resultUri;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private ProgressDialog loader;
    private String onlineuserID="",phoneNumber,userName,fullName,date,gender,password,email;


    //variables
    ImageView backBtn;
    Button next;
    TextView titleText, login, sideImage;

    EditText phoneNumberEt;
    CountryCodePicker countryCodePicker;

    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3rd_class);

        changeStatusBarColor();

        phoneNumberEt = findViewById(R.id.editTextMobile);
        countryCodePicker=findViewById(R.id.country_code_picker);
        profileImage=findViewById(R.id.profile_img);
        relativeLayout = findViewById(R.id.signup_3rd_screen_scroll_view);

        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            resultUri = data.getData();
            profileImage.setImageURI(resultUri);
        }
        else {
            Toast.makeText(SignUp3rdClass.this,"something went wrong",Toast.LENGTH_SHORT).show();
        }
    }

    public void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void callVerifyOTPScreen(View view) {

        Intent prevIntent = getIntent();

        phoneNumber = phoneNumberEt.getText().toString();
        fullName= prevIntent.getStringExtra("fullName");
        userName = prevIntent.getStringExtra("userName");
        password = prevIntent.getStringExtra("password");
        email = prevIntent.getStringExtra("email");
        gender = prevIntent.getStringExtra("gender");
        date = prevIntent.getStringExtra("date");

        String val = phoneNumberEt.getText().toString();

        if (val.isEmpty()) {
            phoneNumberEt.setError("Field Cannot be empty");
            phoneNumberEt.requestFocus();

        } else if (val.length() != 10) {
            phoneNumberEt.setError("Phone number must be 10 digits");
            phoneNumberEt.requestFocus();

        } else if(resultUri == null){
            Toast.makeText(SignUp3rdClass.this,"profile image is required",Toast.LENGTH_SHORT).show();
        }
        else {
            loader.setMessage("Registeration in progress");
            loader.setCanceledOnTouchOutside(false);
            loader.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        loader.dismiss();
                        Toast.makeText(SignUp3rdClass.this,"this email is already used in another account",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp3rdClass.this,RegisterActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        onlineuserID = mAuth.getCurrentUser().getUid();
                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineuserID);
                        Map hashMap =new HashMap();
                        hashMap.put("userName",userName);
                        hashMap.put("fullName",fullName);
                        hashMap.put("date",date);
                        hashMap.put("email",email);
                        hashMap.put("password",password);
                        hashMap.put("gender",gender);
                        hashMap.put("phoneNo",phoneNumber);
                        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SignUp3rdClass.this,"Details set successfully",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(SignUp3rdClass.this,"failed to upload details"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                                }
                                finish();
                                loader.dismiss();
                            }
                        });

                        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile images").child(onlineuserID);
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),resultUri);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
                        byte[] data = byteArrayOutputStream.toByteArray();
                        UploadTask uploadTask =filepath.putBytes(data);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if(taskSnapshot.getMetadata().getReference() != null){
                                    final Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageuri = uri.toString();
                                            Map hashMap = new HashMap();
                                            hashMap.put("profileimage",imageuri);
                                            reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(SignUp3rdClass.this,"profile image added successfully",Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Toast.makeText(SignUp3rdClass.this,"process failed"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                            finish();
                                        }
                                    });
                                }
                            }
                        });

                        Intent intent = new Intent(SignUp3rdClass.this, UserDashboard.class);
                        startActivity(intent);
                        finish();
                        loader.dismiss();
                    }
                }
            });

        }
    }


    public void onLoginClick(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUp2ndClass.class);

        //Add Transition
        Pair[] pairs = new Pair[5];

        pairs[0] = new Pair<View, String>(backBtn, "transition_back_btn");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(login, "transition_login_btn");
        pairs[3] = new Pair<View, String>(titleText, "transition_title_text");
        pairs[4] = new Pair<View, String>(sideImage, "transition_side_image");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp3rdClass.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }
}