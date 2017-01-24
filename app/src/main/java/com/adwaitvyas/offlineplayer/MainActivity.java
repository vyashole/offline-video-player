package com.adwaitvyas.offlineplayer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;

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
        //videoUrlView.setText(R.string.hardcoded_video_url_2);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_menu:
                new DeleteTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteCache() {
        try {
            File dir = getCacheDir();
            deleteDir(dir);
        } catch (Exception ignored) {}
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }

    class DeleteTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle(getString(R.string.deleting_videos));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... nothing) {
            deleteCache();
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            progressDialog.dismiss();
        }
    };
}
