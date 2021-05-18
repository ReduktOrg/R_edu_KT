package com.example.r_edu_kt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.r_edu_kt.Adapters.PostAdapter;
import com.example.r_edu_kt.Model.Post;
import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.User.MyAccount.MyAccount;
import com.example.r_edu_kt.User.UserDashboard;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import soup.neumorphism.NeumorphCardView;


public class discussion_home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer_layout;
    private FloatingActionButton fab;
    Context mcontext;

    private RecyclerView recyclerView;
    private ProgressBar progress_circular;
    ImageView menuIcon,search,search2;
    NavigationView navigationView;
    EditText et;
    NeumorphCardView cardView;
    CircleImageView pro_img;
    String s="";
    static final float END_SCALE = 0.7f;

    String fullName,email;

    private PostAdapter postAdapter;
    private List<Post> postList;

    LinearLayout contentView;

    int [] animList = {R.anim.layout_animation};
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_home);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }
        fab = findViewById(R.id.fab);
        contentView=findViewById(R.id.content);
        menuIcon = findViewById(R.id.menu_icon);
        search=findViewById(R.id.search);
        search2=findViewById(R.id.search2);
        et=findViewById(R.id.searchtext);
        cardView=findViewById(R.id.card);
        drawer_layout=findViewById(R.id.drawer_layout);


        progress_circular=findViewById(R.id.progress_circular);
        recyclerView=findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(linearLayoutManager);


        navigationView = findViewById(R.id.nav_view);
        final View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        final TextView app_nameEt=header.findViewById(R.id.app_name);
        final TextView mail_id = header.findViewById(R.id.mail_id);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                fullName = user.getFullName();
                //Glide.with(mcontext).load(user.getProfileimage()).into(pro_img);
                email=user.getEmail();
                app_nameEt.setText("Hi !\n"+fullName);
                mail_id.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(discussion_home.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });



        // ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer_layout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        // drawer_layout.addDrawerListener(toggle);
        // toggle.syncState();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(discussion_home.this,AskQuestionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation animation = AnimationUtils.loadAnimation(discussion_home.this,R.anim.bounce);
                BounceInterpolator interpolator = new BounceInterpolator();
                animation.setInterpolator(interpolator);
                search.startAnimation(animation);
                cardView.setVisibility(View.VISIBLE);
                search2.setVisibility(View.VISIBLE);
                et.requestFocus();
                s=et.getText().toString().toLowerCase();
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("questions posts");
                Query query=reference.orderByChild("topic").startAt(s).endAt(s+"\uf8ff");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postList.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Post post=dataSnapshot.getValue(Post.class);
                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });


        search2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s="";
                cardView.setVisibility(View.GONE);
                et.setText("");
                search2.setVisibility(View.GONE);
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("questions posts");
                Query query=reference.orderByChild("topic").startAt(s).endAt(s+"\uf8ff");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postList.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Post post=dataSnapshot.getValue(Post.class);
                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });



        naviagationDrawer();

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(discussion_home.this,postList);
        recyclerView.setAdapter(postAdapter);
        readQuestionsPosts();

    }

    private void naviagationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_discussion);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerVisible(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);
                else drawer_layout.openDrawer(GravityCompat.START);
            }
        });

        animateNavigateDrawer();
    }

    private void animateNavigateDrawer() {
        drawer_layout.setScrimColor(getResources().getColor(R.color.colorAccent));
        drawer_layout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
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

    private void readQuestionsPosts() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("questions posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post= dataSnapshot.getValue(Post.class);
                    postList.add(post);
                }
                final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(discussion_home.this,animList[i]);
                recyclerView.setLayoutAnimation(controller);
                postAdapter.notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();
                progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(discussion_home.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_discussion:
                Intent intent = new Intent(discussion_home.this, discussion_home.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.nav_home:
                Intent intent1 = new Intent(discussion_home.this,UserDashboard.class);
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

    @Override
    public void onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
}