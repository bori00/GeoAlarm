package com.example.mukodjvegre_location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class backgroundServiceRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(backgroundServiceRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        //context.startService(new Intent(context, backgroundService.class));;// in tutorial
        Intent serviceIntent = new Intent(context, backgroundService.class);
        int androidVersion = Build.VERSION.SDK_INT;
        //androidverziotol fugg
        context.startService(serviceIntent); //in tutorial
        /*if (androidVersion >= 8){
            // Do something for oreo and above versions
            ContextCompat.startForegroundService(context, serviceIntent );
        } else{
            context.startService(new Intent(context, backgroundService.class));;
            // do something for phones running an SDK before oreo
        }
        ContextCompat.startForegroundService(context, serviceIntent );*/
    }
}
