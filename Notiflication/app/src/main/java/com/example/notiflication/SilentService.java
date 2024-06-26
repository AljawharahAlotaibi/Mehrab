package com.example.notiflication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;


public class SilentService extends BroadcastReceiver {
    private AudioManager audioManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context.getApplicationContext(), "Phone is switched off silent mode", Toast.LENGTH_SHORT).show();
        audioManager =(AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
}
