package com.adwaitvyas.offlineplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;
import android.widget.Toast;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    @BindView(R.id.videoView) PlayerView playerView;
    @BindView(R.id.textView) TextView status;
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
        if(cacheServer.isCached(videoUrl)) {
            status.setText(R.string.playing_offline);
        }
        else cacheServer.registerCacheListener(new CacheListener() {
            @Override
            public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
                status.setText(getString(R.string.cache_status,percentsAvailable));
                if(percentsAvailable == 100) status.setText(R.string.playing_offline);
            }
        }, videoUrl);
    }

    @Override
    protected void onStart() {
        super.onStart();
        createPlayer();
        playerView.setPlayer(player);
        player.seekTo(position);
        preparePlayer(true);
        initPlayerListner();
    }

    private void createPlayer() {
        // Create a default TrackSelector
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(this).build();
        AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(this, videoTrackSelectionFactory);
        // Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();
        // Create the player
        player = new SimpleExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl)
                .setBandwidthMeter(bandwidthMeter)
                .build();
    }

    private void preparePlayer(boolean play) {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(this).build();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "OfflinePlayer"), bandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaItem item = new MediaItem.Builder()
                .setUri(Uri.parse(proxyVideoUrl))
                .build();

        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory)
                .createMediaSource(item);

        player.setPlayWhenReady(play);
        player.setMediaSource(videoSource);
        player.prepare();
    }

    private void initPlayerListner() {

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Toast.makeText(PlayerActivity.this,R.string.error_generic, Toast.LENGTH_LONG).show();
                Player.Listener.super.onPlayerError(error);
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
