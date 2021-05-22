package com.example.r_edu_kt.User.CourseLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.CourseLayout.Fragments.FragmentAdapter;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

public class CourseOverview extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;
    ImageView img;
    TextView title,desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_overview);

        //hooks
        tabLayout=findViewById(R.id.tab_layout);
        pager2=findViewById(R.id.view_pager2);

        img=(ImageView)findViewById(R.id.image);
        title=(TextView)findViewById(R.id.title);
//        desc=(TextView)findViewById(R.id.description);
        img.setImageResource(getIntent().getIntExtra("courseimage",0));
        title.setText(getIntent().getStringExtra("title").toString());
//        desc.setText(getIntent().getStringExtra("description").toString());

        FragmentManager fm=getSupportFragmentManager();


        adapter=new FragmentAdapter(fm,getLifecycle(),title.getText().toString());
        pager2.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }
}