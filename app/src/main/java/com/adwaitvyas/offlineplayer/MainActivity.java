package com.adwaitvyas.offlineplayer;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.videoUrlView) TextInputEditText videoUrlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        videoUrlView.setText(R.string.hardcoded_video_url);
    }

    @OnClick(R.id.btnPlay)
    void onClickPlay() {
        String videoUrl = videoUrlView.getText().toString().trim();
        if(videoUrl.isEmpty() || !videoUrl.startsWith("http")) {
            Toast.makeText(this, R.string.error_invalid_input, Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(PlayerActivity.getStartingIntent(this,videoUrl));
    }
}
