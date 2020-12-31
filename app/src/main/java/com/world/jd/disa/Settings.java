package com.world.jd.disa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void voicechng(View view) {
        Intent vchng = new Intent();
        vchng.setAction("com.android.settings.TTS_SETTINGS");
        vchng.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(vchng);
    }

    public void sendAbout(View view) {
        Intent aboutIntent = new Intent(getApplicationContext(),About.class);
        startActivity(aboutIntent);
    }
}