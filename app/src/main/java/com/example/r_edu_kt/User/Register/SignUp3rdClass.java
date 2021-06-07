package com.example.r_edu_kt.User.Register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r_edu_kt.AskQuestionActivity;
import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.User.Login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp3rdClass extends AppCompatActivity {
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private CircleImageView profileImage;
    private Uri resultUri;
    AlertDialog dialog;
    BottomSheetDialog bottomSheetDialog;
    private Task<Void> usertask;

    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    private DatabaseReference reference;
    private ProgressDialog loader;
    private String onlineuserID="",phoneNumber,userName,fullName,date,gender,password,email,Uname;
    MediaPlayer loginSound;
    private Handler mhandler = new Handler();


    //variables
    ImageView backBtn;
    Button next,register;
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
        register = findViewById(R.id.cirRegisterButton);

        loginSound=MediaPlayer.create(this,R.raw.alert);

        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog = new BottomSheetDialog(SignUp3rdClass.this,R.style.BottomSheetTheme);
                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.imgbottom, (ViewGroup) findViewById(R.id.BottomSheet));

                ImageView camera = sheetView.findViewById(R.id.camera);
                ImageView gallery = sheetView.findViewById(R.id.gallery);

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                                String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                requestPermissions(permission,PERMISSION_CODE);
                            }
                            else {
                                openCamer();
                            }
                        }
                        else {
                            openCamer();
                        }
                    }
                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        Intent intent=new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, 1);
                    }
                });
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.setCanceledOnTouchOutside(false);
                bottomSheetDialog.show();
            }
        });
    }

    private void openCamer() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
        resultUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,resultUri);
        startActivityForResult(cameraintent,IMAGE_CAPTURE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            resultUri = data.getData();
            profileImage.setImageURI(resultUri);
        }
        else if(requestCode == 1001 && resultCode == RESULT_OK){
            profileImage.setImageURI(resultUri);
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
            phoneNumberEt.clearFocus();
            loader.setMessage("Registeration in progress");
            loader.setCanceledOnTouchOutside(false);
            loader.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        loader.dismiss();
                        Toast.makeText(SignUp3rdClass.this,"failed to create account "+task.getException().toString(),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp3rdClass.this,RegisterActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    loader.dismiss();
                                    register.setEnabled(false);

                                    runnable.run();
                                    View view = LayoutInflater.from(SignUp3rdClass.this).inflate(R.layout.timerdialog,null);
                                    final TextView textView = view.findViewById(R.id.text_view);
                                    TextView display_message = view.findViewById(R.id.display_message);

                                    dialog = new AlertDialog.Builder(SignUp3rdClass.this)
                                            .setView(view).setCancelable(false).create();

                                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    dialog.show();

                                    display_message.setText(mAuth.getCurrentUser().getEmail());

                                    long duration = TimeUnit.MINUTES.toMillis(5);

                                    new CountDownTimer(duration, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            String sDuration = String.format(Locale.ENGLISH,"%02d : %02d"
                                                    ,TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                                    ,TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                                            textView.setText(sDuration);
                                        }

                                        @Override
                                        public void onFinish() {
                                            mUser = mAuth.getCurrentUser();
                                            if(mUser != null){
                                                String mailid = mUser.getEmail();
                                                AuthCredential credential = EmailAuthProvider.getCredential(mailid,password);
                                                mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            mhandler.removeCallbacks(runnable);
                                                            usertask = mAuth.getCurrentUser().reload();
                                                            usertask.addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                Toast.makeText(SignUp3rdClass.this,"Time out! please try again",Toast.LENGTH_LONG).show();
                                                                                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                                                                                finish();
                                                                            }
                                                                            else {
                                                                                Toast.makeText(SignUp3rdClass.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                        else {
                                                            Toast.makeText(SignUp3rdClass.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                            else {
                                                Toast.makeText(SignUp3rdClass.this,"Time out! please try again",Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                                                finish();
                                            }
                                            dialog.dismiss();
                                        }
                                    }.start();
                                }
                                else {
                                    loader.dismiss();
                                    Toast.makeText(SignUp3rdClass.this,"Could not process your request! please try again",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                                    finish();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            usertask = mAuth.getCurrentUser().reload();
            usertask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    if(mAuth.getCurrentUser().isEmailVerified()) {
                        onlineuserID = mAuth.getCurrentUser().getUid();
                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineuserID);
                        Map hashMap = new HashMap();
                        hashMap.put("userName", userName);
                        hashMap.put("fullName", fullName);
                        hashMap.put("date", date);
                        hashMap.put("email", email);
                        hashMap.put("password", password);
                        hashMap.put("gender", gender);
                        hashMap.put("phoneNo", phoneNumber);
                        hashMap.put("userid", onlineuserID);
                        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUp3rdClass.this, "Details Uploaded successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUp3rdClass.this, "failed to upload details" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }
                        });

                        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile images").child(onlineuserID);
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                        byte[] data = byteArrayOutputStream.toByteArray();
                        UploadTask uploadTask = filepath.putBytes(data);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    final Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageuri = uri.toString();
                                            Map hashMap = new HashMap();
                                            hashMap.put("profileimage", imageuri);
                                            reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SignUp3rdClass.this, "profile image added successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(SignUp3rdClass.this, "process failed" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                            finish();
                                        }
                                    });
                                }
                            }
                        });
                        Intent intent = new Intent(SignUp3rdClass.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        String userid = mAuth.getCurrentUser().getUid();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                Uname = user.getUserName();
                                Toast.makeText(SignUp3rdClass.this, "Welcome " + Uname + "!", Toast.LENGTH_SHORT).show();
                                loginSound.start();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        mhandler.removeCallbacks(runnable);
                        register.setEnabled(true);
                        finish();
                    }
                    else {
                        mhandler.postDelayed(runnable, 5000);
                    }
                }
            });
        }
    };


    public void onLoginClick(View view) {
        Intent intent = new Intent(SignUp3rdClass.this, SignUp2ndClass.class);

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

    @Override
    public void onBackPressed() {
        View view = LayoutInflater.from(this).inflate(R.layout.quit_dialog,null);

        Button submit=view.findViewById(R.id.postl);
        Button canc=view.findViewById(R.id.cancell);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view).setCancelable(false).create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        canc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                if (mAuth.getCurrentUser() != null) {
                    mUser = mAuth.getCurrentUser();
                    String mailid = mUser.getEmail();
                    AuthCredential credential = EmailAuthProvider.getCredential(mailid, password);
                    mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                usertask = mAuth.getCurrentUser().reload();
                                usertask.addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignUp3rdClass.this, "Your request is granted", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                    dialog.dismiss();
                    SignUp3rdClass.super.onBackPressed();
                } else {
                    dialog.dismiss();
                    SignUp3rdClass.super.onBackPressed();
                }
            }
        });
    }
}