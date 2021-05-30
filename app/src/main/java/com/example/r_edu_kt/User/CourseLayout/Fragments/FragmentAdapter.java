package com.example.r_edu_kt.User.CourseLayout.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentStateAdapter {
    String title;
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,String title) {
        super(fragmentManager, lifecycle);
        this.title=title;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new CourseSecondFragment();
            case 2:
                return new CourseThirdFragment();
            case 3:
                return new CourseFourthFragment();
            case 4:
                return new CourseFifthFragment();
        }

        ArrayList<String> imageLinks=new ArrayList<>();

        imageLinks.add("https://www.simplilearn.com/ice9/free_resources_article_thumb/Building-a-career-in-Mobile-App-Development.jpg");
        imageLinks.add("https://image.freepik.com/free-vector/app-development-illustration_52683-47931.jpg");
        imageLinks.add("https://www.digitalauthority.me/wp-content/uploads/2019/04/shutterstock_572886535.jpg");
        imageLinks.add("https://www.digitalauthority.me/wp-content/uploads/2019/04/shutterstock_1199235616.jpg");
        imageLinks.add("https://www.newgenapps.com/wp-content/uploads/2020/04/mobile-app-development.jpg");

        Fragment fragment= new CourseFirstFragment();
        Bundle bundle=new Bundle();
        bundle.putString("key",title);
        bundle.putStringArrayList("array",imageLinks);
        fragment.setArguments(bundle);
        ((CourseFirstFragment) fragment).setMyData(imageLinks);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
