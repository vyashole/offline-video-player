package com.adwaitvyas.offlineplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    @BindView(R.id.videoView) SimpleExoPlayerView simpleExoPlayerView;
    private String videoUrl , proxyVideoUrl;
    long position = 0L;


    // Activity onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        videoUrl = getIntent().getStringExtra("videoUrl");
        HttpProxyCacheServer cacheServer = OfflinePlayerApplication.getCacheServer(this);
        proxyVideoUrl = cacheServer.getProxyUrl(videoUrl, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        createPlayer();
        simpleExoPlayerView.setPlayer(player);
        player.seekTo(position);
        preparePlayer(true);
        initPlayerListner();
    }

    private void createPlayer() {
        // Create a default TrackSelector
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        // Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();
        // Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
    }

    private void preparePlayer(boolean play) {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "OfflinePlayer"), bandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(proxyVideoUrl),
                dataSourceFactory, extractorsFactory, null, null);
        // Prepare the player with the source and play when ready
        player.setPlayWhenReady(play);
        player.prepare(videoSource);
    }

    private void initPlayerListner() {
        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(PlayerActivity.this,R.string.error_generic, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPositionDiscontinuity() {

            }
        });
    }

    @Override
    protected void onStop() {
        position = player.getCurrentPosition();
        player.release();
        super.onStop();
    }

    public static Intent getStartingIntent(Context context, String videoUrl) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("videoUrl", videoUrl);
        return intent;
    }


}
