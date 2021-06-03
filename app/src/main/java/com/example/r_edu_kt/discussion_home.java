package com.example.r_edu_kt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.r_edu_kt.Adapters.PostAdapter;
import com.example.r_edu_kt.Model.Post;
import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.User.Login.LoginActivity;
import com.example.r_edu_kt.User.MyAccount.MyAccount;
import com.example.r_edu_kt.User.UserDashboard;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import soup.neumorphism.NeumorphCardView;


public class discussion_home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer_layout;
    private FloatingActionButton fab;

    private RecyclerView recyclerView;
    private ProgressBar progress_circular, progress;
    ImageView menuIcon,search,search2,up;
    NavigationView navigationView;
    EditText et;
    TextView load_more;
    NeumorphCardView cardView;
    String s="";
    static final float END_SCALE = 0.7f;
    Boolean total_items_loaded = false;

    private static int total_items_to_load = 7;
    private int mcurrentpage = 1,count = 0;

    String fullName,email;
    SwipeRefreshLayout splayout;

    private PostAdapter postAdapter;
    private List<Post> postList;

    LinearLayout contentView;

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
        splayout = findViewById(R.id.splayout);
        load_more = findViewById(R.id.load_more);
        progress = findViewById(R.id.progress);
        up=findViewById(R.id.up);

        progress_circular=findViewById(R.id.progress_circular);
        recyclerView=findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        navigationView = findViewById(R.id.nav_view);
        final View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        final TextView app_nameEt=header.findViewById(R.id.app_name);
        final TextView mail_id = header.findViewById(R.id.mail_id);
        final CircleImageView pro_img = header.findViewById(R.id.pro_img);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                fullName = user.getFullName();
                Glide.with(header.getContext()).load(user.getProfileimage()).into(pro_img);
                email=user.getEmail();
                app_nameEt.setText("Hi !\n"+fullName);
                mail_id.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(discussion_home.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcurrentpage++;
                load_more.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                readQuestionsPosts();
            }
        });


        splayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postList.clear();
                readQuestionsPosts();
                recyclerView.scrollToPosition(postList.size() - ((mcurrentpage -1 ) * total_items_to_load));
            }
        });

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
                String val=et.getText().toString();
                s = StringUtils.capitalize(val);
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && !total_items_loaded){
                    load_more.setVisibility(View.VISIBLE);
                    up.setVisibility(View.VISIBLE);
                }
                else if(total_items_loaded){
                    load_more.setVisibility(View.GONE);
                    up.setVisibility(View.VISIBLE);
                }
                if(!recyclerView.canScrollVertically(-1)){
                    up.setVisibility(View.GONE);
                }
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(postList.size()-1);
            }
        });


        search2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.GONE);
                et.setText("");
                search2.setVisibility(View.GONE);
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("questions posts");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postList.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Post post= dataSnapshot.getValue(Post.class);
                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(discussion_home.this,error.getMessage(),Toast.LENGTH_SHORT).show();

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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("questions posts");

        Query question_query = reference.limitToLast(mcurrentpage * total_items_to_load);
        question_query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                postList.clear();
                count=0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    count++;
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                }
                postAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(postList.size() - ((mcurrentpage -1 ) * total_items_to_load));
                splayout.setRefreshing(false);
                progress.setVisibility(View.GONE);
                load_more.setVisibility(View.GONE);
                progress_circular.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(discussion_home.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(count >= snapshot.getChildrenCount()){
                    total_items_loaded = true;
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

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
                        Intent intent=new Intent(discussion_home.this, LoginActivity.class);
                        dialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                });
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