package com.example.a12thproject;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MusicService extends Service {





    private MediaPlayer mediaPlayer;

    private static boolean flag = false;

















    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!flag) {
            mediaPlayer = MediaPlayer.create(this, R.raw.music2);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            flag = true;
        } else {
            if(mediaPlayer != null)
                mediaPlayer.pause();
            flag = false;
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }


}
