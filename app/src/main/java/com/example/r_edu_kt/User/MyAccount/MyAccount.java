package com.example.r_edu_kt.User.MyAccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.CourseLayout.CourseOverview;
import com.example.r_edu_kt.User.UserDashboard;
import com.example.r_edu_kt.discussion_home;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyAccount extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    Button nameButton,birthdayButton,genderButton,passwordButton,emailButton,phoneButton;


    DrawerLayout drawerLayout;
    LinearLayout contentView;
    NavigationView navigationView;
    ImageView menuIcon;
    TextView userFullName,user_name,mail_id,user_email_header,user_full_name_text,user_birthday,user_gender,user_password,user_email,user_phone;
    static final float END_SCALE = 0.7f;

    String userName,fullName,password,email,phoneNumber,gender,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        //STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        //Hooks for navigation
        menuIcon = findViewById(R.id.my_account_menu_icon);
        contentView = findViewById(R.id.my_account_content);
        drawerLayout = findViewById(R.id.my_account_drawer_layout);
        navigationView = findViewById(R.id.my_account_navigation_view);
        naviagationDrawer();

        //data from userdashboard
        View header = navigationView.getHeaderView(0);
        user_name = header.findViewById(R.id.app_name);
        mail_id = header.findViewById(R.id.mail_id);



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                fullName = user.getFullName();
                email=user.getEmail();
                userName = user.getUserName();
                password = user.getPassword();
                phoneNumber = user.getPhoneNo();
                gender = user.getGender();
                date = user.getDate();
                user_name.setText("Hi !\n"+fullName);
                mail_id.setText(email);
                userFullName=findViewById(R.id.user_full_name);
                userFullName.setText(fullName.toUpperCase());
                user_email_header=findViewById(R.id.user_email);
                user_email_header.setText(email);
                user_full_name_text=findViewById(R.id.name_text);
                user_full_name_text.setText(fullName);
                user_birthday=findViewById(R.id.birthday_text);
                user_birthday.setText(date);
                user_gender=findViewById(R.id.gender_text);
                user_gender.setText(gender);
                user_password=findViewById(R.id.password_text);
                user_password.setText(password);
                user_email=findViewById(R.id.email_text);
                user_email.setText(email);
                user_phone=findViewById(R.id.phone_text);
                user_phone.setText(phoneNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyAccount.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        //account details


        //buttons
        nameButton=findViewById(R.id.name_button);
        birthdayButton=findViewById(R.id.birthday_button);
        genderButton=findViewById(R.id.gender_button);
        passwordButton=findViewById(R.id.password_button);
        emailButton=findViewById(R.id.email_button);
        phoneButton=findViewById(R.id.phone_button);



        //button onclicks
        nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nameIntent=new Intent(getApplicationContext(),MyAccountName.class);
                startActivity(nameIntent);
                in_animation();
            }
        });

        birthdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dateIntent=new Intent(getApplicationContext(),MyAccountBirthday.class);
                startActivity(dateIntent);
                in_animation();
            }
        });

        genderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent genderIntent=new Intent(getApplicationContext(),MyAccountGender.class);
                startActivity(genderIntent);
                in_animation();
            }
        });

        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passwordIntent=new Intent(getApplicationContext(),MyAccountPassword.class);
                startActivity(passwordIntent);
                in_animation();
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent=new Intent(getApplicationContext(),MyAccountEmail.class);
                emailIntent.putExtra("email",email);
                startActivity(emailIntent);
                in_animation();
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent=new Intent(getApplicationContext(),MyAccountPhone.class);
                phoneIntent.putExtra("phoneNumber",phoneNumber);
                startActivity(phoneIntent);
                in_animation();
            }
        });


    }

    private void in_animation() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void naviagationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else
                    drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        animateNavigateDrawer();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            intent_to_dashboard();
        }

    }

    private void animateNavigateDrawer() {
        drawerLayout.setScrimColor(getResources().getColor(R.color.colorAccent));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }

        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_discussion:
                Intent intent = new Intent(MyAccount.this, discussion_home.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.nav_home:
                Intent intent1 = new Intent(MyAccount.this,UserDashboard.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.nav_profile:
                Intent account_intent=new Intent(getApplicationContext(), MyAccount.class);
                startActivity(account_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
        return true;
    }

    private void intent_to_dashboard() {
        Intent dashboard_intent = new Intent(getApplicationContext(), UserDashboard.class);
        startActivity(dashboard_intent);
    }
}