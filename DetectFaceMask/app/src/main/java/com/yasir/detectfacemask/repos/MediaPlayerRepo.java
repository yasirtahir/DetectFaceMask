package com.yasir.detectfacemask.repos;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerRepo {

    private static MediaPlayer mediaPlayer;
    private static int count = 0;

    public static void playSound(Context activity, Integer fileName){
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(activity, fileName);
        }
        count ++;
        if(count > 5 && !mediaPlayer.isPlaying()){
            count = 0;
            mediaPlayer.start();
        }
    }

    public static void stopSound(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            count = 0;
            mediaPlayer.stop();
        }
    }
}
