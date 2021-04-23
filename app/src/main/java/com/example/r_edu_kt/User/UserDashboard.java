package com.example.r_edu_kt.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class UserDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static final float END_SCALE = 0.7f;
    RecyclerView featuredRecycler, mostViewedRecycler, categoriesRecycler;
    RecyclerView.Adapter adapter;
    private GradientDrawable gradient1, gradient2, gradient3, gradient4;
    ImageView menuIcon;
    LinearLayout contentView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_dashboard);

        //Hooks
        featuredRecycler = findViewById(R.id.featured_recycler);
        mostViewedRecycler = findViewById(R.id.most_viewed_recycler);
        categoriesRecycler = findViewById(R.id.categories_recycler);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);
        //menu hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
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

        adapter = new MostViewedAdapter(mostViewedLocations);
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


        adapter = new FeaturedAdapter(featuredCourses);
        featuredRecycler.setAdapter(adapter);


    }

}
