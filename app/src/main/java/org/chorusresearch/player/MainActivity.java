/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.protyposis.android.mediaplayer.MediaPlayer;
import net.protyposis.android.mediaplayer.VideoView;
import net.protyposis.android.mediaplayer.dash.ChorusAdaptationLogic;
import net.protyposis.android.mediaplayer.dash.DashSource;

import org.chorusresearch.player.transport.RedactedUrl;
import org.chorusresearch.player.transport.TransportClient;
import org.chorusresearch.player.transport.http.HttpUrlConnectionTransportClient;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public final class MainActivity extends Activity {
    private static final String TAG = "ChorusPlayer";
    private static final String STATE_URL = "mpd-url";

    private final ExecutorService loader = Executors.newSingleThreadExecutor();
    private final AtomicInteger playbackGeneration = new AtomicInteger();
    private EditText urlInput;
    private TextView status;
    private Button play;
    private VideoView videoView;
    private TransportClient transportClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlInput = findViewById(R.id.mpd_url);
        status = findViewById(R.id.status);
        play = findViewById(R.id.play);
        videoView = findViewById(R.id.video);

        String savedUrl = savedInstanceState == null
                ? null
                : savedInstanceState.getString(STATE_URL, "");
        urlInput.setText(MpdUrlDefaults.initialValue(savedUrl, BuildConfig.DEFAULT_MPD_URL));

        videoView.setOnPreparedListener(player -> {
            play.setEnabled(true);
            status.setText(R.string.status_playing);
            videoView.start();
        });
        videoView.setOnErrorListener((player, what, extra) -> {
            play.setEnabled(true);
            status.setText(R.string.status_playback_failed);
            return true;
        });

        play.setOnClickListener(view -> startPlayback());
        findViewById(R.id.stop).setOnClickListener(view -> stopPlayback());
    }

    private void startPlayback() {
        final URI uri;
        try {
            uri = MpdUrlValidator.requireHttpsMpd(urlInput.getText().toString());
        } catch (IllegalArgumentException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        stopPlayback();
        play.setEnabled(false);
        status.setText(R.string.status_loading);
        transportClient = new HttpUrlConnectionTransportClient();
        TransportClient client = transportClient;
        int generation = playbackGeneration.get();

        loader.execute(() -> {
            try {
                ChorusAdaptationLogic adaptationLogic = new ChorusAdaptationLogic(client);
                DashSource source = new DashSource(
                        getApplicationContext(), Uri.parse(uri.toString()), client, adaptationLogic);
                runOnUiThread(() -> {
                    if (generation == playbackGeneration.get() && client == transportClient) {
                        videoView.setVideoSource(source);
                    }
                });
            } catch (RuntimeException exception) {
                Log.e(TAG, "Unable to load " + RedactedUrl.describe(uri)
                        + " (" + exception.getClass().getSimpleName() + ")");
                runOnUiThread(() -> {
                    if (generation == playbackGeneration.get()) {
                        play.setEnabled(true);
                        status.setText(R.string.status_load_failed);
                    }
                });
            }
        });
    }

    private void stopPlayback() {
        playbackGeneration.incrementAndGet();
        if (videoView != null) videoView.stopPlayback();
        if (transportClient != null) {
            transportClient.close();
            transportClient = null;
        }
        if (status != null) status.setText(R.string.status_stopped);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_URL, urlInput.getText().toString());
    }

    @Override
    protected void onDestroy() {
        stopPlayback();
        loader.shutdownNow();
        super.onDestroy();
    }
}
