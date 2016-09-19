package com.dragoncoil.gameshowbuzzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SetupActivity extends AppCompatActivity {
    public final static String EXTRA_HOST = "com.dragoncoil.gameshowbuzzer.HOST";
    public final static String EXTRA_PLAYER = "com.dragoncoil.gameshowbuzzer.PLAYER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Intent intent = getIntent();
        String newHost = intent.getStringExtra(MainActivity.EXTRA_HOST);
        if (newHost != null) {
            EditText hostEditText = (EditText) findViewById(R.id.host_edit_text);
            hostEditText.setText(newHost);
        }

        String newPlayer = intent.getStringExtra(MainActivity.EXTRA_PLAYER);
        if (newPlayer != null) {
            EditText playerEditText = (EditText) findViewById(R.id.player_edit_text);
            playerEditText.setText(newPlayer);
        }
    }

    public void onOkClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        EditText hosturl = (EditText) findViewById(R.id.host_edit_text);
        EditText playername = (EditText) findViewById(R.id.player_edit_text);
        intent.putExtra(EXTRA_HOST, hosturl.getText().toString());
        intent.putExtra(EXTRA_PLAYER, playername.getText().toString());
        startActivity(intent);
    }
}
