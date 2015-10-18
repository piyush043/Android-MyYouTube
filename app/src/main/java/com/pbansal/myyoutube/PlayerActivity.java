package com.pbansal.myyoutube;

/**
 * Created by sprakash on 10/9/15.
 */

import android.os.Bundle;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeInitializationResult;

import android.widget.TextView;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubePlayer;


public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView playerView;
    private TextView videoTitle;
    private TextView videoDescription;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_player);
        videoTitle = (TextView)findViewById(R.id.video_title);
        videoDescription = (TextView) findViewById(R.id.video_description);
        playerView = (YouTubePlayerView)findViewById(R.id.player_view);
        playerView.initialize(YoutubeConnect.KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(!b){
            youTubePlayer.cueVideo(getIntent().getStringExtra("VIDEO_ID"));
            videoTitle.setText(getIntent().getStringExtra("VIDEO_TITLE"));
            videoDescription.setText(getIntent().getStringExtra("VIDEO_DESCRIPTION"));
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Initialization Failed", Toast.LENGTH_LONG).show();
    }
}