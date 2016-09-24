package com.dragoncoil.gameshowbuzzer;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PlayActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = "PlayActivity";
    private String host = null;
    private String player = null;
    private MediaPlayer mediaPlayer = null;
    private Button notifyButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        createMediaPlayer();

        Intent intent = getIntent();
        host = intent.getStringExtra(MainActivity.EXTRA_HOST);
        player = intent.getStringExtra(MainActivity.EXTRA_PLAYER).replace(" ", "_");

        notifyButton = (Button) findViewById(R.id.notify_button);
        notifyButton.setOnTouchListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        destroyMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyMediaPlayer();
    }

    private void createMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.cowbell);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.v(TAG, "MediaPlayer errored.");
                Log.v(TAG, "  what=" + what);
                Log.v(TAG, "  extra=" + extra);
                Log.v(TAG, "Nuking media player.");
                destroyMediaPlayer();

                return true;
            }
        });
    }
    private void destroyMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                notifyHost();
                break;
            case MotionEvent.ACTION_UP:
                reset();
                break;
            default:
                return false;
        }

        return true;
    }

    private void notifyHost() {
        Log.v(TAG, "notify host");
        Button notifyButton = (Button) findViewById(R.id.notify_button);
        notifyButton.setBackgroundColor(Color.RED);
        sendToHost();
    }

    private void sendToHost() {
        class SendToHostTask extends AsyncTask<Integer, Integer, Boolean> {
            @Override
            protected Boolean doInBackground(Integer... integers) {
                String TAG = "SendToHostTask";
                boolean winner = false;
                try {
                    URL url = new URL("http://" + host + "/players/" + player);
                    Log.v(TAG, url.toString());
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("POST");

                    int responseCode = c.getResponseCode();
                    Log.v(TAG, "HTTP response code = " + responseCode);
                    BufferedReader in = new BufferedReader(
                        new InputStreamReader(c.getInputStream())
                    );
                    String input;
                    StringBuilder response = new StringBuilder();

                    while ((input = in.readLine()) != null) {
                        response.append(input);
                    }
                    in.close();

                    winner = response.toString().contains("true");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return winner;
            }

            @Override
            protected void onPostExecute(Boolean winner) {
                if (winner) {
                    activate();
                }
            }
        }

        new SendToHostTask().execute(0);
    }

    private void reset() {
        notifyButton.setBackgroundColor(Color.LTGRAY);
    }

    private void activate() {
        class FlashButtonTask extends AsyncTask<Integer, Integer, Integer> {
            @Override
            protected Integer doInBackground(Integer... colors) {
                for (int i = 0; i < 2; ++i) {
                    for (int c : colors) {
                        publishProgress(c);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return Color.LTGRAY;
            }

            @Override
            protected void onProgressUpdate(Integer... colors) {
                notifyButton.setBackgroundColor(colors[0]);
            }

            @Override
            protected void onPostExecute(Integer color) {
                reset();
            }
        }

        if (mediaPlayer == null) {
            Log.v(TAG, "Re-creating media player.");
            createMediaPlayer();
        }
        mediaPlayer.start();
        new FlashButtonTask().execute(
            Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN
        );
    }
}
