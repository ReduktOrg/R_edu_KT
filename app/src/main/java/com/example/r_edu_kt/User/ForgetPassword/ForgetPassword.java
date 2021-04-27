package com.example.r_edu_kt.User.ForgetPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.Login.LoginActivity;

public class ForgetPassword extends AppCompatActivity {

    Animation animation;

    ImageView screenIcon,passwordIcon;
    TextView title,description;
    EditText emailTextField;
    Button nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window=getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.button_blue));
        }

        screenIcon=findViewById(R.id.forget_password_back_btn);
        passwordIcon=findViewById(R.id.forget_password_icon);
        title=findViewById(R.id.forget_password_title);
        description=findViewById(R.id.forget_password_description);
        emailTextField=findViewById(R.id.editTextEmail);
        nextBtn=findViewById(R.id.forget_password_next_btn);


        //Animation Hook
        animation = AnimationUtils.loadAnimation(this, R.anim.up_animation);

        //Set animation to all the elements
        screenIcon.setAnimation(animation);
        passwordIcon.setAnimation(animation);
        title.setAnimation(animation);
        description.setAnimation(animation);
        emailTextField.setAnimation(animation);
        nextBtn.setAnimation(animation);
    }

    public void callBackScreenFromForgetPassword(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public void verifyPhoneNumber(View view) {

        Intent intent = new Intent(getApplicationContext(), MakeSelection.class);
        startActivity(intent);
    }
}