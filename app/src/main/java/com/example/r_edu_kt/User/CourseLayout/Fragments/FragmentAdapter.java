package com.example.r_edu_kt.User.CourseLayout.Fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
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
        return new CourseFirstFragment();
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
