package com.example.mukodjvegre_location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class notificationBroadcastReceiver extends BroadcastReceiver {

    static MediaPlayer myPlayer;
    public static void init(MediaPlayer player){
        myPlayer = player;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if(action.equals("notification_cancelled"))
        {
            myPlayer.stop();
            Log.e("broadcastReceiver", "notification deleted");
        }
    }
}
