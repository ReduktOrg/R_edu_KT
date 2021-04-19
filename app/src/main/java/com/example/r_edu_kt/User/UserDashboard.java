package com.example.r_edu_kt.User;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.r_edu_kt.HelperClasses.HomeAdapter.CategoriesAdapter;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.CategoriesHelperClass;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.FeaturedAdapter;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.FeaturedHelperClass;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.MostViewedAdapter;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.MostViewedHelperClass;
import com.example.r_edu_kt.R;

import java.util.ArrayList;

public class UserDashboard extends AppCompatActivity {

    RecyclerView featuredRecycler, mostViewedRecycler, categoriesRecycler;
    RecyclerView.Adapter adapter;
    private GradientDrawable gradient1, gradient2, gradient3, gradient4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_dashboard);

        //Hooks
        featuredRecycler = findViewById(R.id.featured_recycler);
        mostViewedRecycler = findViewById(R.id.most_viewed_recycler);
        categoriesRecycler = findViewById(R.id.categories_recycler);
        featuredRecycler();
        mostViewedRecycler();
        categoriesRecycler();
    }

    private void categoriesRecycler() {

        //All Gradients
        gradient2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffd4cbe5, 0xffd4cbe5});
        gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xff7adccf, 0xff7adccf});
        gradient3 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xfff7c59f, 0xFFf7c59f});
        gradient4 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffb8d7f5, 0xffb8d7f5});


        ArrayList<CategoriesHelperClass> categoriesHelperClasses = new ArrayList<>();
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient1, R.drawable.cat1, "Programming"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient2, R.drawable.cat1, "Web Design"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient3, R.drawable.cat1, "Shell scripting"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient4, R.drawable.cat1, "Dbms"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient1, R.drawable.cat1, "Operating systems"));


        categoriesRecycler.setHasFixedSize(true);
        adapter = new CategoriesAdapter(categoriesHelperClasses);
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoriesRecycler.setAdapter(adapter);

    }

    private void mostViewedRecycler() {

        mostViewedRecycler.setHasFixedSize(true);
        mostViewedRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<MostViewedHelperClass> mostViewedLocations = new ArrayList<>();
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.artificial_intelligence, "Artificial Intelligence"));
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.artificial_intelligence, "FLAT"));
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.artificial_intelligence, "Machine Learning"));
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.artificial_intelligence, "IOT"));

        adapter = new MostViewedAdapter(mostViewedLocations);
        mostViewedRecycler.setAdapter(adapter);

    }


    private void featuredRecycler() {
        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<FeaturedHelperClass> featuredCourses = new ArrayList<>();

        featuredCourses.add(new FeaturedHelperClass(R.drawable.artificial_intelligence,"Artificial Intelligence","asbkd asudhlasn saudnas jasdjasl hisajdl asjdlnas"));
        featuredCourses.add(new FeaturedHelperClass(R.drawable.artificial_intelligence,"IOT","asbkd asudhlasn saudnas jasdjasl hisajdl asjdlnas"));
        featuredCourses.add(new FeaturedHelperClass(R.drawable.artificial_intelligence,"Data analysis","asbkd asudhlasn saudnas jasdjasl hisajdl asjdlnas"));
        featuredCourses.add(new FeaturedHelperClass(R.drawable.artificial_intelligence,"App development","asbkd asudhlasn saudnas jasdjasl hisajdl asjdlnas"));


        adapter = new FeaturedAdapter(featuredCourses);
        featuredRecycler.setAdapter(adapter);


    }

}
