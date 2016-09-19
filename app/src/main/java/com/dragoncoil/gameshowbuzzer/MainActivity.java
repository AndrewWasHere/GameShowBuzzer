package com.dragoncoil.gameshowbuzzer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_HOST = "com.dragoncoil.gameshowbuzzer.HOST";
    public final static String EXTRA_PLAYER = "com.dragoncoil.gameshowbuzzer.PLAYER";

    private String host = null;
    private String player = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPreferences();

        Intent intent = getIntent();
        String newHost = intent.getStringExtra(SetupActivity.EXTRA_HOST);
        if (newHost != null) {
            host = newHost;
        }

        String newPlayer = intent.getStringExtra(SetupActivity.EXTRA_PLAYER);
        if (newPlayer != null) {
            player = newPlayer;
        }

        if (host == null && player == null) {
            loadPreferences();
        } else {
            savePreferences();
        }
    }

    private void loadPreferences() {
        SharedPreferences p = this.getPreferences(Context.MODE_PRIVATE);
        host = p.getString(EXTRA_HOST, "");
        player = p.getString(EXTRA_PLAYER, "");
    }

    private void savePreferences() {
        SharedPreferences p = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor e = p.edit();
        e.putString(EXTRA_HOST, host);
        e.putString(EXTRA_PLAYER, player);
        e.apply();
    }

    public void onSetupClick(View view) {
        Intent intent = new Intent(this, SetupActivity.class);
        intent.putExtra(EXTRA_HOST, host);
        intent.putExtra(EXTRA_PLAYER, player);
        startActivity(intent);
    }

    public void onPlayClick(View view) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra(EXTRA_HOST, host);
        intent.putExtra(EXTRA_PLAYER, player);
        startActivity(intent);
    }
}
