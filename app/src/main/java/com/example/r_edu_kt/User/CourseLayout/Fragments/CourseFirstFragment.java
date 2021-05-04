package com.example.r_edu_kt.User.CourseLayout.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.r_edu_kt.R;

import java.util.ArrayList;
import java.util.List;

public class CourseFirstFragment extends Fragment {

    //list view
    View v;
    private RecyclerView myrecyclerview;
    private List<CourseModel> lstCourse;

    ListView list;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CourseFirstFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CourseFirstFragment newInstance(String param1, String param2) {
        CourseFirstFragment fragment = new CourseFirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        lstCourse = new ArrayList<>();
        lstCourse.add(new CourseModel("Introduction to Artificial Intelligence", "Video - 02:40mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Why do we need to learn about Artificial intelligence?", "Video - 02:40mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Advantages of artificial intelligence", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Disadvantages of artificial intelligence", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("What is a Robot?", "Video - 06:10mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Different types of a robots", "Video - 04:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Difference and similarities between robots and super computers", "Video - 04:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Introduction to Artificial Intelligence", "Video - 02:40mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Why do we need to learn about Artificial intelligence?", "Video - 02:40mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Advantages of artificial intelligence", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Disadvantages of artificial intelligence", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("What is a Robot?", "Video - 06:10mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Different types of a robots", "Video - 04:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Difference and similarities between robots and super computers", "Video - 04:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Introduction to Artificial Intelligence", "Video - 02:40mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Why do we need to learn about Artificial intelligence?", "Video - 02:40mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Advantages of artificial intelligence", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Disadvantages of artificial intelligence", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("What is a Robot?", "Video - 06:10mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Different types of a robots", "Video - 04:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("Difference and similarities between robots and super computers", "Video - 04:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_course_first, container, false);
        myrecyclerview = (RecyclerView) v.findViewById(R.id.course_first_recyclerview);
        CourseRecyclerViewAdapter courseRecyclerViewAdapter = new CourseRecyclerViewAdapter(getContext(), lstCourse);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(courseRecyclerViewAdapter);
        return v;
    }
}