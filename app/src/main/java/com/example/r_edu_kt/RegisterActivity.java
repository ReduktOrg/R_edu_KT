package com.example.r_edu_kt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    //variables
    ImageView backBtn, sideImage;
    Button next;
    TextView titleText, login;

    EditText nameEt, emailEt, phonenumEt, passwordEt;
    Button registerButton;

    FirebaseDatabase rootNode;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        changeStatusBarColor();

        //Hooks of sign-up page
        backBtn = findViewById(R.id.sign_up_back_button);
        next = findViewById(R.id.cirRegisterButton);
        login = findViewById(R.id.sign_up_login_button);
        titleText = findViewById(R.id.sign_up_title_text);
        sideImage = findViewById(R.id.sign_up_side_image);
        //Hooks end


        nameEt = findViewById(R.id.editTextName);
        emailEt = findViewById(R.id.editTextEmail);
        phonenumEt = findViewById(R.id.editTextMobile);
        passwordEt = findViewById(R.id.editTextPaswword);
        registerButton = findViewById(R.id.cirRegisterButton);

        ProgressDialog progressDialog;

//        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                register();
//            }
//        });
    }

    public void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));

        }
    }

    public void onLoginClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void callNextSignupScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUp2ndClass.class);

        //Add Transition
        Pair[] pairs = new Pair[5];

        pairs[0] = new Pair<View, String>(backBtn, "transition_back_btn");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(login, "transition_login_btn");
        pairs[3] = new Pair<View, String>(titleText, "transition_title_text");
        pairs[4] = new Pair<View, String>(sideImage, "transition_side_image");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

    }
//    void register(){
//        rootNode =FirebaseDatabase.getInstance();
//        reference=rootNode.getReference("users");
//       final String name,email,password,phoneNo;
//        name=nameEt.getText().toString();
//        email=emailEt.getText().toString();
//        password=passwordEt.getText().toString();
//        phoneNo=phonenumEt.getText().toString();
//
//         if(TextUtils.isEmpty(name)){
//            nameEt.setError("Enter your name");
//            return;
//        }
//        else if(TextUtils.isEmpty(email)){
//            emailEt.setError("Enter your mail");
//            return;
//        }
//        else if(!isValidEmail(email)){
//            emailEt.setError("Enter correct EmailAddress");
//            return;
//         }
//        else if(TextUtils.isEmpty(phoneNo)){
//             phonenumEt.setError("Enter your PhoneNumber");
//             return;
//         }
//        else if(TextUtils.isEmpty(password)){
//            passwordEt.setError("Enter your password");
//            return;
//        }
//        else if(name.length()<6){
//            nameEt.setError("Name should be atleast 6 characters");
//            return;
//         }
//        else if(phoneNo.length()!=10){
//            phonenumEt.setError("phone number should contain 10 digits");
//            return;
//         }
//        else if(password.length()<6){
//            passwordEt.setError("Password should be atleast 6 characters");
//            return;
//        }
//
//
//
//        Query checkUser=reference.orderByChild("name").equalTo(name);
//        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    Toast.makeText(RegisterActivity.this,"user already exists",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    userHelperClass helperClass=new userHelperClass(name,email,phoneNo,password);
//                    reference.child(name).setValue(helperClass);
//                    Toast.makeText(RegisterActivity.this,"Register Successfull",Toast.LENGTH_LONG).show();
//                    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
//                    startActivity(intent);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}
