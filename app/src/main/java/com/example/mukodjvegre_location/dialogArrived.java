package com.example.mukodjvegre_location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.os.Vibrator;
import android.support.v7.app.AppCompatDialogFragment;

public class dialogArrived extends AppCompatDialogFragment {

    public void playAlarmSound(MediaPlayer mySong){
        //MediaPlayer mySong = MediaPlayer.create(getContext(), R.raw.alarmSound);
        mySong.start();
        mySong.setLooping(true);
    }
    public void stopAlarmSound(MediaPlayer mySong){
        mySong.stop();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        //Log.d(TAG, "opening dialogue");
        AlertDialog.Builder arrivedDialogue = new AlertDialog.Builder(getActivity());
        arrivedDialogue.setTitle("Ébresztő!");


        String targetName = getArguments().getString("targetName"); //a celpont neve
        arrivedDialogue.setMessage("Megérkeztél ide: " + targetName + "!"); //megerkeztel + celpont neve


        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        final Ringtone r = RingtoneManager.getRingtone(getContext(), alert);
        r.setStreamType(AudioManager.STREAM_ALARM);


        final AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int volume=getArguments().getInt("volume"); //celpontra beallitott hangero
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);



        /*final Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long time[] = new long[3];
            time[0] = 0;
            time[1]= 100;
            time[2]= 1000;
            v.vibrate(VibrationEffect.createWaveform(time, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }*/

        r.play();
        //r.setLooping(true);

        arrivedDialogue.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                r.stop();
                //v.cancel();
            }
        });
        return arrivedDialogue.create();
    }
}
