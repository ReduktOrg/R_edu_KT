package com.example.r_edu_kt.Common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.r_edu_kt.User.Login.LoginActivity;
import com.example.r_edu_kt.R;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIMER =2000;

    // variables
    ImageView backgroundImage;
    TextView poweredByLine;

    //Animations
    Animation sideAnim,bottomAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        //Hooks
        backgroundImage=findViewById(R.id.background_image);
        poweredByLine=findViewById(R.id.powered_by_line);

        //Animations
        sideAnim= AnimationUtils.loadAnimation(this,R.anim.side_anim);
        bottomAnim=AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        //set animations on elements
        backgroundImage.setAnimation(sideAnim);
        poweredByLine.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIMER);
    }
}
