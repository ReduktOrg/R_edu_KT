package com.example.r_edu_kt.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.r_edu_kt.HelperClasses.HomeAdapter.CategoriesAdapter;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.CategoriesHelperClass;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.FeaturedAdapter;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.FeaturedHelperClass;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.MostViewedAdapter;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.MostViewedHelperClass;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.CourseLayout.CourseOverview;
import com.example.r_edu_kt.User.MyAccount.MyAccount;
import com.example.r_edu_kt.User.Quiz.QuizIntro;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class UserDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static final float END_SCALE = 0.7f;
    RecyclerView featuredRecycler, mostViewedRecycler, categoriesRecycler;
    RecyclerView.Adapter adapter;
    private GradientDrawable gradient1, gradient2, gradient3, gradient4;
    ImageView menuIcon, cseIcon,mechIcon;
    LinearLayout contentView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    String userName,fullName,password,email,phoneNumber,gender,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_dashboard);

        //STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }


        //Hooks
        featuredRecycler = findViewById(R.id.featured_recycler);
        mostViewedRecycler = findViewById(R.id.most_viewed_recycler);
        categoriesRecycler = findViewById(R.id.categories_recycler);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);
        //menu hooks
        drawerLayout = findViewById(R.id.drawer_layout);

        //codefor hi + userName
        navigationView = findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        TextView app_nameEt = header.findViewById(R.id.app_name);
        TextView mail_id = header.findViewById(R.id.mail_id);
        fullName = getIntent().getStringExtra("fullName");
        userName = getIntent().getStringExtra("userName");
        password = getIntent().getStringExtra("password");
        email = getIntent().getStringExtra("email");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        gender = getIntent().getStringExtra("gender");
        date = getIntent().getStringExtra("date");
        app_nameEt.setText("Hi ! " + userName);
        mail_id.setText(email);

        //course hooks
        cseIcon = findViewById(R.id.cse);
        cseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CourseOverview.class));
            }
        });

        //quiz hooks
        mechIcon=findViewById(R.id.mech);
        mechIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), QuizIntro.class));
            }
        });

        naviagationDrawer();

        featuredRecycler();
        mostViewedRecycler();
        categoriesRecycler();


    }

    private void naviagationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        animateNavigateDrawer();
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
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                Intent account_intent=new Intent(getApplicationContext(), MyAccount.class);
                account_intent.putExtra("userName",userName);
                account_intent.putExtra("fullName",fullName);
                account_intent.putExtra("password",password);
                account_intent.putExtra("email",email);
                account_intent.putExtra("phoneNumber",phoneNumber);
                account_intent.putExtra("gender",gender);
                account_intent.putExtra("date",date);
                startActivity(account_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }

        return true;
    }


    private void categoriesRecycler() {

        //All Gradients
        gradient2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffd4cbe5, 0xffd4cbe5});
        gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xff7adccf, 0xff7adccf});
        gradient3 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xfff7c59f, 0xFFf7c59f});
        gradient4 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffb8d7f5, 0xffb8d7f5});


        ArrayList<CategoriesHelperClass> categoriesHelperClasses = new ArrayList<>();
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient4, R.drawable.dbms, "Dbms"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient1, R.drawable.os, "Operating systems"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient2, R.drawable.web_design, "Web Design"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient3, R.drawable.shell, "Shell script"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient1, R.drawable.programm, "Programming"));


        categoriesRecycler.setHasFixedSize(true);
        adapter = new CategoriesAdapter(categoriesHelperClasses);
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoriesRecycler.setAdapter(adapter);

    }

    private void mostViewedRecycler() {

        mostViewedRecycler.setHasFixedSize(true);
        mostViewedRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<MostViewedHelperClass> mostViewedLocations = new ArrayList<>();
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.machinelearning, "Machine Learning"));
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.artificial_intelligence, "Artificial Intelligence"));
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.cloudcomputing, "Cloud Computing"));
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.iot, "IOT"));

        adapter = new MostViewedAdapter(mostViewedLocations,getApplicationContext());
        mostViewedRecycler.setAdapter(adapter);

    }

    private void featuredRecycler() {
        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<FeaturedHelperClass> featuredCourses = new ArrayList<>();

        featuredCourses.add(new FeaturedHelperClass(R.drawable.mad, "App development", "asbkd asudhlasn saudnas jasdjasl hisajdl asjdlnas"));
        featuredCourses.add(new FeaturedHelperClass(R.drawable.data_analysis, "Data analysis", "asbkd asudhlasn saudnas jasdjasl hisajdl asjdlnas"));
        featuredCourses.add(new FeaturedHelperClass(R.drawable.artificial_intelligence, "Artificial Intelligence", "asbkd asudhlasn saudnas jasdjasl hisajdl asjdlnas"));
        featuredCourses.add(new FeaturedHelperClass(R.drawable.iot, "IOT", "asbkd asudhlasn saudnas jasdjasl hisajdl asjdlnas"));


        adapter = new FeaturedAdapter(featuredCourses,getApplicationContext());
        featuredRecycler.setAdapter(adapter);


    }

}
