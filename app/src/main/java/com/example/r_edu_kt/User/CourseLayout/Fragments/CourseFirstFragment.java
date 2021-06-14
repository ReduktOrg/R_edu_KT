package com.example.r_edu_kt.User.CourseLayout.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.CourseLayout.CourseOverview;

import java.util.ArrayList;
import java.util.List;

public class CourseFirstFragment extends Fragment {

    //list view
    View v;
    String title;
    private RecyclerView myrecyclerview;
    private List<CourseModel> lstCourse;


    ArrayList<String> imageLinks,videoLinks;


    ListView list;

    public CourseFirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_course_first, container, false);
        Bundle bundle=this.getArguments();
        title=bundle.getString("key");





        lstCourse = new ArrayList<>();
        lstCourse.add(new CourseModel(" video1", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video2", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video3", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video4", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video5", "Video - 06:10mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video6", "Video - 04:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video7", "Video - 04:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video8", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video9", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video10", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video11", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video12", "Video - 06:10mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video13", "Video - 04:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video14", "Video - 04:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video15", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video16", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video17", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video18", "Video - 03:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video19", "Video - 06:10mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video20", "Video - 04:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));
        lstCourse.add(new CourseModel("video21", "Video - 04:20mins", R.drawable.baseline_play_circle_filled_black_48,R.drawable.baseline_check_circle_black_48));


        videoLinks=new ArrayList<>();
        videoLinks.add("https://i.imgur.com/6kQ7fGW.mp4");
        videoLinks.add("https://i.imgur.com/IsQr1Fl.mp4");
        videoLinks.add("https://i.imgur.com/YnUELTm.mp4");
        videoLinks.add("https://i.imgur.com/7bMqysJ.mp4");
        videoLinks.add("https://i.imgur.com/ohrqpvs.mp4");

        for(int i=0;i<lstCourse.size();i++){
            lstCourse.get(i).setMainTitle(title+" "+lstCourse.get(i).getMainTitle());
        }

        myrecyclerview = (RecyclerView) v.findViewById(R.id.course_first_recyclerview);
        CourseRecyclerViewAdapter courseRecyclerViewAdapter = new CourseRecyclerViewAdapter(getContext(), lstCourse,
                new CourseRecyclerViewAdapter.onItemClickListenerInterface(){
                    @Override
                    public  void onItemClick(int position){
//                        Toast.makeText(getActivity(),"hello",Toast.LENGTH_SHORT).show();
                        ImageView imgView=(ImageView) getActivity().findViewById(R.id.image);

                        ((CourseOverview)getActivity()).setUpExoPlayer(videoLinks.get(position%5));
                        int md=position%5;
//                        if(md==0)
//                            imgView.setImageResource(R.drawable.app5);
//                        else if(md==1)
//                            imgView.setImageResource(R.drawable.app1);
//                        else if(md==2)
//                            imgView.setImageResource(R.drawable.app2);
//                        else if(md==3)
//                            imgView.setImageResource(R.drawable.app3);
//                        else
//                            imgView.setImageResource(R.drawable.app4);

                        Glide.with(getActivity())
                                .load(imageLinks.get(md)).into(imgView);
//                        imgView.setImageResource((int)(appDevImages.get(position%3)));
                    }
                });
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(courseRecyclerViewAdapter);





        return v;
    }

    void  setMyData(ArrayList<String> list){
        imageLinks=list;
    }

}