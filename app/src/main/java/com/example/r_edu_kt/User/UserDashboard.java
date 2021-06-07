package com.example.r_edu_kt.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.r_edu_kt.Common.NetWorkChangeListener;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.CategoriesAdapter;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.CategoriesHelperClass;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.FeaturedAdapter;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.FeaturedHelperClass;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.MostViewedAdapter;
import com.example.r_edu_kt.HelperClasses.HomeAdapter.MostViewedHelperClass;
import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.Login.LoginActivity;
import com.example.r_edu_kt.User.MyAccount.MyAccount;
import com.example.r_edu_kt.User.Quiz.QuizIntro;
import com.example.r_edu_kt.discussion_home;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();

    static final float END_SCALE = 0.7f;
    RecyclerView featuredRecycler, mostViewedRecycler, categoriesRecycler;
    RecyclerView.Adapter adapter;
    private GradientDrawable gradient1, gradient2, gradient3, gradient4;
    ImageView menuIcon, cseIcon,mechIcon;
    LinearLayout contentView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    String fullName,email;
    Context context;

    FloatingActionButton toDiso;

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

        //to discussion forum
        toDiso=findViewById(R.id.user_dashboard_to_discussion);
        toDiso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent todisco=new Intent(getApplicationContext(),discussion_home.class);
                startActivity(todisco);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });


        //Hooks
        featuredRecycler = findViewById(R.id.featured_recycler);
        mostViewedRecycler = findViewById(R.id.most_viewed_recycler);
        categoriesRecycler = findViewById(R.id.categories_recycler);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);
        //menu hooks
        drawerLayout = findViewById(R.id.drawer_layout);

        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //codefor hi + userName
        navigationView = findViewById(R.id.navigation_view);
        final View header = navigationView.getHeaderView(0);
        final TextView app_nameEt = header.findViewById(R.id.app_name);
        final TextView mail_id = header.findViewById(R.id.mail_id);
        final CircleImageView pro_img = header.findViewById(R.id.pro_img);



        //course hooks
    //dont use below one
//        cseIcon = findViewById(R.id.cse);
//        cseIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), CourseOverview.class));
//            }
//        });

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

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    Glide.with(header.getContext()).load(user.getProfileimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(pro_img);
                    fullName = user.getFullName();
                    email = user.getEmail();
                    app_nameEt.setText("Hi !\n" + fullName);
                    mail_id.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserDashboard.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

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
               startActivity(account_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.nav_discussion:
                Intent intent = new Intent(UserDashboard.this, discussion_home.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.nav_home:
                Intent intent1 = new Intent(UserDashboard.this,UserDashboard.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.nav_contact_us:

                String[] redukt_mail={"teamredukt@gmail.com"};
                String mailSubject="Help needed for "+fullName.toUpperCase();
                String chooseOne="Choose one email application";
                Intent mailIntent=new Intent(Intent.ACTION_SENDTO);
                mailIntent.setData(Uri.parse("mailto:"));
                mailIntent.putExtra(Intent.EXTRA_EMAIL,redukt_mail);
                mailIntent.putExtra(Intent.EXTRA_SUBJECT,mailSubject);
                startActivity(Intent.createChooser(mailIntent,chooseOne));
                break;

            case R.id.nav_logout:
                View view = LayoutInflater.from(this).inflate(R.layout.logoutdialog,null);

                Button submit=view.findViewById(R.id.postl);
                Button canc=view.findViewById(R.id.cancell);

                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setView(view).setCancelable(false).create();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                canc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(UserDashboard.this, LoginActivity.class);
                        dialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                });
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

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(netWorkChangeListener);
        super.onStop();
    }

}
