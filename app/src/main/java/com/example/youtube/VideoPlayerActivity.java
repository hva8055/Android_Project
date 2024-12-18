package com.example.youtube;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class VideoPlayerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer player;
    private ImageButton fullscreenButton;
    private TextView videoTitle, videoDescription;
    private boolean isFullscreen = false;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.playerView);
        fullscreenButton = findViewById(R.id.fullscreen_button);
        videoTitle = findViewById(R.id.video_title);
        videoDescription = findViewById(R.id.video_description);
        linearLayout = findViewById(R.id.linear);

        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Load video URL from intent
        String videoUrl = getIntent().getStringExtra("video_url");
        String title = getIntent().getStringExtra("video_title");
        String description = getIntent().getStringExtra("video_description");

        // Set video title and description
        videoTitle.setText(title);
        videoDescription.setText(description);

        if (videoUrl != null) {
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }

        // Handle fullscreen toggle
        fullscreenButton.setOnClickListener(view -> toggleFullscreen());
    }

    private void toggleFullscreen() {
        if (!isFullscreen) {
            // Enter Fullscreen
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            // Set PlayerView to fullscreen
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            playerView.setLayoutParams(params);
            playerView.requestLayout(); // Force layout refresh

            // Hide title and description
            linearLayout.setVisibility(View.GONE);
            fullscreenButton.setImageResource(R.drawable.baseline_fullscreen_exit_24);

            isFullscreen = true;
        } else {
            // Exit Fullscreen
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            // Reset PlayerView to normal size
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    250 // Default height
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            playerView.setLayoutParams(params);
            playerView.requestLayout(); // Force layout refresh

            // Show title and description
            linearLayout.setVisibility(View.VISIBLE);
            fullscreenButton.setImageResource(R.drawable.baseline_fullscreen_exit_24);

            isFullscreen = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
