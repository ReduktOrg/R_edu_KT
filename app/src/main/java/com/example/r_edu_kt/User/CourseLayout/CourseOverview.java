package com.example.r_edu_kt.User.CourseLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.CourseLayout.Fragments.FragmentAdapter;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

public class CourseOverview extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;
    ImageView img;
    TextView title, desc;

    //Exoplayer
    PlayerView playerView;
    ProgressBar progressBar;
    ImageView btFullScreen;
    SimpleExoPlayer simpleExoPlayer;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_overview);


        setUpExoPlayer("https://i.imgur.com/7bMqysJ.mp4");

        //hooks
        tabLayout = findViewById(R.id.tab_layout);
        pager2 = findViewById(R.id.view_pager2);

        img = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title);
//        desc=(TextView)findViewById(R.id.description);
        img.setImageResource(getIntent().getIntExtra("courseimage", 0));
        title.setText(getIntent().getStringExtra("title").toString());
//        desc.setText(getIntent().getStringExtra("description").toString());

        FragmentManager fm = getSupportFragmentManager();


        adapter = new FragmentAdapter(fm, getLifecycle(), title.getText().toString());
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


    public void setUpExoPlayer(String url){

        releasePlayer();

//        Toast.makeText(this,"setting up player",Toast.LENGTH_LONG).show();

        //Exoplayer
        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.video_progress_bar);
        btFullScreen = playerView.findViewById(R.id.bt_fullscreen);
        Uri videoUrl = Uri.parse(url);

//        return;
        LoadControl loadControl = new DefaultLoadControl();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

        //Initialise simple exo player
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector, loadControl);



        //Initialise data source factory
        DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory("exoplayer_video");
        //Initialise extrafactors factory
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoUrl, factory, extractorsFactory, null, null);

        //set player
        playerView.setPlayer(simpleExoPlayer);
        //keep screen on
        playerView.setKeepScreenOn(true);
        //prepare media
        simpleExoPlayer.prepare(mediaSource);
        //play video when ready
        simpleExoPlayer.setPlayWhenReady(true);

        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                //check condition
                if (playbackState == Player.STATE_BUFFERING) {
                    //when buffering
                    //show progressbar
                    progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    //when ready
                    //Hide progress Bar
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        btFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    //When flag is true
                    //set enter full screen
                    btFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen));
                    //set portrait orientation
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    playerView.getLayoutParams().height=400;
                    //set falg value is false
                    flag=false;

                    //remove full screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                else {
                    //when flag is false
                    //set exit full screen image
                    btFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit));
                    //set landscape orientation
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    playerView.getLayoutParams().height=1000;
                    //set flag true
                    flag=true;

                    //make activity full screen
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        //stop video when ready
        simpleExoPlayer.setPlayWhenReady(false);
        //get playback state
        simpleExoPlayer.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //play video when ready
        simpleExoPlayer.setPlayWhenReady(true);
        //get playback state
        simpleExoPlayer.getPlaybackState();
    }
    private void releasePlayer(){
        if(simpleExoPlayer!=null){
            simpleExoPlayer.release();
            simpleExoPlayer.clearVideoSurface();
            playerView.getPlayer().release();;


            simpleExoPlayer= null;
            playerView=null;
        }
    }
}